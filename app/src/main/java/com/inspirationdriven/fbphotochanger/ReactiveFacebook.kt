package com.inspirationdriven.fbphotochanger

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.inspirationdriven.fbphotochanger.model.fb.AlbumMeta
import com.inspirationdriven.fbphotochanger.model.fb.Picture
import com.inspirationdriven.fbphotochanger.model.fb.User
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import org.json.JSONArray
import org.json.JSONObject

fun getProfilePicture(desiredWidth: Int = 200) = GraphRequest(token,
        me.profilePictureUri)
        .addParams("redirect" to "false")
        .addParams { it.putInt("width", desiredWidth) }
        .toSingle()
        .map {
            (it.jsonObject["data"] as JSONObject).getString("url")
        }

fun getPhotosOfMe(): Single<Pair<AlbumMeta, Picture>> = GraphRequest(token, me.photosUri)
        .addParams("type" to "tagged")
        .toSingle()
        .map {
            val arr = it.jsonObject["data"] as JSONArray
            val imgId = if (arr.length() > 0) (arr[0] as JSONObject).getString("id") else null
            AlbumMeta(null, "Photos of Me", arr.length(), me.photosUri, imgId)
        }
        .flatMap { meta: AlbumMeta ->
            val picSingle = if (meta.explicitThumbnailUri != null)
                getPicture(meta.thumbnailUri, DEFAULT_IMG_SIZE)
            else
                Single.create<Picture> { it.onSuccess(Picture(null)) }

            picSingle.map { t ->
                meta to t
            }
        }

fun getAlbumPhotos(album: AlbumMeta, imgSize: Int): Observable<Pair<Picture, Picture>> = GraphRequest(token, album.photosUri).toSingle()
        .flatMapObservable { response: GraphResponse ->
            createObservable(response.jsonObject["data"] as JSONArray, {
                it.getString("id")
            })
        }.flatMap { getPictures(it, imgSize).toObservable() }

fun getAlbumList() =
        GraphRequest(token, me.albumsUri)
                .addParams("fields" to "photo_count,id,name")
                .toSingle()
                .flatMapObservable { response: GraphResponse ->
                    createObservable(response.jsonObject["data"] as JSONArray, {
                        AlbumMeta(
                                it.getString("id"),
                                it.getString("name"),
                                it.getInt("photo_count"))
                    })
                }

fun getAlbums(): Observable<Pair<AlbumMeta, Picture>> {
    return getAlbumList().flatMap({ t: AlbumMeta -> getAlbumPicture(t).toObservable() }, { t1: AlbumMeta, t2: Picture -> t1 to t2 })
}

private fun getPicture(fbId: String, imgSize: Int): Single<Picture> = GraphRequest(token, fbId)
        .addParams("fields" to "images")
        .toSingle().map {
            val imgList = it.jsonObject.getJSONArray("images")
                    .map {
                        it.getInt("width") to it.getString("source")
                    } as List<Pair<Int, String>>
            Picture(imgList.sortedBy { it.first }.first { it.first >= imgSize }.second)
        }

private fun getPictures(fbId: String, imgSize: Int): Single<Pair<Picture, Picture>> = GraphRequest(token, fbId)
        .addParams("fields" to "images")
        .toSingle().map {
            val imgList = it.jsonObject.getJSONArray("images")
                    .map {
                        it.getInt("width") to it.getString("source")
                    } as List<Pair<Int, String>>
            val sortedList = imgList.sortedBy { it.first }
            val thumb = Picture(sortedList.first { it.first >= imgSize }.second)
            val hiresImage = Picture(sortedList.last().second)
            thumb to hiresImage
        }

private fun getAlbumPicture(source: AlbumMeta) = GraphRequest(token, source.thumbnailUri)
        .addParams(
                "redirect" to "false",
                "type" to "album")
        .toSingle().map {
            Picture((it.jsonObject["data"] as JSONObject).getString("url"))
        }

private fun <T> createObservable(jsonArray: JSONArray, mapper: (JSONObject) -> T): Observable<T> {
    return Observable.create<T> {
        for (item in jsonArray) {
            with(item as JSONObject) {
                it.onNext(mapper.invoke(this))
            }
        }
        it.onComplete()
    }
}

private fun GraphRequest.toSingle() = Single.create { emitter: SingleEmitter<GraphResponse> ->
    val response = executeAndWait()
    val error = response.error
    if (error == null)
        emitter.onSuccess(response)
    else
        emitter.onError(error.exception)
}

private fun GraphRequest.addParams(vararg pairs: Pair<String, String>): GraphRequest {
    val paramBundle = this.parameters ?: Bundle()
    pairs.forEach { paramBundle.putString(it.first, it.second) }
    parameters = paramBundle
    return this
}

private fun GraphRequest.addParams(injector: (Bundle) -> Unit): GraphRequest {
    val paramBundle = this.parameters ?: Bundle()
    injector.invoke(paramBundle)
    parameters = paramBundle
    return this
}

private val me = User.me()
const val DEFAULT_IMG_SIZE = 320
private var token: AccessToken
    get() {
        return AccessToken.getCurrentAccessToken()
    }
    set(v) = TODO()

private fun JSONArray.map(function: (JSONObject) -> Any?): List<Any?> {
    return ArrayList<Any?>().apply {
        for (i in 0 until length())
            add(function.invoke(getJSONObject(i)))
    }
}

private operator fun JSONArray.iterator() = object : Iterator<Any> {
    private var i = 0
    override fun hasNext() = i < length()
    override fun next() = get(i++)
}