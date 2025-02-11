@file:Suppress("ktlint:standard:no-wildcard-imports")

import com.natpryce.konfig.ConfigurationProperties
import dataClasses.Data
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import org.slf4j.LoggerFactory

fun main() {
    val config = ConfigurationProperties.fromResource("config.properties")
    startUpdate(
        config,
        LoggerFactory.getLogger("SpotifyBotLogger"),
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
