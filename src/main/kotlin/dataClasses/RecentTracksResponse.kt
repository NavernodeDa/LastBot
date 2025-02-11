@file:Suppress("SpellCheckingInspection")

package dataClasses

import com.google.gson.annotations.SerializedName

data class RecentTracksResponse(
    val recenttracks: RecentTracks,
)

data class RecentTracks(
    val track: List<Track>,
    @SerializedName("@attr")
    val attr: RecentTracksAttr,
)

data class Track(
    val artist: TrackArtist,
    val streamable: String,
    val image: List<Image>,
    val mbid: String,
    val album: TrackAlbum,
    val name: String,
    val url: String,
    val date: TrackDate,
)

data class RecentTracksAttr(
    val user: String,
    val totalPages: String,
    val page: String,
    val perPage: String,
    val total: String,
)

data class TrackArtist(
    val mbid: String,
    @SerializedName("#text")
    val text: String,
)

data class TrackAlbum(
    val mbid: String,
    @SerializedName("#text")
    val text: String,
)

data class TrackDate(
    val uts: String,
    @SerializedName("#text")
    val text: String,
)
