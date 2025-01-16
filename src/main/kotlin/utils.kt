@file:Suppress("ktlint:standard:filename", "ktlint:standard:no-wildcard-imports")

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.natpryce.konfig.*
import de.umass.lastfm.Artist
import de.umass.lastfm.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object Data : PropertyGroup() {
    val apiKey by stringType
    val user by stringType
    val tokenBot by stringType
    val chatId by longType
    val messageId by longType
    val userAgent by stringType
    val updateInterval by longType
}

@Serializable
private data class Strings(
    val pastSongs: String,
    val favoriteArtists: String,
    val listens: String,
    val thereIsNothingHere: String,
)

@Suppress("ktlint:standard:property-naming")
const val notes = "\uD83C\uDFB6"

@Suppress("ktlint:standard:property-naming")
const val whiteHeart = "\uD83E\uDE76"

@Suppress("ktlint:standard:property-naming")
const val blueHeart = "\uD83E\uDE75"

@Suppress("ktlint:standard:property-naming")
const val think = "\uD83E\uDD14"

val config = ConfigurationProperties.fromResource("config.properties")
private val logger: Logger = LoggerFactory.getLogger("SpotifyBotLogger")
val bot = createBot()

private val deserialized: Strings =
    Json.decodeFromString<Strings>(
        File("./src/main/resources/strings.json").readText(),
    )

private fun createBot(): Bot =
    bot {
        token = config[Data.tokenBot]
        dispatch {
            command("update") {
                updateMessage(message.from?.id)
            }
        }
    }

fun updateMessage(userId: Long? = null) {
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

private fun buildText(): String {
    val text =
        StringBuilder().append(
            "$notes${deserialized.pastSongs}$notes\n" + """Тут должны быть песни - Но Spotify гандоны""" + "\n\n" +
                "$whiteHeart${deserialized.favoriteArtists}$blueHeart\n",
        )

    getFavoriteArtists().also { list ->
        if (list.isNotEmpty()) {
            list.dropLast(30).forEachIndexed { index, artist ->
                text.append(
                    """${index + 1}. <a href="${artist.url}">${artist.name}</a> - ${artist.playcount} ${deserialized.listens}""" +
                        "\n",
                )
            }
        } else {
            logger.warn("Result of getFavoriteArtists() is empty")
            text.append("${deserialized.thereIsNothingHere} $think")
        }
    }

    return text.toString()
}

private fun getFavoriteArtists(): List<Artist> =
    try {
        val user = config[Data.user]
        val apiKey = config[Data.apiKey]
        User.getTopArtists(user, apiKey).toList()
    } catch (e: Exception) {
        logger.error("Error fetching favorite artists: ${e.message}", e)
        emptyList()
    }
