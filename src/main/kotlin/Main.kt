@file:Suppress("ktlint:standard:no-wildcard-imports")

import de.umass.lastfm.Caller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("SpotifyBotLogger")

fun main() {
    Caller.getInstance().apply {
        userAgent = config[Data.userAgent].toString()
    }

    CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            updateMessage()
            delay(config[Data.updateInterval])
        }
    }

    bot.startPolling()
    logger.info("Bot is started")
}
