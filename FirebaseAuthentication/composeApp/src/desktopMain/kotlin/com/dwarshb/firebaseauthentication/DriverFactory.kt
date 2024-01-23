package com.dwarshb.firebaseauthentication

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("java.io.tmpdir"), "firebase.db")
        val driver: SqlDriver = JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}")
        Database.Schema.create(driver)
        return driver
    }
}