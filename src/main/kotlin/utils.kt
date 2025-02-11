@file:Suppress("ktlint:standard:filename", "ktlint:standard:no-wildcard-imports")

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.User
import com.github.kotlintelegrambot.logging.LogLevel
import com.natpryce.konfig.*
import dataClasses.TopArtist
import dataClasses.Track
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@Serializable
data class Strings(
    val pastSongs: String,
    val favoriteArtists: String,
    val listens: String,
    val thereIsNothingHere: String,
    val nowPlaying: String,
    val infoForAccount: String,
    val age: String,
    val gender: String,
    val subscriber: String,
    val realName: String,
    val country: String,
    val playcount: String,
    val artistCount: String,
    val trackCount: String,
    val albumCount: String,
    val playlists: String,
    val registered: String,
    val link: String,
)

object Data : PropertyGroup() {
    val apiKey by stringType
    val user by stringType
    val tokenBot by stringType
    val chatId by longType
    val messageId by longType
    val userAgent by stringType
    val updateInterval by longType
    val limitForArtists by intType
    val limitForTracks by intType
}

val config = ConfigurationProperties.fromResource("config.properties")
private val logger: Logger = LoggerFactory.getLogger("SpotifyBotLogger")
val bot = createBot()

private val client =
    HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
        install(UserAgent) {
            config[Data.userAgent]
        }
    }
private val lastFmApi = LastFmApi(client)

class Deserialized {
    fun getDeserialized(fileName: String): Strings? {
        val file = Deserialized::class.java.getResource(fileName)?.readText()

        return if (file != null) {
            Json.decodeFromString<Strings>(file)
        } else {
            logger.error("strings.json is null")
            null
        }
    }
}

private val deserialized: Strings =
    try {
        Deserialized().getDeserialized("strings.json")!!
    } catch (e: Exception) {
        logger.error(e.toString())
        throw e
    }

private fun createBot(): Bot =
    bot {
        token = config[Data.tokenBot]
        logLevel = LogLevel.Error
        dispatch {
            command("update") {
                if (checkNullMessageFrom(message.from)) {
                    updateMessage(message.from!!.id)
                }
            }
            command("info") {
                if (checkNullMessageFrom(message.from)) {
                    val messageSplit = message.text!!.split(" ")
                    if (messageSplit.size >= 2) {
                        sendInfo(message.from!!.id, messageSplit[1])
                    } else {
                        sendInfo(message.from!!.id)
                    }
                }
            }
        }
    }

fun checkNullMessageFrom(from: User?) =
    if (from?.id == null) {
        logger.warn("message.from is null")
        false
    } else {
        true
    }

private suspend fun sendInfo(
    id: Long,
    lastFmUser: String? = null,
) {
    val user =
        lastFmApi
            .getInfo(
                lastFmUser ?: config[Data.user],
                config[Data.apiKey],
            ).user
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
    bot.sendMessage(
        ChatId.fromId(id),
        text,
        parseMode = ParseMode.HTML,
    )
}

suspend fun updateMessage(userId: Long? = null) {
    bot.editMessageText(
        chatId = ChatId.fromId(config[Data.chatId]),
        messageId = config[Data.messageId],
        text = buildText(),
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

private suspend fun buildText(): String {
    var recentTracks = getRecentSongs().ifEmpty { null }
    val text = StringBuilder()

    if (recentTracks != null) {
        if (recentTracks.size != config[Data.limitForTracks]) {
            val firstTrack = recentTracks[0]
            text
                .append("${deserialized.nowPlaying}\n")
                .append(
                    """${firstTrack.artist.text} - <a href="${firstTrack.url}">${firstTrack.name}</a>""",
                ).append("\n\n")
            recentTracks = recentTracks.drop(1)
        }
    }

    text.append("${deserialized.pastSongs}\n")

    if (recentTracks != null) {
        recentTracks.onEach { track ->
            text
                .append(
                    """${track.artist.text} - <a href="${track.url}">${track.name}</a>""",
                ).append("\n")
        }
    } else {
        logger.warn("Result of getRecentSongs() is empty")
        text.append(deserialized.thereIsNothingHere)
    }

    text.append("\n${deserialized.favoriteArtists}\n")

    getFavoriteArtists().also { list ->
        if (list != null) {
            list.forEachIndexed { index, artist ->
                text
                    .append(
                        """${index + 1}. <a href="${artist.url}">${artist.name}</a> - ${artist.playcount} ${deserialized.listens}""",
                    ).append("\n")
            }
        } else {
            logger.warn("Result of getFavoriteArtists() is empty")
            text.append(deserialized.thereIsNothingHere)
        }
    }

    return text
        .toString()
        .replace("&", "&amp;")
}

private suspend fun getFavoriteArtists(): List<TopArtist>? =
    try {
        val user = config[Data.user]
        val apiKey = config[Data.apiKey]
        lastFmApi.getTopArtists(user, apiKey, limit = config[Data.limitForArtists])?.topartists?.artist
    } catch (e: HttpRequestTimeoutException) {
        logger.error("Error fetching favorite artists: Request timeout has expired")
        null
    } catch (e: Exception) {
        logger.error("Error fetching favorite artists: ${e.message}", e)
        null
    }

private suspend fun getRecentSongs(): List<Track> =
    try {
        val user = config[Data.user]
        val apiKey = config[Data.apiKey]
        lastFmApi.getRecentTracks(user, apiKey, limit = config[Data.limitForTracks]).recenttracks.track
    } catch (e: Exception) {
        logger.error("Error fetching recent songs: ${e.message}", e)
        emptyList()
    }
