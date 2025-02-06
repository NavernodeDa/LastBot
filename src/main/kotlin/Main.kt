@file:Suppress("ktlint:standard:no-wildcard-imports")

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("SpotifyBotLogger")

fun main() {
    CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            updateMessage()
            logger.info("Message is updated")
            delay(config[Data.updateInterval] * 60000)
        }
    }

    bot.startPolling()
    logger.info("Bot is started")
}
