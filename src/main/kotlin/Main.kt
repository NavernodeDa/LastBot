@file:Suppress("ktlint:standard:no-wildcard-imports")

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.natpryce.konfig.*
import de.umass.lastfm.Artist
import de.umass.lastfm.Caller
import de.umass.lastfm.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val config = ConfigurationProperties.fromResource("config.properties")
private val logger: Logger = LoggerFactory.getLogger("SpotifyBotLogger")
private val chatId = config[Key("chatId", stringType)]
private val messageId = config[Key("messageId", longType)]
private val bot = createBot()

fun main() {
    Caller.getInstance().apply {
        userAgent = config[Key("userAgent", stringType)]
    }

    CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            updateMessage()
            delay(config[Key("updateInterval", longType)])
        }
    }

    bot.startPolling()
    logger.info("Bot is started")
}

private fun createBot(): Bot =
    bot {
        token = config[Key("tokenBot", stringType)]
        dispatch {
            command("update") {
                updateMessage(message.from?.id)
            }
        }
    }

private fun updateMessage(userId: Long? = null) {
    bot.editMessageText(
        chatId = ChatId.fromChannelUsername(chatId),
        messageId = messageId,
        text = buildText(),
        parseMode = ParseMode.HTML,
        disableWebPagePreview = true,
    )
    if (userId != null) {
        bot.forwardMessage(
            ChatId.fromId(userId),
            ChatId.fromChannelUsername(chatId),
            messageId = messageId,
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
        val user = config[Key("user", stringType)]
        val apiKey = config[Key("apiKey", stringType)]
        User.getTopArtists(user, apiKey).toList()
    } catch (e: Exception) {
        logger.error("Error fetching favorite artists: ${e.message}", e)
        emptyList()
    }
