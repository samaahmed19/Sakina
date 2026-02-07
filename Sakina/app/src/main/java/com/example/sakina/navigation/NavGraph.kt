package com.example.sakina.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sakina.ui.Azkar.azkar_list.AzkarListScreen
import com.example.sakina.ui.Azkar.azkar_details.AzkarDetailsScreen
import com.example.sakina.ui.Home.HomeScreen
import com.example.sakina.ui.Splash.SplashScreen
import com.example.sakina.navigation.Screen
import com.example.sakina.ui.HolyQuran.surah_list.SurahListScreen
import com.example.sakina.ui.Prayers.PrayerScreen
//import com.example.sakina.ui.Tasbeeh.TasbeehScreen
import com.example.sakina.ui.Checklist.ChecklistScreen
//import com.example.sakina.ui.Gwame3Dua.DuaListScreen








@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } } }) }

        //  Home Screen
        composable(Screen.Home.route) {
             HomeScreen(onAzkarCardClick = { navController.navigate(Screen.Categories.route) },
        onQuranCardClick = { navController.navigate(Screen.Quran.route) },
        onSalahCardClick = { navController.navigate(Screen.Salah.route) },
        onDuaCardClick = { navController.navigate(Screen.Dua.route) },
        onTasbeehCardClick = { navController.navigate(Screen.Tasbeeh.route) },
        onCheckCardClick = { navController.navigate(Screen.Checklist.route) })
        }

        //  Azkar List Screen
        composable(Screen.Categories.route) {
            AzkarListScreen(onCategoryClick = { categoryId ->
                navController.navigate(Screen.Details.createRoute(categoryId))
            })
        }

        // ِAzkar Details Screen
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            AzkarDetailsScreen(categoryId = categoryId)
        }
        //  Quran List Screen

        /*composable(Screen.Quran.route) {
            SurahListScreen()
        }*/

        //  Salah Screen
        composable(Screen.Salah.route) {
            PrayerScreen()
        }

        //  Tasbeeh Screen
       /* composable(Screen.Tasbeeh.route) {
            TasbeehScreen()
        }*/

        //  Checklist Screen
        composable(Screen.Checklist.route) {
            ChecklistScreen()
        }

        //  Dua Screen
       /* composable(Screen.Dua.route) {
            DuaListScreen()
        }*/
    }
}


