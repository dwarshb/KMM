import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.cash.sqldelight.db.SqlDriver
import authentication.AuthenticationView
import authentication.AuthenticationViewModel
import firebase.onCompletion
import cafe.adriel.voyager.navigator.Navigator
import com.dwarshb.firebaseauthentication.User
import firebase.Firebase
import firebase.FirebaseUser
import mainview.MainScreen
import mainview.MainScreenViewModel

@Composable
fun App(sqlDriver: SqlDriver) {
    val API_KEY = "AIzaSyA4mmg2LvJMNkljUnIFV7SJRgWlvnHG1-Q"
    val DATABASE_URL = "https://fitnessconnect-3e757-default-rtdb.firebaseio.com"
    val firebase = Firebase()
    firebase.initialize(apiKey = API_KEY, databaseUrl = DATABASE_URL)

    val authenticationViewModel = AuthenticationViewModel(firebase,sqlDriver)
    val mainScreenViewModel = MainScreenViewModel(sqlDriver)
    var sessionExist = remember { mutableStateOf(false) }
    MaterialTheme {
        authenticationViewModel.checkSession(object : onCompletion<User> {
            override fun onSuccess(currentUser: User) {
                if (currentUser!=null) {
                    val firebaseUser = FirebaseUser(
                        currentUser.email, currentUser.idToken,currentUser.localId.toString())
                    firebase.setCurrentUser(firebaseUser)
                }
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