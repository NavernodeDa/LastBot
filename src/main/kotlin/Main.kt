@file:Suppress("ktlint:standard:no-wildcard-imports")

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.logging.LogLevel
import com.natpryce.konfig.ConfigurationProperties
import dataClasses.Data
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*

fun main(args: Array<String>): Unit =
    runBlocking {
        val config = ConfigurationProperties.fromResource("config.properties")
        val logger = LoggerFactory.getLogger("SpotifyBotLogger")
        val httpClient =
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    gson()
                }
                install(UserAgent) {
                    config[Data.userAgent]
                }
            }
        val stringFile =
            when (args.first().lowercase()) {
                "russian" -> "strings_ru"
                "ukrainian" -> "strings_ua"
                "english" -> "strings_en"
                else -> "strings_${args.first().lowercase()}"
            } + ".json"
        launch {
            val bot =
                bot {
                    token = config[Data.tokenBot]
                    logLevel = LogLevel.Error
                    dispatch {
                        fun warnUserIsNull() = logger.warn("message.from is null")
                        command("update") {
                            message.from?.let { user ->
                                updateMessage(buildText(), user.id)
                            } ?: run {
                                warnUserIsNull()
                            }
                        }
                        command("info") {
                            message.from?.let {
                                val messageSplit = message.text!!.split(" ")
                                if (messageSplit.size >= 2) {
                                    bot.sendMessage(
                                        ChatId.fromId(message.from!!.id),
                                        infoText(messageSplit[1]),
                                        parseMode = ParseMode.HTML,
                                    )
                                } else {
                                    bot.sendMessage(
                                        ChatId.fromId(message.from!!.id),
                                        infoText(),
                                        parseMode = ParseMode.HTML,
                                    )
                                }
                            } ?: run {
                                warnUserIsNull()
                            }
                        }
                    }
                }
            setValues(
                config,
                logger,
                httpClient,
                bot,
                stringFile,
            )

            fun getTime() = SimpleDateFormat("HH:mm:ss").format(Date().time)
            CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
                while (true) {
                    val text = buildText()
                    if (cache != text) {
                        cache = text
                        updateMessage(cache ?: text)
                        logger.info("${getTime()} - message is updated")
                    }
                    delay(config[Data.updateInterval] * 60000)
                }
            }

            bot.startPolling()
            logger.info("Bot is started")
        }
    }
