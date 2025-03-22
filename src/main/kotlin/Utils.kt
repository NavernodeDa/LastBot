@file:Suppress("ktlint:standard:no-wildcard-imports")

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.natpryce.konfig.ConfigurationProperties
import dataClasses.Data
import dataClasses.Strings
import dataClasses.TopArtist
import dataClasses.Track
import io.ktor.client.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import java.util.*

private lateinit var config: ConfigurationProperties
private lateinit var logger: Logger
lateinit var client: HttpClient
private lateinit var bot: Bot

var cache = Cache<String>()[CacheKey.SUMMARY_TEXT]

fun setValues(
    properties: ConfigurationProperties,
    loggerFunc: Logger,
    httpClient: HttpClient,
    botTelegram: Bot,
) {
    config = properties
    logger = loggerFunc
    client = httpClient
    bot = botTelegram
}

private fun deserialize(logger: Logger): Strings =
    try {
        Deserialized(logger).getDeserialized("strings_ru.json")!!
    } catch (e: Exception) {
        logger.error(e.toString())
        throw e
    }

suspend fun infoText(lastFmUser: String? = null): String {
    val user =
        LastFmApi(client, config[Data.apiKey]).User(lastFmUser ?: config[Data.user]).getInfo().user
    val deserialized = deserialize(logger)
    val text =
        """
        <b>${deserialized.infoForAccount}: <a href="${user.image[2].text}">${user.name}</a></b>
        
        ${deserialized.realName}: ${user.realname}
        ${deserialized.country}: ${user.country}
        ${deserialized.subscriber}: ${user.subscriber}
        
        ${deserialized.playcount}: ${user.playcount}
        ${deserialized.artistCount}: ${user.artist_count}
        ${deserialized.trackCount}: ${user.track_count}
        ${deserialized.albumCount}: ${user.album_count}
        ${deserialized.playlists}: ${user.playlists}
        
        ${deserialized.link}: <a href="${user.url}">${user.name}</a>
        ${deserialized.registered}: ${Date(user.registered.text * 1000L)}
        """.trimIndent()
    return text
}

fun updateMessage(
    text: String,
    userId: Long? = null,
) {
    bot.editMessageText(
        chatId = ChatId.fromId(config[Data.chatId]),
        messageId = config[Data.messageId],
        text = text,
        parseMode = ParseMode.HTML,
        disableWebPagePreview = true,
    )
    if (userId != null) {
        bot.forwardMessage(
            ChatId.fromId(userId),
            ChatId.fromId(config[Data.chatId]),
            messageId = config[Data.messageId],
        )
    }
}

suspend fun buildText(): String =
    coroutineScope {
        val deserialized = deserialize(logger)
        val text = StringBuilder()

        val recentTracksDeferred = async { getRecentSongs() }
        val favoriteArtistsDeferred = async { getFavoriteArtists() }

        var recentTracks = recentTracksDeferred.await()?.ifEmpty { null }
        val favoriteArtists = favoriteArtistsDeferred.await()?.ifEmpty { null }

        if ((recentTracks != null) && (recentTracks.size != config[Data.limitForTracks])) {
            text.append("${deserialized.nowPlaying}\n")
            addNowPlaying(text, recentTracks[0])
            recentTracks = recentTracks.drop(1)
        }

        text.append("\n${deserialized.pastSongs}\n")
        recentTracks?.let {
            addRecentTracks(text, it)
        } ?: run {
            logger.warn("Result of getRecentSongs() is empty")
            text.append(deserialized.thereIsNothingHere)
        }

        text.append("\n${deserialized.favoriteArtists}\n")
        favoriteArtists?.let {
            addFavoriteArtists(text, it)
        } ?: run {
            logger.warn("Result of getFavoriteArtists() is empty")
            text.append(deserialized.thereIsNothingHere)
        }

        return@coroutineScope text
            .toString()
            .replace("&", "&amp;")
    }

private fun addNowPlaying(
    text: StringBuilder,
    track: Track?,
): Boolean =
    if (track != null) {
        text
            .append(
                """${track.artist.text} - <a href="${track.url}">${track.name}</a>""",
            ).append("\n")
        true
    } else {
        false
    }

private fun addRecentTracks(
    text: StringBuilder,
    recentTracks: List<Track>,
) {
    recentTracks.onEach { track ->
        text
            .append(
                """${track.artist.text} - <a href="${track.url}">${track.name}</a>""",
            ).append("\n")
    }
}

private fun addFavoriteArtists(
    text: StringBuilder,
    listArtists: List<TopArtist>,
) {
    listArtists.forEachIndexed { index, artist ->
        text
            .append(
                """${index + 1}. <a href="${artist.url}">${artist.name}</a> - ${artist.playcount} ${deserialize(logger).listens}""",
            ).append("\n")
    }
}

private suspend fun getFavoriteArtists(): List<TopArtist>? =
    safeApiCall {
        val user = config[Data.user]
        val apiKey = config[Data.apiKey]
        LastFmApi(client, apiKey)
            .User(user)
            .getTopArtists(limit = config[Data.limitForArtists])
            ?.topartists
            ?.artist
    }

private suspend fun getRecentSongs(): List<Track>? =
    safeApiCall {
        val user = config[Data.user]
        val apiKey = config[Data.apiKey]
        LastFmApi(client, apiKey)
            .User(user)
            .getRecentTracks(limit = config[Data.limitForTracks])
            .recenttracks.track
    }

private suspend fun <T> safeApiCall(block: suspend () -> T): T? =
    try {
        block()
    } catch (e: HttpRequestTimeoutException) {
        logger.error("Request timeout has expired")
        null
    } catch (e: Exception) {
        logger.error("Error occurred", e)
        null
    }
