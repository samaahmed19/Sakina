package com.example.sakina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
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
import com.example.sakina.ui.Checklist.DailyResetWorker
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleDailyReset()
        setContent {
            val navController = rememberNavController()
            AppNavGraph(navController = navController)
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