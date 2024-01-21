import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AuthenticationViewModel : ViewModel() {
    val API_KEY = "AIzaSyA4mmg2LvJMNkljUnIFV7SJRgWlvnHG1-Q"

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

    fun signUp(email: String,
                       password: String,
                       confirmPassword: String,
                       onCompletion: onCompletion) {
        viewModelScope.launch {
            val responseBody = httpClient
                .post("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=${API_KEY}"){
                    header("Content-Type","application/json")
                    parameter("email",email)
                    parameter("password",password)
                    parameter("returnSecureToken",true)
                }
                .bodyAsText()
            var response = Json { ignoreUnknownKeys = true }.decodeFromString<AuthResponse>(responseBody)
            if (response!=null) {
                onCompletion.onSuccess(response.idToken)
            } else {
                onCompletion.onError(Exception("Failed to signup"))
            }
        }
    }

    suspend fun login(email: String, password: String): Any {
        return ""
    }
}
