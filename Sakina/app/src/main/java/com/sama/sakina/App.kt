package com.sama.sakina

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.sama.sakina.data.source.DataInitializer

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var dataInitializer: DataInitializer

    @Inject
    lateinit var appScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        try {
            if (::appScope.isInitialized && ::dataInitializer.isInitialized) {
                appScope.launch {
                    safeInit("Azkar") { dataInitializer.initAzkarIfNeeded() }
                    safeInit("Quran") { dataInitializer.initQuranIfNeeded() }
                    safeInit("Duas") { dataInitializer.initDuasIfNeeded() }
                    safeInit("Tasbeeh") { dataInitializer.initTasbeehIfNeeded() }
                }
            }
        } catch (e: Exception) {
            Log.e("App", "onCreate init failed", e)
        }
    }

    private suspend fun safeInit(name: String, block: suspend () -> Unit) {
        try {
            block()
            Log.d("DataInit", "$name initialized successfully")
        } catch (e: Exception) {
            Log.e("DataInit", "Error in $name: ${e.localizedMessage}", e)
        }
    }
}