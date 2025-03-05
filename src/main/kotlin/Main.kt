@file:Suppress("ktlint:standard:no-wildcard-imports")

import com.natpryce.konfig.ConfigurationProperties
import dataClasses.Data
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

fun main(): Unit =
    runBlocking {
        val config = ConfigurationProperties.fromResource("config.properties")
        val logger = LoggerFactory.getLogger("SpotifyBotLogger")
        launch {
            startUpdate(
                config,
                logger,
                HttpClient(CIO) {
                    install(ContentNegotiation) {
                        gson()
                    }
                    install(UserAgent) {
                        config[Data.userAgent]
                    }
                },
            )
        }
    }
