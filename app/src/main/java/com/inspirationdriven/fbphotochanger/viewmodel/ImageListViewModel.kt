package com.inspirationdriven.fbphotochanger.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.res.Resources
import com.inspirationdriven.fbphotochanger.getAlbumPhotos
import com.inspirationdriven.fbphotochanger.model.LargeImage
import com.inspirationdriven.fbphotochanger.model.fb.AlbumMeta
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ImageListViewModel : ViewModel() {
    val data = MutableLiveData<List<LargeImage>>()

    fun getCollection(albumMeta: AlbumMeta): LiveData<List<LargeImage>> {
        data.value = null
        getAlbumPhotos(albumMeta, SCREEN_WIDTH / 3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .collect<MutableList<LargeImage>>({ mutableListOf() }, { t1, t2 ->
                    t1.add(t2)
                    data.value = t1
                })
                .subscribe { t1: MutableList<LargeImage>?, t2: Throwable? -> data.value = t1 }
        return data
    }

    private val SCREEN_WIDTH = Resources.getSystem().displayMetrics.widthPixels
}