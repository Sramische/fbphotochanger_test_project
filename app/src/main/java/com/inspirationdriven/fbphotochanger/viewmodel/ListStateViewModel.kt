package com.inspirationdriven.fbphotochanger.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.inspirationdriven.fbphotochanger.ui.ThumbnailListFragment

open class ListStateViewModel : ViewModel(){
    val state = MutableLiveData<ThumbnailListFragment.State>()

    init{
        state.setValue(ThumbnailListFragment.State.LIST)
    }
}