package com.inspirationdriven.fbphotochanger.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.inspirationdriven.fbphotochanger.getAlbums
import com.inspirationdriven.fbphotochanger.getPhotosOfMe
import com.inspirationdriven.fbphotochanger.model.Album
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AlbumsViewModel : ViewModel() {
    val albums = MutableLiveData<List<Album>>()

    fun fetchAlbums() {
        getAlbums()
                .startWith(getPhotosOfMe().toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { Album(it.first, it.second.thumbnail) }
                .toList()
                .subscribe({ albums.value = it }, { e -> e.printStackTrace() })
    }
}
