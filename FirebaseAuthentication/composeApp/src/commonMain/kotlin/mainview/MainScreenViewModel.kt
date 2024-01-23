package mainview

import app.cash.sqldelight.db.SqlDriver
import com.dwarshb.firebaseauthentication.Database
import com.dwarshb.firebaseauthentication.DatabaseQueries
import dev.icerock.moko.mvvm.viewmodel.ViewModel

class MainScreenViewModel(var sqlDriver: SqlDriver) : ViewModel() {

    lateinit var databaseQuery : DatabaseQueries
    init {
        val database = Database(sqlDriver)
        databaseQuery = database.databaseQueries
    }

    internal fun clearDatabase() {
        databaseQuery.transaction {
            databaseQuery.removeAllUsers()
        }
    }

    internal fun getDriver() = sqlDriver
}