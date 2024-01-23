package mainview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ChatScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text("This is Chat Screen")
    }
}