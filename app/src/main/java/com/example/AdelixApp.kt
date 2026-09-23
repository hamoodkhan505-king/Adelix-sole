package com.example

import android.app.Application
import com.example.data.db.AppDatabase
import com.example.data.repository.AdelixRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AdelixApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { AdelixRepository(database) }

    companion object {
        lateinit var instance: AdelixApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
