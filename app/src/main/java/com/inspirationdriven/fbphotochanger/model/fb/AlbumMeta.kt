package com.inspirationdriven.fbphotochanger.model.fb

import java.io.Serializable

data class AlbumMeta(val fbId: String?,
                     val name: String,
                     var count: Int,
                     val explicitPhotosUri: String? = null,
                     val explicitThumbnailUri: String? = null) : Serializable{
    private val uri = "/$fbId"
    val thumbnailUri = explicitThumbnailUri ?: "$uri/picture"
    val photosUri =  if(fbId == null) explicitPhotosUri else "$uri/photos"
}