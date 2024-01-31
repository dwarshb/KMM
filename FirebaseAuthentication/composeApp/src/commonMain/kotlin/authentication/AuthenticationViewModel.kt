package authentication
import app.cash.sqldelight.db.SqlDriver
import com.dwarshb.firebaseauthentication.Database
import com.dwarshb.firebaseauthentication.DatabaseQueries
import com.dwarshb.firebaseauthentication.User
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import firebase.AuthResponse
import firebase.Firebase
import firebase.FirebaseAuth
import firebase.FirebaseDatabase
import firebase.FirebaseUser
import firebase.TokenResponse
import firebase.onCompletion
import io.ktor.http.parameters
import kotlinx.coroutines.launch

class AuthenticationViewModel(var firebase: Firebase? = null,var sqlDriver: SqlDriver) : ViewModel() {
    private var databaseQuery : DatabaseQueries
    var firebaseAuth = FirebaseAuth()

    init {
        val database = Database(sqlDriver)
        databaseQuery = database.databaseQueries
    }

    fun validateEmail(email: String): Boolean {
        if (email == "") return false
        val emailRegex = Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\$")
        return emailRegex.matches(email)
    }

    suspend fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        completion: onCompletion<String>
    ) {
        if (password == confirmPassword) {
                firebaseAuth.signUpWithEmailAndPassword(email,password, object : onCompletion<AuthResponse> {
                    override fun onSuccess(T: AuthResponse) {
                        storeUserDetails(T)
                        updateUserInDatabase(T.localId,T.email,T.refreshToken)
                        completion.onSuccess(T.idToken)
                    }

                    override fun onError(e: Exception) {
                        completion.onError(e)
                    }
                })
        } else {
            completion.onError(Exception("Password doesn't match"))
        }
    }

    suspend fun login(
        email: String,
        password: String,
        completion: onCompletion<String>
    ) {
            firebaseAuth.login(email,password,object : onCompletion<AuthResponse> {
                override fun onSuccess(T: AuthResponse) {
                    storeUserDetails(T)
                    updateUserInDatabase(T.localId,T.email,T.refreshToken)
                    completion.onSuccess(T.idToken)
                }
                override fun onError(e: Exception) {
                    completion.onError(e)
                }
            })
    }

    private fun updateUserInDatabase(uid:String, email:String, token:String) {
        firebase?.setCurrentUser(FirebaseUser(email,token,uid))
        val firebaseDatabase = FirebaseDatabase()
        viewModelScope.launch {
            val child = listOf("users",uid)
            val map = HashMap<String,Any>()
            map.put("uid",uid)
            map.put("email",email)
            firebaseDatabase.updateFirebaseDatabase(child,map,
                object : onCompletion<String> {
                    override fun onSuccess(T: String) {
                        print(T)
                    }

                    override fun onError(e: Exception) {
                        print(e.message)
                    }
                }
            )
        }
    }
    internal fun storeUserDetails(response: AuthResponse) {
        databaseQuery.insertUser(
            response.idToken, response.email, response.refreshToken,
            response.email,response.localId
        )
    }

    internal fun checkSession(onCompletion: onCompletion<User>) {
        for(user in databaseQuery.selectAllUsers().executeAsList()) {
            println(user.toString())
            if (user!=null) {
                viewModelScope.launch {
                    firebaseAuth.getRefreshToken(user.refreshToken,
                        object : onCompletion<TokenResponse> {
                            override fun onSuccess(T: TokenResponse) {
                                val _user = User(
                                    idToken = T.id_token,
                                    email = user.email,
                                    localId = T.user_id,
                                    refreshToken = T.refresh_token,
                                    name = user.email
                                )
                                onCompletion.onSuccess(_user)
                            }

                            override fun onError(e: Exception) {
                                onCompletion.onError(e)
                            }
                        })
                }
            } else {
                onCompletion.onError(Exception("No session found"))
            }
        }
    }

    internal fun getDriver() = sqlDriver
}
