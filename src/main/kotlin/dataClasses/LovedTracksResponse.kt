package dataClasses

import com.google.gson.annotations.SerializedName

data class LovedTracksResponse(
    @SerializedName("lovedtracks")
    val lovedTracks: LovedTracks,
    @SerializedName("@attr")
    val attr: LovedTracksAttr,
)

data class LovedTracksAttr(
    val user: String,
    val totalPages: String,
    val page: String,
    val perPage: String,
    val total: String,
)

data class LovedTracks(
    @SerializedName("track")
    val tracks: List<LovedTrack>,
)

data class LovedTrack(
    val artist: Artist,
    val date: TrackDate,
    val mbid: String,
    val url: String,
    val name: String,
    val image: List<Image>,
    val streamable: Streamable,
) {
    fun toTrack() =
        Track(
            artist = TrackArtist(this.artist.mbid, this.artist.name),
            streamable = this.streamable.text,
            image = this.image,
            mbid = this.mbid,
            album = TrackAlbum("", ""),
            name = this.name,
            url = this.url,
            date = this.date,
        )
}

data class Streamable(
    @SerializedName("fulltrack")
    val fullTrack: String,
    @SerializedName("#text")
    val text: String,
)

data class Artist(
    val url: String,
    val name: String,
    val mbid: String,
)
