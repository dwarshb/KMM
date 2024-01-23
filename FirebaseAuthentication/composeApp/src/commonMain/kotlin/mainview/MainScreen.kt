package mainview

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen

sealed class BottomNavigationScreen(val title:String) {
    object HomeScreen : BottomNavigationScreen("Home")

    object ChatScreen : BottomNavigationScreen("Chat")

}

data class BottomNavigationItem(
    val route: String,
    val icon: ImageVector
)

val bottomNavigationItems = listOf(
    BottomNavigationItem(
        BottomNavigationScreen.HomeScreen.title,
        Icons.Filled.Home
    ),
    BottomNavigationItem(
        BottomNavigationScreen.ChatScreen.title,
        Icons.Filled.Email
    )
)


data class MainScreen(var mainScreenViewModel: MainScreenViewModel) : Screen {
    @Composable
    override fun Content() {
        val selectedIndex = remember { mutableStateOf(0) }
        Scaffold(
            bottomBar = {
                BottomNavigation {
                    bottomNavigationItems.forEachIndexed { index, bottomNavigationItem ->
                        BottomNavigationItem(
                            selectedContentColor = Color.White,
                            unselectedContentColor = Color.Black,
                            label = {
                                Text(
                                    bottomNavigationItem.route
                                )
                            },
                            icon = {
                                Icon(bottomNavigationItem.icon, "")
                            },
                            selected = selectedIndex.value == index,
                            onClick = {
                                selectedIndex.value = index
                            }
                        )
                    }
                }
            }
        ) {
            when (selectedIndex.value) {
                0 -> HomeScreen(mainScreenViewModel)
                1 -> ChatScreen()
            }
        }
    }
}