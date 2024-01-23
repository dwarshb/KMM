package authentication
import app.cash.sqldelight.db.SqlDriver
import com.dwarshb.firebaseauthentication.Database
import com.dwarshb.firebaseauthentication.DatabaseQueries
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import onCompletion

class AuthenticationViewModel(var sqlDriver: SqlDriver) : ViewModel() {
    private val API_KEY = "AIzaSyA4mmg2LvJMNkljUnIFV7SJRgWlvnHG1-Q"
    lateinit var databaseQuery : DatabaseQueries
    init {
        val database = Database(sqlDriver)
        databaseQuery = database.databaseQueries
    }

    private val httpClient = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
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
        onCompletion: onCompletion
    ) {
        if (password == confirmPassword) {
            viewModelScope.launch {
                val responseBody = httpClient
                    .post("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=${API_KEY}") {
                        header("Content-Type", "application/json")
                        parameter("email", email)
                        parameter("password", password)
                        parameter("returnSecureToken", true)
                    }
                if (responseBody.status.value in 200..299) {
                    val response = Json { ignoreUnknownKeys = true }
                        .decodeFromString<AuthResponse>(responseBody.bodyAsText())
                    storeUserDetails(response)
                    onCompletion.onSuccess(response.idToken)
                } else {
                    onCompletion.onError(Exception(responseBody.bodyAsText()))
                }
            }
        } else {
            onCompletion.onError(Exception("Password doesn't match"))
        }
    }

    fun login(
        email: String,
        password: String,
        onCompletion: onCompletion
    ) {
        viewModelScope.launch {
            val responseBody = httpClient
                .post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${API_KEY}") {
                    header("Content-Type", "application/json")
                    parameter("email", email)
                    parameter("password", password)
                    parameter("returnSecureToken", true)
                }
            if (responseBody.status.value in 200..299) {
                val response = Json { ignoreUnknownKeys = true }
                    .decodeFromString<AuthResponse>(responseBody.bodyAsText())
                storeUserDetails(response)
                onCompletion.onSuccess(response.idToken)
            } else {
                onCompletion.onError(Exception(responseBody.bodyAsText()))
            }
        }
    }

    internal fun storeUserDetails(response: AuthResponse) {
        databaseQuery.insertUser(
            response.idToken, response.email, response.refreshToken,
            response.email
        )
    }

    internal fun checkSession(onCompletion: onCompletion) {
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
