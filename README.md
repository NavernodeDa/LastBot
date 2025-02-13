# MusicProfileBot
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

Kotlin-based Telegram bot for tracking and updating information about user's music preferences based on Last.fm data. The bot periodically checks the user's top artists and sends updates to the specified Telegram channel or chat.

## How to use
Insert the required values into src/main/resources/config.properties:

```properties
Data.apiKey=ap1K3y40r1astFm
Data.user=user_from_last_fm
Data.tokenBot=1234567890:tokenForYourTelegramBot
Data.chatId=chatId for your chat/channel
Data.messageId=id of the message that will be changed in chat/channel
Data.userAgent=user agent for Last.fm library
Data.updateInterval=interval in minutes
Data.limitForArtists=limit for list of artists
Data.limitForTracks=limit for list of recent tracks
```
The ```chatId``` and  ```messageId``` fields are filled in together: the id of your chat/channel is inserted into chatId, and the id of the message from your chat/channel is inserted into messageId.

### ❗️Before you run the bot, add it to your chat/channel and give it admin if you add it to the  channel ❗️

## Changing the language for the message

If you want to change the language for your modifiable message, all you need to do is change the name of your file in the `utils.kt` file in the `deserialize()` function. You can also change string values in the file itself (including smiles).

![](https://github.com/user-attachments/assets/199c305b-4003-46ed-aff5-0a6043297fed)

## Example
![](https://github.com/user-attachments/assets/4c90dc35-d52d-45c2-b773-7a2f1c7ce61f)

## Usage
- ![](https://avatars.githubusercontent.com/u/57418018?s=24) [Kotlin-telegram-bot](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot) for Telegram API.
- ![](https://avatars.githubusercontent.com/u/1521407?s=24) [Slf4j](https://github.com/qos-ch/slf4j) for logging.
- ![](https://avatars.githubusercontent.com/u/56219?s=24) [Konfig](https://github.com/npryce/konfig) for work with properties file.
- ![](https://avatars.githubusercontent.com/u/1446536?s=24) [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) for coroutines (async).
- ![](https://avatars.githubusercontent.com/u/28214161?s=24) [ktor](https://github.com/ktorio/ktor) for api requests.

## License
MusicProfileBot is under the MIT License. See the [LICENSE](LICENSE) for more information.
