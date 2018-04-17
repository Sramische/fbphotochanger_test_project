package com.inspirationdriven.fbphotochanger.model

import com.inspirationdriven.fbphotochanger.model.fb.AlbumMeta
import com.inspirationdriven.fbphotochanger.model.fb.Picture

class Album(val meta: AlbumMeta, thumbnail: Picture) : Thumbnail(thumbnail.url) {
    val count = meta.count
    val countStr = "$count ${if (count == 1) "photo" else "photos"}"
    val name = meta.name
}