package authentication
import app.cash.sqldelight.db.SqlDriver
import com.dwarshb.firebaseauthentication.Database
import com.dwarshb.firebaseauthentication.DatabaseQueries
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import firebase.AuthResponse
import firebase.Firebase
import firebase.onCompletion
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AuthenticationViewModel(var sqlDriver: SqlDriver) : ViewModel() {
    private val API_KEY = "AIzaSyA4mmg2LvJMNkljUnIFV7SJRgWlvnHG1-Q"
    private var databaseQuery : DatabaseQueries
    var firebase: Firebase = Firebase()

    init {
        firebase.initializeAPI(apiKey = API_KEY)
        val database = Database(sqlDriver)
        databaseQuery = database.databaseQueries
    }

    fun validateEmail(email: String): Boolean {
        if (email == "") return false
        val emailRegex = Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\$")
        return emailRegex.matches(email)
    }

    fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        completion: onCompletion<String>
    ) {
        if (password == confirmPassword) {
            viewModelScope.launch {
                firebase.signUpWithEmailAndPassword(email,password, object : onCompletion<AuthResponse> {
                    override fun onSuccess(T: AuthResponse) {
                        storeUserDetails(T)
                        completion.onSuccess(T.idToken)
                    }

                    override fun onError(e: Exception) {
                        completion.onError(e)
                    }
                })
            }
        } else {
            completion.onError(Exception("Password doesn't match"))
        }
    }

    fun login(
        email: String,
        password: String,
        completion: onCompletion<String>
    ) {
        viewModelScope.launch {
            firebase.login(email,password,object : onCompletion<AuthResponse> {
                override fun onSuccess(T: AuthResponse) {
                    storeUserDetails(T)
                    completion.onSuccess(T.idToken)
                }
                override fun onError(e: Exception) {
                    completion.onError(e)
                }
            })
        }
    }

    internal fun storeUserDetails(response: AuthResponse) {
        databaseQuery.insertUser(
            response.idToken, response.email, response.refreshToken,
            response.email
        )
    }

    internal fun checkSession(onCompletion: onCompletion<String>) {
        for(user in databaseQuery.selectAllUsers().executeAsList()) {
            println(user.toString())
            if (user!=null) {
                onCompletion.onSuccess(user.refreshToken.toString())
            } else {
                onCompletion.onError(Exception("No session found"))
            }
        }
    }

    internal fun getDriver() = sqlDriver
}
