import com.natpryce.konfig.ConfigurationProperties
import dataClasses.Data
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.gson.gson
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
