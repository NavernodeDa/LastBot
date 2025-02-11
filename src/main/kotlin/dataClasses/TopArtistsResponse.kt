@file:Suppress("SpellCheckingInspection")

package dataClasses

import com.google.gson.annotations.SerializedName

data class TopArtistsResponse(
    val topartists: TopArtists,
)

data class TopArtists(
    val artist: List<TopArtist>,
    @SerializedName("@attr")
    val attr: TopArtistsAttr,
)

data class TopArtistsAttr(
    val user: String,
    val totalPages: String,
    val page: String,
    val perPage: String,
    val total: String,
)

data class TopArtist(
    val streamable: String,
    val image: List<Image>,
    val mbid: String,
    val url: String,
    val playcount: String,
    @SerializedName("@attr")
    val attr: ArtistAttr,
    val name: String,
)

data class ArtistAttr(
    val rank: String,
)

data class Image(
    val size: String,
    @SerializedName("#text")
    val text: String,
)
