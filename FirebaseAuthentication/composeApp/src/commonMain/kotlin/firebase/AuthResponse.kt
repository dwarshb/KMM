package firebase

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse (
    val idToken : String,
    val email : String ,
    val refreshToken: String,
    val expiresIn: Int,
    val localId: String
)
