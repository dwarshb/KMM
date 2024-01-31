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

@Serializable
data class TokenResponse(
    val expires_in: String,
    val token_type: String,
    val refresh_token: String,
    val id_token: String,
    val user_id: String,
    val project_id: String
)
