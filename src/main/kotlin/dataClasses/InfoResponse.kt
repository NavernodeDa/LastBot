@file:Suppress("SpellCheckingInspection", "PropertyName")

package dataClasses

import com.google.gson.annotations.SerializedName

data class InfoResponse(
    val user: User,
)

data class User(
    val name: String,
    val age: String,
    val subscriber: String,
    val realname: String,
    val bootstrap: String,
    val playcount: String,
    val artist_count: String,
    val playlists: String,
    val track_count: String,
    val album_count: String,
    val image: List<Image>,
    val registered: Registered,
    val country: String,
    val gender: String,
    val url: String,
    val type: String,
)

data class Registered(
    val unixtime: String,
    @SerializedName("#text")
    val text: Long,
)
