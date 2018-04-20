package com.inspirationdriven.fbphotochanger.model

import com.inspirationdriven.fbphotochanger.model.fb.AlbumMeta

class Album(val meta: AlbumMeta, thumbnail: String?) : Thumbnail(thumbnail) {
    val count = meta.count
    val countStr = "$count ${if (count == 1) "photo" else "photos"}"
    val name = meta.name
}