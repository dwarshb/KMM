package mainview.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import mainview.chat.model.Message




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatList(messages: List<Message>, _onRefresh : () -> Unit = {}) {
    val listState = rememberLazyListState()
    val isRefreshing by remember {
        mutableStateOf(false)
    }
    val state = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = _onRefresh)
    if (messages.isNotEmpty()) {
        LaunchedEffect(messages) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    Column(modifier = Modifier
        .padding(bottom = 56.dp)) {
        if(state.progress>0.5f) {
            LoadingAnimation(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                circleSize = 10.dp
            )
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .pullRefresh(state)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val position = event.changes.first().scrollDelta.y
                            // on every relayout Compose will send synthetic Move event,
                            // so we skip it to avoid event spam
                            if (event.type == PointerEventType.Scroll) {
                                println(position)
                                if (event.changes.first().scrollDelta.y<0) {
                                    _onRefresh.invoke()
                                }
                            }
                        }
                    }
                },
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {

            items(messages.size) {
                val message = messages[it]
//                if (message.images.isNotEmpty()) {
//                    MessageImagesStack(message = message)
//                    Spacer(modifier = Modifier.height(4.dp))
//                }
                MessageBubble(message = message)
            }
        }
    }
}