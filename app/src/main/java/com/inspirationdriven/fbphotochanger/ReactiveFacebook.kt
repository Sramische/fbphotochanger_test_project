package com.inspirationdriven.fbphotochanger

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.inspirationdriven.fbphotochanger.model.fb.User
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleEmitter

fun fbGetProfilePicture(desiredWidth: Int): Single<GraphResponse> =
        GraphRequest(token, me.profilePictureUri)
                .addParams("redirect" to "false")
                .addParams { it.putInt("width", desiredWidth) }
                .toSingle()

fun fbGetPhotosOfMe(): Observable<GraphResponse> =
        GraphRequest(token, me.photosUri)
                .addParams("type" to "tagged", "fields" to "id")
                .toPagedObservable()

fun fbGetAlbumPhotos(photosUri: String): Observable<GraphResponse> =
        GraphRequest(token, photosUri)
                .addParams("fields" to "images")
                .toPagedObservable()

fun fbGetAlbums(): Observable<GraphResponse> =
        GraphRequest(token, me.albumsUri)
                .addParams("fields" to "photo_count,id,name,picture{url}")
                .toPagedObservable()

fun fbGetImages(fbId: String): Single<GraphResponse> =
        GraphRequest(token, fbId)
                .addParams("fields" to "images")
                .toSingle()

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

private val me = User.me()
private var token: AccessToken
    get() {
        return AccessToken.getCurrentAccessToken()
    }
    set(v) = TODO()