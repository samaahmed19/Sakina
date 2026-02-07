package com.example.sakina

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.sakina.data.source.DataInitializer

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var dataInitializer: DataInitializer

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {

            if (::dataInitializer.isInitialized) {

                safeInit("Azkar") { dataInitializer.initAzkarIfNeeded() }
                safeInit("Quran") { dataInitializer.initQuranIfNeeded() }
                safeInit("Duas") { dataInitializer.initDuasIfNeeded() }
                safeInit("Tasbeeh") { dataInitializer.initTasbeehIfNeeded() }
            }
        }
    }


    private suspend fun safeInit(name: String, block: suspend () -> Unit) {
        try {
            block()
            Log.d("DataInit", "$name initialized successfully ✅")
        } catch (e: Exception) {
            Log.e("DataInit", "❌ Error in $name: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }
}