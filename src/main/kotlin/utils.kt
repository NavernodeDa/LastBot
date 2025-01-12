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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Data : PropertyGroup() {
    val apiKey by stringType
    val user by stringType
    val tokenBot by stringType
    val chatId by longType
    val messageId by longType
    val userAgent by stringType
    val updateInterval by longType
}

val config = ConfigurationProperties.fromResource("config.properties")
private val logger: Logger = LoggerFactory.getLogger("SpotifyBotLogger")
val bot = createBot()

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
            "\uD83C\uDFB6Прошлые песни\uD83C\uDFB6\nТут должны быть песни - Но Spotify гандоны\n\n" +
                "\uD83E\uDE76Любимые исполнители\uD83E\uDE75\n",
        )

    getFavoriteArtists().also { artist ->
        if (artist.isNotEmpty()) {
            artist.dropLast(30).forEachIndexed { index, name ->
                text.append("""${index + 1}. <a href="${name.url}">${name.name}</a> - ${name.playcount} прослушиваний""" + "\n")
            }
        } else {
            text.append("Тут ничего нету \uD83E\uDD14")
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
