@file:Suppress("ktlint:standard:no-wildcard-imports")

import dataClasses.GetInfoResponse
import dataClasses.RecentTracksResponse
import dataClasses.TopArtistsResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LastFmApi(
    val client: HttpClient,
    val apiKey: String,
) {
    inner class User(
        private val user: String,
    ) {
        /**
         * @param period (Optional) : overall | 7day | 1month | 3month | 6month | 12month - The time period over which to retrieve top artists for.
         * @param limit (Optional) : The number of results to fetch per page. Defaults to 50.
         * @param page (Optional) : The page number to fetch. Defaults to first page.
         */
        suspend fun getTopArtists(
            period: String? = null,
            limit: Int? = null,
            page: Int? = null,
        ): TopArtistsResponse? =
            withContext(Dispatchers.IO) {
                client
                    .get("https://ws.audioscrobbler.com/2.0/") {
                        parameter("method", "user.getTopArtists")
                        parameter("api_key", apiKey)
                        parameter("user", user)
                        parameter("limit", limit)
                        parameter("period", period)
                        parameter("page", page)
                        parameter("format", "json")
                    }.body()
            }

        /**
         * @param limit (Optional) : The number of results to fetch per page. Defaults to 50. Maximum is 200.
         * @param page (Optional) : The page number to fetch. Defaults to first page.
         * @param from (Optional) : Beginning timestamp of a range - only display scrobbles after this time, in UNIX timestamp format (integer number of seconds since 00:00:00, January 1st 1970 UTC). This must be in the UTC time zone.
         * @param extended (0|1) (Optional) : Includes extended data in each artist, and whether the user has loved each track
         * @param to (Optional) : End timestamp of a range - only display scrobbles before this time, in UNIX timestamp format (integer number of seconds since 00:00:00, January 1st 1970 UTC). This must be in the UTC time zone.
         */
        suspend fun getRecentTracks(
            limit: Int? = null,
            page: Int? = null,
            from: Long? = null,
            extended: Int? = null,
            to: Long? = null,
        ): RecentTracksResponse =
            withContext(Dispatchers.IO) {
                client
                    .get("https://ws.audioscrobbler.com/2.0/") {
                        parameter("method", "user.getRecentTracks")
                        parameter("api_key", apiKey)
                        parameter("user", user)
                        parameter("limit", limit)
                        parameter("page", page)
                        parameter("from", from)
                        parameter("extended", extended)
                        parameter("to", to)
                        parameter("format", "json")
                    }.body()
            }

        suspend fun getInfo(): GetInfoResponse =
            withContext(Dispatchers.IO) {
                client
                    .get("https://ws.audioscrobbler.com/2.0/") {
                        parameter("method", "user.getInfo")
                        parameter("api_key", apiKey)
                        parameter("user", user)
                        parameter("format", "json")
                    }.body()
            }
    }
}
