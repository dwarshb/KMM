package com.dwarshb.firebase

import kotlinx.serialization.Serializable

@Serializable
data class File(
    val path: String?,
    val name: String,
    var byteArray: ByteArray? = null
)