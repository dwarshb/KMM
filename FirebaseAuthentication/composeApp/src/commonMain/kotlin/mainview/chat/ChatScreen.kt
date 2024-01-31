package mainview.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import firebase.Firebase
import firebase.onCompletion
import kotlinx.coroutines.launch
import mainview.chat.model.Message
import mainview.chat.model.Sender


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel = ChatViewModel()) {

    val uid = Firebase.getCurrentUser().uid
    val email = Firebase.getCurrentUser().emailID

    val messages by viewModel.messageList.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        bottomBar = {
            MessageInputBox(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = 60.dp, top = 5.dp),
                onSendClick = { text ->
                    val message = Message(
                        sender = Sender(uid,email),
                        text = text
                    )
                    println("Click $message")
                    coroutineScope.launch {
                        viewModel.sendTextMessage(message)
                    }
                })
        }
    )
    {

        ChatList(messages)
    }
}