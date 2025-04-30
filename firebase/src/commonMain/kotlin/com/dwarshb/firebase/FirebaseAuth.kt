package com.dwarshb.firebase


import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FirebaseAuth {

    private val API_KEY: String? = Firebase.getAPIKey()

    private val httpClient = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

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
            val response = json.decodeFromString<AuthResponse>(responseBody.bodyAsText())
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
            val response = json.decodeFromString<AuthResponse>(responseBody.bodyAsText())
            onCompletion.onSuccess(response)
        } else {
            onCompletion.onError(Exception(responseBody.bodyAsText()))
        }
    }

    suspend fun getRefreshToken(refreshToken: String?,onCompletion: onCompletion<TokenResponse>) {
        try {

            val responseBody = httpClient
                .post("https://securetoken.googleapis.com/v1/token?key=${API_KEY}") {
                    header("Content-Type", "application/json")
                    parameter("grant_type", "refresh_token")
                    parameter("refresh_token", refreshToken)
                }
            if (responseBody.status.value in 200..299) {
                try {
                    val response = json.decodeFromString<TokenResponse>(responseBody.bodyAsText())
                    onCompletion.onSuccess(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                onCompletion.onError(Exception(responseBody.bodyAsText()))
            }
        } catch (e: Exception) {
            onCompletion.onError(e)
        }
    }
}