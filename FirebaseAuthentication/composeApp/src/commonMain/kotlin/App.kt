import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.cash.sqldelight.db.SqlDriver
import authentication.AuthenticationView
import authentication.AuthenticationViewModel
import cafe.adriel.voyager.navigator.Navigator
import mainview.MainScreen
import mainview.MainScreenViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(sqlDriver: SqlDriver) {
    val authenticationViewModel = AuthenticationViewModel(sqlDriver)
    val mainScreenViewModel = MainScreenViewModel(sqlDriver)
    var sessionExist = remember { mutableStateOf(false) }
    MaterialTheme {
        authenticationViewModel.checkSession(object : onCompletion{
            override fun onSuccess(token: String) {
                sessionExist.value =  true
            }

            override fun onError(e: Exception) {
                sessionExist.value = false
            }
        })

        if (sessionExist.value)
            Navigator(
                screen = MainScreen(mainScreenViewModel),
                onBackPressed = { currentScreen -> true })
        else
            Navigator(AuthenticationView(authenticationViewModel))
    }
}