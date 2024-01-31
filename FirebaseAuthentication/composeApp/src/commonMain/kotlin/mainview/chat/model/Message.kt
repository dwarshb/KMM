package mainview.chat.model

import Utils
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val sender: Sender,
    val text: String,
    val messageTime: String = Utils.currentTime(),
    val images: List<ByteArray> = emptyList(),
)


@Serializable
data class Sender(
    val uid: String,
    val email: String
)