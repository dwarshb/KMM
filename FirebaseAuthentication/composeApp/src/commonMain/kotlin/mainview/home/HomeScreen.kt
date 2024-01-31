package mainview.home

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import mainview.chat.ChatList
import mainview.chat.LoadingAnimation

@Composable
fun HomeScreen() {
    val viewModel = HomeScreenViewModel()
    val messages by viewModel.messageList.collectAsState()
    Scaffold {
        ChatList(messages, _onRefresh = {
            viewModel.getMessages()
        })
    }
}