package com.inspirationdriven.fbphotochanger.model.fb

class User(val userId: String) {

    val photosUri = "$userId/photos"
    val profilePictureUri = "$userId/picture"
    val albumsUri = "$userId/albums"

    companion object {
        fun me() = User("me")
    }

}