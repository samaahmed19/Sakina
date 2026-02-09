package com.example.sakina

import android.Manifest
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sakina.ui.authentication.LoginScreen
import com.example.sakina.ui.Home.HomeScreen
import com.example.sakina.ui.settings.SettingsScreen
import com.example.sakina.ui.theme.SakinaTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.sakina.navigation.AppNavGraph
import com.example.sakina.navigation.SakinaBottomBar
import com.example.sakina.ui.Checklist.DailyResetWorker
import com.example.sakina.workers.DuaOfDayWorker
import com.example.sakina.workers.EveningAdhkarWorker
import com.example.sakina.workers.MorningAdhkarWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = AndroidColor.parseColor("#020617")
        window.navigationBarColor = AndroidColor.parseColor("#020617")
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        scheduleDailyReset()
        scheduleNotificationWorkers()
        setContent {
            val navController = rememberNavController()
            LaunchedEffect(Unit) {
                intent.getStringExtra("notification_target")?.let { target ->
                    navController.navigate(target) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                    intent.removeExtra("notification_target")
                }
            }
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color(0xFF020617),
                bottomBar = { SakinaBottomBar(navController = navController) }
            ) { paddingValues ->
                AppNavGraph(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .systemBarsPadding()
                )
            }
        }
    }
    private fun scheduleDailyReset() {
        val now = System.currentTimeMillis()
        val millisInDay = 24 * 60 * 60 * 1000

        val nextMidnight = ((now / millisInDay) + 1) * millisInDay
        val delay = nextMidnight - now

        val workRequest =
            PeriodicWorkRequestBuilder<DailyResetWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_task_reset",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun scheduleNotificationWorkers() {
        val workManager = WorkManager.getInstance(this)

        // 7 AM - أذكار الصباح
        val morningRequest = PeriodicWorkRequestBuilder<MorningAdhkarWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntil(7, 0), TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "morning_adhkar",
            ExistingPeriodicWorkPolicy.UPDATE,
            morningRequest
        )

        // 7 PM - أذكار المساء
        val eveningRequest = PeriodicWorkRequestBuilder<EveningAdhkarWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntil(19, 0), TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "evening_adhkar",
            ExistingPeriodicWorkPolicy.UPDATE,
            eveningRequest
        )

        // 12 PM - دعاء اليوم
        val duaRequest = PeriodicWorkRequestBuilder<DuaOfDayWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayUntil(12, 0), TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "dua_of_day",
            ExistingPeriodicWorkPolicy.UPDATE,
            duaRequest
        )
    }

    private fun delayUntil(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance()
        val now = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        var target = cal.timeInMillis
        if (target <= now) target += 24 * 60 * 60 * 1000
        return target - now
    }

}

@PreviewScreenSizes
@Composable
fun SakinaApp(

) {





    }


enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SakinaTheme {
        Greeting("Android")
    }
}