@file:Suppress("ktlint:standard:no-wildcard-imports")

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val logger: Logger = LoggerFactory.getLogger("SpotifyBotLogger")

fun main() {
    CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            updateMessage()
            logger.info("${getTime()} - message is updated")
            delay(config[Data.updateInterval] * 60000)
        }
    }

    bot.startPolling()
    logger.info("Bot is started")
}

fun getTime(): String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
