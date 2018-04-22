package com.inspirationdriven.fbphotochanger

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inspirationdriven.fbphotochanger.model.Album
import com.inspirationdriven.fbphotochanger.model.LargeImage
import com.inspirationdriven.fbphotochanger.model.Thumbnail
import com.inspirationdriven.fbphotochanger.model.fb.AlbumMeta
import com.inspirationdriven.fbphotochanger.model.fb.User
import com.inspirationdriven.fbphotochanger.model.fb.gson.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter

fun getProfilePicture(desiredWidth: Int = 200) = GraphRequest(token,
        me.profilePictureUri)
        .addParams("redirect" to "false")
        .addParams { it.putInt("width", desiredWidth) }
        .toSingle()
        .map {
            gson.fromJson<ObjectRoot<Url>>(it.rawResponse, object : TypeToken<ObjectRoot<Url>>() {}.type).data.url
        }

fun getPhotosOfMe(): Single<MutableList<Album>> = GraphRequest(token, me.photosUri)
        .addParams("type" to "tagged", "fields" to "id")
        .toPagedObservable()
        .map {
            val list = gson.fromJson<ListRoot<Id>>(it.rawResponse, object : TypeToken<ListRoot<Id>>() {}.type).data
            list.count() to list.firstOrNull()?.id
        }
        .scan { t1: Pair<Int, String?>, t2: Pair<Int, String?> -> (t1.first + t2.first) to t1.second }
        .last(0 to null)
        .flatMap {
            if (it.second.isNullOrEmpty())
                Single.just(mutableListOf())
            else
                getPicture(it.second!!, DEFAULT_IMG_SIZE).map { thumbnail ->
                    mutableListOf(Album(AlbumMeta(null, "Photos of Me", it.first, me.photosUri, it.second), thumbnail.thumbnail))
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

fun getAlbums(): Observable<List<com.inspirationdriven.fbphotochanger.model.Album>> =
        GraphRequest(token, me.albumsUri)
                .addParams("fields" to "photo_count,id,name,picture{url}")
                .toPagedObservable()
                .map {
                    //"{data:[]}"
                    val root = gson.fromJson<ListRoot<PhotoCollection>>(it.rawResponse, object : TypeToken<ListRoot<PhotoCollection>>() {}.type)
                    root.data.map { com.inspirationdriven.fbphotochanger.model.Album(AlbumMeta(it.id, it.name, it.count), it.picture.data.url) }
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
