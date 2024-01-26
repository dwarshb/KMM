package firebase

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class Firebase {
    private lateinit var API_KEY: String

    private val httpClient = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
    }
    fun initializeAPI(apiKey: String) {
        API_KEY = apiKey
    }

    suspend fun signUpWithEmailAndPassword(
        email: String, password: String, onCompletion: onCompletion<AuthResponse>) {
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
            onCompletion.onSuccess(response)
        } else {
            onCompletion.onError(Exception(responseBody.bodyAsText()))
        }
    }

    suspend fun login(
        email: String, password: String, onCompletion: onCompletion<AuthResponse>) {
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
            onCompletion.onSuccess(response)
        } else {
            onCompletion.onError(Exception(responseBody.bodyAsText()))
        }
    }
}