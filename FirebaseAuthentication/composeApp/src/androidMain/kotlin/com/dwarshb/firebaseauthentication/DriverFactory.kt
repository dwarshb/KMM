package com.dwarshb.firebaseauthentication

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DriverFactory(var appContext: Context) {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, appContext, "firebase.db")
    }
}