package mainview.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import firebase.Firebase
import firebase.FirebaseDatabase
import firebase.onCompletion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import mainview.chat.model.Message

class ChatViewModel : ViewModel() {
    private val _messagesList = MutableStateFlow(listOf<Message>())
    val messageList = _messagesList.asStateFlow()
    val firebaseDatabase = FirebaseDatabase()
    val currentUser = Firebase.getCurrentUser()

    init {
        getMessages()
    }


    suspend fun sendTextMessage(message: Message) {
        val child = listOf("messages", Clock.System.now().epochSeconds.toString())
        val map = HashMap<String,Any>()
        map.put(message.sender.uid,message)
        firebaseDatabase.updateFirebaseDatabase(child,map,
            object : onCompletion<String> {
                override fun onSuccess(T: String) {
                    addToMessageList(message)
                }

                override fun onError(e: Exception) {
                    println(e.message)
                }
            })
    }

    fun getMessages() {
        viewModelScope.launch {
            val child = listOf("messages")
            val listofMessage = mutableListOf<Message>()
            firebaseDatabase.readFirebaseDatabase(child,"",
                object : onCompletion<String> {
                    override fun onSuccess(T: String) {
                       try {
                            val response = Json.parseToJsonElement(T)
                            for (key1 in response.jsonObject) {
                                for (key2 in key1.value.jsonObject) {
                                    if (key2.key==currentUser.uid) {
                                        val message = Json {
                                            ignoreUnknownKeys = true
                                        }.decodeFromJsonElement<Message>(key2.value)
                                        listofMessage.add(message)
                                    }
                                }
                            }
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }
                        _messagesList.value = listofMessage
                    }

                    override fun onError(e: Exception) {
                        print(e.message)
                    }
                }
            )
        }
    }

    fun addToMessageList(message: Message) {
        _messagesList.value += message
    }

}