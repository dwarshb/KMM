package com.dwarshb.firebase


import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.prepareGet
import io.ktor.client.request.put
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.request
import io.ktor.events.EventHandler
import io.ktor.events.Events
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.headers
import io.ktor.http.headersOf
import io.ktor.http.websocket.websocketServerAccept
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.core.use
import io.ktor.utils.io.readUTF8Line
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FirebaseStorage() {
    private val STORAGE_URL = Firebase.getStorageURL()
    private val currentUser = Firebase.getCurrentUser()
    private val httpClient = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
        install(HttpRedirect)
    }

    suspend fun updateFirebaseStorage(
        child: List<String>,
        parameter: HashMap<String,Any>,
        file: File,
        onCompletion: onCompletion<String>) {
        val fileBytes = file.byteArray
        val idToken = currentUser?.idToken
        val childPath = child.joinToString("/")
        println("Path: ${fileBytes}")
        val responseBody = httpClient.post("${STORAGE_URL}?uploadType=media&name=messages/${file.name}") {
            header(HttpHeaders.Authorization, "Bearer $idToken")
            header(HttpHeaders.ContentType, "image/jpeg")
            setBody(fileBytes)
        }

        println("Response ${responseBody.request.headers.toString()} \n ${responseBody.bodyAsText()}")
        if (responseBody.status.value in 200..299) {
            val response = responseBody.bodyAsText()
            onCompletion.onSuccess(response)
        } else {
            onCompletion.onError(Exception(
                "${responseBody.request.url} ${responseBody.bodyAsText()}"))
        }
    }

    suspend fun readFirebaseDatabase(
        child: List<String>,
        query: String,
        onCompletion: onCompletion<String>) {
        val idToken = currentUser?.idToken
        val childPath = child.joinToString("/")

        val responseBody = httpClient
            .get("${STORAGE_URL}${childPath}.json?auth=${idToken}&${query}") {
                header("Content-Type", "application/json")
            }

        if (responseBody.status.value in 200..299 || responseBody.status == HttpStatusCode.TemporaryRedirect) {
            onCompletion.onSuccess(responseBody.bodyAsText())
        } else {
            onCompletion.onError(
                Exception(
                    "${responseBody.request.url} ${responseBody.bodyAsText()}"
                )
            )
        }
    }



    data class Event(val name: String = "", val data: String = "")

    fun getEventsFlow(child: List<String>, query: String): Flow<Event> = flow {
        coroutineScope {
            while (isActive) {
                val idToken = currentUser?.idToken
                val childPath = child.joinToString("/")

                val conn = httpClient.prepareGet(
                    "${STORAGE_URL}${childPath}.json?auth=${idToken}&${query}"
                ) {
                    header(HttpHeaders.Accept, "text/event-stream")
                    header(HttpHeaders.Connection, "keep-alive")
                }
                conn.execute { response ->
                    println("Connection:" + response)

                    var inputReader = response.bodyAsChannel()
                    var event = Event()
                    // run forever
                    while (true) {
                        val line = inputReader.readUTF8Line()
                            .toString() // Blocking function. Read stream until \n is found

                        when {
                            line.startsWith("event:") -> { // get event name
                                event = event.copy(name = line.substring(6).trim())
                            }

                            line.startsWith("data:") -> { // get data
                                event = event.copy(data = line.substring(5).trim())
                            }

                            line.isEmpty() -> { // empty line, finished block. Emit the event
                                emit(event)
                                event = Event()
                            }
                        }
                        println(event.name)
                    }
                }
            }
        }
    }
}

