import com.dwarshb.firebaseauthentication.Database
import com.dwarshb.firebaseauthentication.DriverFactory
import com.dwarshb.firebaseauthentication.User

class SQLDatabase(databaseDriverFactory: DriverFactory) {
    private val database = Database(databaseDriverFactory.createDriver())
    private val dbQuery = database.databaseQueries

    internal fun clearDatabase() {
        dbQuery.transaction {
            dbQuery.removeAllUsers()
        }
    }

    internal fun insertUser(user: User) {
        dbQuery.transaction {
            dbQuery.insertUser(
                idToken = user.idToken,
                email = user.email,
                refreshToken = user.refreshToken,
                name = user.name
            )
        }
    }

    internal fun getUsers() : List<User> {
        return dbQuery.selectAllUsers().executeAsList()
    }
}
