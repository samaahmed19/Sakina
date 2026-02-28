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
import javax.inject.Inject
import android.app.AlarmManager
import android.content.Intent
import android.provider.Settings


@AndroidEntryPoint
@OptIn(com.google.accompanist.permissions.ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setContent {
            val navController = rememberNavController()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionState =
                    rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

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
                != PackageManager.PERMISSION_GRANTED
            ) {

                requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
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
}}
