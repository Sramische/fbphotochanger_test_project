package com.inspirationdriven.fbphotochanger

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspirationdriven.fbphotochanger.model.LargeImage
import com.inspirationdriven.fbphotochanger.model.Thumbnail
import com.inspirationdriven.fbphotochanger.model.fb.AlbumMeta
import com.inspirationdriven.fbphotochanger.model.fb.User
import com.inspirationdriven.fbphotochanger.model.fb.gson.Album
import com.inspirationdriven.fbphotochanger.model.fb.gson.ListRoot
import com.inspirationdriven.fbphotochanger.model.fb.gson.Picture
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import org.json.JSONObject

fun getProfilePicture(desiredWidth: Int = 200) = GraphRequest(token,
        me.profilePictureUri)
        .addParams("redirect" to "false")
        .addParams { it.putInt("width", desiredWidth) }
        .toSingle()
        .map {
            (it.jsonObject["data"] as JSONObject).getString("url")
        }

class Id(val id: String)

fun getPhotosOfMe(): Single<Pair<AlbumMeta, Thumbnail>> = GraphRequest(token, me.photosUri)
        .addParams("type" to "tagged", "fields" to "id")
        .toPagedObservable()
        .map {
            val list = gson.fromJson<ListRoot<Id>>(it.rawResponse, object : TypeToken<ListRoot<Id>>() {}.type).data
            list.count() to list.firstOrNull()?.id
        }.scan { t1: Pair<Int, String?>, t2: Pair<Int, String?> -> (t1.first + t2.first) to t1.second }
        .map { AlbumMeta(null, "Photos of Me", it.first, me.photosUri, it.second) }
        .last(AlbumMeta("", "Photos of Me", 0))
        .flatMap { meta: AlbumMeta ->
            val picSingle = if (meta.explicitThumbnailUri != null)
                getPicture(meta.thumbnailUri, DEFAULT_IMG_SIZE)
            else
                Single.create<Thumbnail> { it.onSuccess(Thumbnail(null)) }

            picSingle.map { t ->
                meta to t
            }
        }


fun getAlbumPhotos(album: AlbumMeta, imgSize: Int): Observable<LargeImage> = GraphRequest(token, album.photosUri)
        .addParams("fields" to "images")
        .toPagedObservable()
        .flatMap {
            val root = gson.fromJson<ListRoot<Picture>>(it.rawResponse, object : TypeToken<ListRoot<Picture>>() {}.type)
            Observable.fromArray(*root.data.toTypedArray())
        }
        .map {
            with(it.images) {
                sort()
                LargeImage(first { it.width >= imgSize }.source, last().source)
            }
        }

fun getAlbums(): Observable<Pair<AlbumMeta, Thumbnail>> =
        GraphRequest(token, me.albumsUri)
                .addParams("fields" to "photo_count,id,name,picture{url}")
                .toPagedObservable()
                .flatMap {
                    val root = gson.fromJson<ListRoot<Album>>(it.rawResponse, object : TypeToken<ListRoot<Album>>() {}.type)
                    Observable.fromArray(root.data)
                }.flatMapIterable { it -> it }
                .map {
                    AlbumMeta(it.id, it.name, it.count) to Thumbnail(it.picture.data.url)
                }

private fun getPicture(fbId: String, imgSize: Int): Single<Thumbnail> = GraphRequest(token, fbId)
        .addParams("fields" to "images")
        .toSingle().map {
            val picture = gson.fromJson<Picture>(it.rawResponse, object : TypeToken<Picture>() {}.type)
            Thumbnail(picture.images.sorted().first { it.width >= imgSize }.source)
        }

private fun GraphRequest.toPagedObservable(): Observable<GraphResponse> = this.toSingle()
        .toObservable()
        .concatMap {
            val next = it.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT)
            if (next == null)
                Observable.just(it)
            else
                Observable.just(it).concatWith(next.toPagedObservable())
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

private val gson = Gson()
private val me = User.me()
const val DEFAULT_IMG_SIZE = 320
private var token: AccessToken
    get() {
        return AccessToken.getCurrentAccessToken()
    }
    set(v) = TODO()
