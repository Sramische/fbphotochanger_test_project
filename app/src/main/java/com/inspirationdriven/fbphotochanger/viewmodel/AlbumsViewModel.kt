package com.inspirationdriven.fbphotochanger.viewmodel

import android.arch.lifecycle.MutableLiveData
import com.inspirationdriven.fbphotochanger.getAlbumsCombined
import com.inspirationdriven.fbphotochanger.model.Album
import com.inspirationdriven.fbphotochanger.ui.ThumbnailListFragment.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AlbumsViewModel : ListStateViewModel() {
    val albums = MutableLiveData<List<Album>>()

    fun fetchAlbums() {
        getAlbumsCombined()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { albums.value ?: state.setValue(State.LOADING) }
                .doFinally { state.value = if (albums.value?.isEmpty() != false) State.NO_DATA else State.LIST }
                .subscribe({
                    albums.value = it
                }, {
                    state.value = State.ERROR
                })
    }
}
