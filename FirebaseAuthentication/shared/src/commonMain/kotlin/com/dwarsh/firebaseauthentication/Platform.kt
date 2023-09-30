package com.dwarsh.firebaseauthentication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform