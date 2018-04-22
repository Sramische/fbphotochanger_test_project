package com.inspirationdriven.fbphotochanger.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.inspirationdriven.fbphotochanger.getAlbums
import com.inspirationdriven.fbphotochanger.getPhotosOfMe
import com.inspirationdriven.fbphotochanger.model.Album
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class AlbumsViewModel : ViewModel() {
    val albums = MutableLiveData<List<Album>>()

    fun fetchAlbums() {
        getPhotosOfMe()
                .toObservable()
                .zipWith(getAlbums(), BiFunction { t1: MutableList<Album>, t2: List<Album> ->
                    t1.apply { addAll(t2) }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .collect<MutableList<Album>>({ mutableListOf() }, { t1, t2 ->
                    t1.addAll(t2)
                })
                .subscribe({ albums.value = it }, { e -> e.printStackTrace() })
    }
}
