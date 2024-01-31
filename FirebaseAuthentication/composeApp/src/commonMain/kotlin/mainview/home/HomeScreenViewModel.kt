package mainview.home

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import firebase.FirebaseDatabase
import firebase.onCompletion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import mainview.chat.model.Message

class HomeScreenViewModel : ViewModel() {
    private val _messagesList = MutableStateFlow(listOf<Message>())
    val messageList = _messagesList.asStateFlow()
    val firebaseDatabase = FirebaseDatabase()

    init {
        getMessages()
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
                                            val message = Json {
                                                ignoreUnknownKeys = true
                                            }.decodeFromJsonElement<Message>(key2.value)
                                            listofMessage.add(message)
                                        }
                            }
                        } catch (e: Exception) {
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
}