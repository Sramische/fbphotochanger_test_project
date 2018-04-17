package com.inspirationdriven.fbphotochanger.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.BindingAdapter
import android.widget.ImageView
import com.inspirationdriven.fbphotochanger.getProfilePicture
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PhotoViewModel : ViewModel() {

    val profilePicUrl = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val overridenProfilePic = MutableLiveData<String>()

    fun fetchProfilePicture(width: Int) {
        getProfilePicture(width).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ profilePicUrl.value = it }, { e -> e.printStackTrace() })
    }

    fun setProfilePic(url: String){
        profilePicUrl.value = url
    }
}

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String?) {
    url?.let {
        Picasso.with(context).load(it).into(this)
    }
}