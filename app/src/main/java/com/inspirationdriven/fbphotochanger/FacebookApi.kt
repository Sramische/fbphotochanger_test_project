package com.inspirationdriven.fbphotochanger

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

fun getProfilePicture(desiredWidth: Int = 200): Single<String> =
        fbGetProfilePicture(desiredWidth)
                .map {
                    gson.fromJson<ObjectRoot<Url>>(it.rawResponse, object : TypeToken<ObjectRoot<Url>>() {}.type).data.url
                }

fun getPhotosOfMeAlbum(): Observable<Album> =
        fbGetPhotosOfMe()
                .map {
                    val list1 = gson.fromJson<ListRoot<Id>>(it.rawResponse, object : TypeToken<ListRoot<Id>>() {}.type)
                    val list = list1.data
                    list.count() to list.firstOrNull()?.id // array of imageIds
                }//Counting all the photos page by page, while carrying the very first image for the album thumbnail
                .scan { t1: Pair<Int, String?>, t2: Pair<Int, String?> -> (t1.first + t2.first) to t1.second }
                .last(0 to null)
                .flatMapObservable {
                    if (it.first == 0)
                        Observable.empty<Album>()
                    else
                        getSyntheticAlbum(AlbumMeta(null, "Photos of Me", it.first, User.me().photosUri, it.second), it.second!!)

                }

fun getAlbumPhotos(album: AlbumMeta, imgSize: Int): Observable<LargeImage> =
        fbGetAlbumPhotos(album.photosUri!!)
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
        fbGetAlbums()
                .map {
                    val root = gson.fromJson<ListRoot<PhotoCollection>>(it.rawResponse, object : TypeToken<ListRoot<PhotoCollection>>() {}.type)
                    root.data.map { com.inspirationdriven.fbphotochanger.model.Album(AlbumMeta(it.id, it.name, it.count), it.picture.data.url) }
                }

fun getAlbumsCombined() =
        getPhotosOfMeAlbum()
                .toList()
                .concatWith(getAlbums().single(listOf()))
                .collect({ mutableListOf<Album>() }, { t1, t2 -> t1.addAll(t2) })


private fun getSyntheticAlbum(albumMeta: AlbumMeta, imgId: String): Observable<Album> =
        fbGetImages(imgId).flatMapObservable {
            val picture = gson.fromJson<Picture>(it.rawResponse, object : TypeToken<Picture>() {}.type)
            val thumbnail = Thumbnail(picture.images.sorted().first { it.width >= DEFAULT_IMG_SIZE }.source)
            Observable.just(Album(albumMeta, thumbnail.thumbnail))
        }

const val DEFAULT_IMG_SIZE = 320
private val gson = Gson()