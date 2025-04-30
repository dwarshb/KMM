package com.dwarshb.firebase

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Reference
 * curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$GEMINI_API_KEY" \
 *   -H 'Content-Type: application/json' \
 *   -d '{
 *     "system_instruction": {
 *       "parts": [
 *         {
 *           "text": "You are a cat. Your name is Neko."
 *         }
 *       ]
 *     },
 *     "contents": [
 *       {
 *         "parts": [
 *           {
 *             "text": "Hello there"
 *           }
 *         ]
 *       }
 *     ]
 *   }'
 */

@Serializable
data class Part(val text: String)

@Serializable
data class Content(
    val role: String = "",
    val parts: List<Part>)

@Serializable
data class RequestBody(
    val system_instruction: Content? = null,
    val contents: List<Content>
)

class Gemini {
    private val GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    private val model = Firebase.getGeminiModel()
    private val geminiApiKey = Firebase.getGeminiAPIKey()
    private val httpClient = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
        install(HttpRedirect)
    }

    suspend fun conversationalAI(personaPrompt: String,
                                 listOfContent: List<Content>,
                                 onCompletion: onCompletion<String>) {
       try {
           val requestBody = RequestBody(
               system_instruction = Content(
                   parts = listOf(Part(text = personaPrompt))
               ),
               contents = listOfContent
           )
           val jsonBody = Json.encodeToString(requestBody)
           val responseBody = httpClient
               .post("${GEMINI_URL}/${model}:generateContent?key=$geminiApiKey") {
                   header("Content-Type", "application/json")
                   setBody(jsonBody)
               }
           println(responseBody.toString())
           if (responseBody.status.value in 200..299) {
               extractPromptFromResponse(responseBody.bodyAsText())?.let {
                   onCompletion.onSuccess(it)
               }
           } else {
               onCompletion.onError(
                   Exception(
                       "${responseBody.request.url} ${responseBody.bodyAsText()}"
                   )
               )
           }
       } catch (e: Exception) {
           onCompletion.onError(e)
       }
    }
    suspend fun generatePrompt(generalPrompt: String, onCompletion: onCompletion<String>) {
        try {
            val requestBody = RequestBody(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = generalPrompt)
                        )
                    )
                )
            )

            val jsonBody = Json.encodeToString(requestBody)
            val responseBody = httpClient
                .post("${GEMINI_URL}/${model}:generateContent?key=$geminiApiKey") {
                    header("Content-Type", "application/json")
                    setBody(jsonBody)
                }
            println(responseBody.toString())
            if (responseBody.status.value in 200..299) {
                extractPromptFromResponse(responseBody.bodyAsText())?.let {
                    onCompletion.onSuccess(it)
                }
            } else {
                onCompletion.onError(
                    Exception(
                        "${responseBody.request.url} ${responseBody.bodyAsText()}"
                    )
                )
            }
        } catch (e: Exception) {
            onCompletion.onError(e)
        }
    }

    fun extractPromptFromResponse(responseString: String): String? {
        val json = Json.parseToJsonElement(responseString)
        val candidates = json.jsonObject["candidates"]?.jsonArray
        val firstCandidate = candidates?.getOrNull(0)?.jsonObject
        val content = firstCandidate?.get("content")?.jsonObject
        val parts = content?.get("parts")?.jsonArray
        val firstPart = parts?.getOrNull(0)?.jsonObject
        val text = firstPart?.get("text")?.jsonPrimitive?.content
        return text
    }
}