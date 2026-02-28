package com.sama.sakina

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import com.sama.sakina.ui.theme.SakinaTheme
import com.sama.sakina.navigation.AppNavGraph
import com.sama.sakina.navigation.SakinaBottomBar
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import com.sama.sakina.ui.checklist.DailyResetWorker
import com.sama.sakina.utils.AlarmScheduler
import com.sama.sakina.utils.PrayerNotificationHelper
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(com.google.accompanist.permissions.ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {

    @Inject lateinit var prayerNotificationHelper: PrayerNotificationHelper
    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            if (::prayerNotificationHelper.isInitialized && ::alarmScheduler.isInitialized) {
                setupPreciseAlarmsAndService()
            }
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Setup alarms/notifications failed", e)
        }
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = AndroidColor.parseColor("#020617")
        window.navigationBarColor = AndroidColor.parseColor("#020617")
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        try {
            DailyResetWorker.schedule(this)
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "DailyResetWorker.schedule failed", e)
        }
        try {
            createNotificationChannel()
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "createNotificationChannel failed", e)
        }
        setContent {
            val navController = rememberNavController()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

                LaunchedEffect(Unit) {
                    if (!permissionState.status.isGranted) {
                        permissionState.launchPermissionRequest()
                    }
                }
            }
            SakinaTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF020617), Color(0xFF0F172A))
                            )
                        )
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        bottomBar = { SakinaBottomBar(navController = navController) }
                    ) { paddingValues ->
                        AppNavGraph(
                            navController = navController,
                            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                        )
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        } }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = "تنبيهات الأذكار"
            val descriptionText = "قناة مخصصة لتذكيرك بأذكار الصباح والمساء"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("AZKAR_NOTIFICATIONS", name, importance).apply {
                description = descriptionText
            }
            val prayerChannel = NotificationChannel(
                "PRAYER_NOTIFICATIONS",
                "تنبيهات الصلاة",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "قناة مخصصة لمواقيت الصلاة" }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            manager.createNotificationChannel(prayerChannel)
        }
    }

    private fun setupPreciseAlarmsAndService() {
        try {
            alarmScheduler.scheduleExactAzkar(7, 0, "morning")
            alarmScheduler.scheduleExactAzkar(19, 0, "evening")
            prayerNotificationHelper.updateNextPrayerNotification()
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "setupPreciseAlarmsAndService failed", e)
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}
