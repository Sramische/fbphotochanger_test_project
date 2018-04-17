package com.inspirationdriven.fbphotochanger.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.inspirationdriven.fbphotochanger.getAlbumPhotos
import com.inspirationdriven.fbphotochanger.model.LargeImage
import com.inspirationdriven.fbphotochanger.model.fb.AlbumMeta
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ImageListViewModel : ViewModel(){
    val data = MutableLiveData<List<LargeImage>>()

    fun getCollection(a: AlbumMeta): LiveData<List<LargeImage>> {
        data.value = null
        getAlbumPhotos(a).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { LargeImage(it.first.url!!, it.second.url!!) }
                .toList()
                .subscribe{ t1: MutableList<LargeImage>?, t2: Throwable? ->  data.value = t1 }
        return data
    }
}