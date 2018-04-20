package com.inspirationdriven.fbphotochanger.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.BindingAdapter
import android.widget.ImageView
import com.inspirationdriven.fbphotochanger.R
import com.inspirationdriven.fbphotochanger.getProfilePicture
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PhotoViewModel(app: Application) : AndroidViewModel(app) {
    val profilePicUrl = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val overridenProfilePic = MutableLiveData<String>()

    init {
        val p = getApplication<Application>().getSharedPreferences(PhotoViewModel::class.java.simpleName, Context.MODE_PRIVATE)
                .getString(PREF_KEY_PROFILE_URL, null)
        profilePicUrl.value = p
    }

    fun fetchProfilePicture(width: Int, force: Boolean = false) {
        if (profilePicUrl.value != null && !force) return
        getProfilePicture(width).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ profilePicUrl.value = it }, { e -> e.printStackTrace() })
    }

    fun setProfilePic(url: String) {
        profilePicUrl.value = url
    }

    override fun onCleared() {
        profilePicUrl.value?.let {
            getApplication<Application>().getSharedPreferences(PhotoViewModel::class.java.simpleName, Context.MODE_PRIVATE)
                    .edit()
                    .putString(PREF_KEY_PROFILE_URL, it)
                    .apply()
        }
        super.onCleared()
    }

    companion object {
        const val PREF_KEY_PROFILE_URL = "profile-url"
    }
}

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String?) {
    url?.let {
        Picasso.with(context).load(it).into(this)
    }
}

@BindingAdapter("imageUrlCropped")
fun ImageView.setImageUrlCropped(url: String?) {
    Picasso.with(context).load(url).fit().centerCrop().placeholder(R.drawable.ic_image_black_24px).into(this)
}