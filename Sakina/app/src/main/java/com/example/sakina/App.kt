package com.example.sakina

import android.app.Application
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
            dataInitializer.initAzkarIfNeeded()
            dataInitializer.initQuranIfNeeded()
            dataInitializer.initDuasIfNeeded()
            dataInitializer.initTasbeehIfNeeded()
        }
    }
}
