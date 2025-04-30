package com.dwarshb.firebase

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class Firebase {

    companion object {
        private var API_KEY: String? = null
        private var DATABASE_URL: String? = null
        private var _currentUser: FirebaseUser? = null
        private var STORAGE_URL: String? = null
        private var GEMINI_API__KEY: String? = null
        private var GEMINI_MODEL: String? = "gemini-2.0-flash"

        fun getAPIKey() = API_KEY
        fun getGeminiAPIKey() = GEMINI_API__KEY
        fun getGeminiModel() = GEMINI_MODEL
        fun getDatabaseURL() = DATABASE_URL
        fun getCurrentUser() = _currentUser
        fun getStorageURL() = STORAGE_URL
    }

    fun initialize(apiKey: String, databaseUrl: String, storageUrl: String) {
        API_KEY = apiKey
        DATABASE_URL = databaseUrl
        STORAGE_URL = storageUrl
    }

    fun setGemini(model: String,key: String) {
        GEMINI_MODEL = model
        GEMINI_API__KEY = key
    }

    fun setCurrentUser(currentUser: FirebaseUser) {
        _currentUser = currentUser
    }
}