package com.killzone.testapp.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Coordinates(
    val result: Int,
    val response: Res
) : Parcelable

@Parcelize
data class Res(
    val points: List<Point>
) : Parcelable

@Parcelize
data class Point(
    val x: Double,
    val y: Double
) : Parcelable


data class Noi(
    @SerializedName("data")
    val m: List<Post>
)

////////////////////////////////////

data class Post(
    val id: String,
    val title: String
)

data class HomeFeed(
    val videos: List<Video>
)

data class Video(
    val id: Int,
    val name: String,
    val link: String,
    val imageUrl: String,
    val numberOfViews: Int,
    val channel: Channel
)

data class Channel(val name: String, val profileImageUrl: String)

data class Resp(
    val id: Int,
    val token: String
)

