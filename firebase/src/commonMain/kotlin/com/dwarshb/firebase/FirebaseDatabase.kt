package com.dwarshb.firebase

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.prepareGet
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class FirebaseDatabase {
    private val httpClient = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
        install(HttpRedirect)
    }

    suspend fun patchFirebaseDatabase(
        child: List<String>,
        parameter: HashMap<String,Any>,
        onCompletion: onCompletion<String>) {
        val DATABASE_URL = Firebase.getDatabaseURL()
        val idToken = Firebase.getCurrentUser()?.idToken

        val childPath = child.joinToString("/")
        println("Path: ${childPath}")
        val responseBody = httpClient
            .patch("${DATABASE_URL}/${childPath}.json?auth=${idToken}") {
                header("Content-Type", "application/json")
                setBody(parameter)
            }
        println(responseBody.toString())
        println("Response ${responseBody.bodyAsText()}")
        if (responseBody.status.value in 200..299) {
            val response = responseBody.bodyAsText()
            onCompletion.onSuccess(response)
        } else {
            onCompletion.onError(Exception(
                "${responseBody.request.url} ${responseBody.bodyAsText()}"))
        }
    }


    suspend fun putFirebaseDatabase(
        child: List<String>,
        parameter: HashMap<String,Any>,
        onCompletion: onCompletion<String>) {
        val DATABASE_URL = Firebase.getDatabaseURL()
        val idToken = Firebase.getCurrentUser()?.idToken ?: run {
            onCompletion.onError(Exception("User is not authenticated"))
            return
        }

        val childPath = child.joinToString("/")
        println("Path: ${childPath}")
        val responseBody = httpClient
            .put("${DATABASE_URL}/${childPath}.json?auth=${idToken}") {
                header("Content-Type", "application/json")
                setBody(parameter)
            }
        println(responseBody.toString())
        println("Response ${responseBody.bodyAsText()}")
        if (responseBody.status.value in 200..299) {
            val response = responseBody.bodyAsText()
            onCompletion.onSuccess(response)
        } else {
            onCompletion.onError(Exception(
                "${responseBody.request.url} ${responseBody.bodyAsText()}"))
        }
    }

    suspend fun postFirebaseDatabase(
        child: List<String>,
        parameter: HashMap<String,Any>,
        onCompletion: onCompletion<String>) {
        val DATABASE_URL = Firebase.getDatabaseURL()
        val idToken = Firebase.getCurrentUser()?.idToken ?: run {
            onCompletion.onError(Exception("User is not authenticated"))
            return
        }

        val childPath = child.joinToString("/")
        println("Path: ${childPath}")
        val responseBody = httpClient
            .post("${DATABASE_URL}/${childPath}.json?auth=${idToken}") {
                header("Content-Type", "application/json")
                setBody(parameter)
            }
        println(responseBody.toString())
        println("Response ${responseBody.bodyAsText()}")
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
        val DATABASE_URL = Firebase.getDatabaseURL()
        val idToken = Firebase.getCurrentUser()?.idToken
        val childPath = child.joinToString("/")


        val responseBody = httpClient
            .get("${DATABASE_URL}${childPath}.json?auth=${idToken}&${query}") {
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
                val DATABASE_URL = Firebase.getDatabaseURL()
                val idToken = Firebase.getCurrentUser()?.idToken
                val childPath = child.joinToString("/")

                val conn = httpClient.prepareGet(
                    "${DATABASE_URL}${childPath}.json?auth=${idToken}&${query}"
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