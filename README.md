# MusicProfileBot
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

Kotlin-based Telegram bot for tracking and updating information about user's music preferences based on Last.fm data. The bot periodically checks the user's top artists and sends updates to the specified Telegram channel or chat.

## How to use
Insert the required values into src/main/resources/config.properties:

```properties
apiKey=ap1K3y40r1astFm
user=user_from_last_fm
tokenBot=1234567890:tokenForYourTelegramBot
chatId=chatId for your chat/channel
messageId=2L // id of the message that will be changed in chat/channel
userAgent=user agent for Last.fm library
updateInterval=interval in minutes
```
The ```chatId``` and  ```messageId``` fields are filled in together: the id of your chat/channel is inserted into chatId, and the id of the message from your chat/channel is inserted into messageId.

### ❗️Before you run the bot, add it to your chat/channel and give it admin if you add it to the  channel ❗️

## TODO list
- [ ] Change library to [last.fm-api](https://github.com/vpaliy/last.fm-api)
- [ ] Make it possible to change text from config.properties
- [ ] Show recently played songs
- [x] Separate all functions into a separate file

## Example
![](https://github.com/user-attachments/assets/bcec9523-5a20-4543-b94e-37ed4e8d433e)

## Usage
- ![](https://avatars.githubusercontent.com/u/57418018?s=24) [Kotlin-telegram-bot](https://github.com/kotlin-telegram-bot/kotlin-telegram-bot) for Telegram API.
- [last.fm-api](https://github.com/jkovacs/lastfm-java) for Last.fm API.
- ![](https://avatars.githubusercontent.com/u/1521407?s=24) [Slf4j](https://github.com/qos-ch/slf4j) for logging.
- ![](https://avatars.githubusercontent.com/u/56219?s=24) [Konfig](https://github.com/npryce/konfig) for work with properties file.
- ![](https://avatars.githubusercontent.com/u/1446536?s=24) [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) for coroutines (async).

## License
MusicProfileBot is under the MIT License. See the [LICENSE](LICENSE) for more information.
