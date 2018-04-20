package com.inspirationdriven.fbphotochanger.model.fb.gson

import com.google.gson.annotations.SerializedName

class ListRoot<T>(val data: List<T>)
class ObjectRoot<T>(val data: T)
class Url(val url: String)
class ImageSource(val width: Int, val source: String) : Comparable<ImageSource> {
    override fun compareTo(other: ImageSource) = width - other.width
}

class Album(val name: String,
            val id: String,
            val picture: ObjectRoot<Url>,
            @SerializedName("photo_count") val count: Int)

class Picture(val images: MutableList<ImageSource>)