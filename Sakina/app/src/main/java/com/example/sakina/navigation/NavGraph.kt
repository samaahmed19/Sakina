package com.example.sakina.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sakina.ui.Azkar.azkar_list.ZikrListScreen
import com.example.sakina.ui.Azkar.azkar_details.ZikrDetailsScreen
import com.example.sakina.ui.Checklist.ChecklistScreen
import com.example.sakina.ui.Home.HomeScreen
import com.example.sakina.ui.Splash.SplashScreen
import com.example.sakina.ui.HolyQuran.surah_details.SurahDetailsScreen
import com.example.sakina.ui.HolyQuran.surah_list.SurahListScreen
import com.example.sakina.ui.Prayers.PrayerScreen
import com.example.sakina.ui.Tasbeeh.TasbeehScreen
import com.example.sakina.navigation.Screen
import com.example.sakina.ui.Gwame3Dua.DuaListScreen
import com.example.sakina.ui.Gwame3Dua.dua_details.DuaDetailsScreen
import com.example.sakina.ui.authentication.LoginScreen




@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        //  Home Screen
        composable(Screen.Home.route) {
             HomeScreen(onAzkarCardClick = { navController.navigate(Screen.Categories.route) },
        onQuranCardClick = { navController.navigate(Screen.Quran.route) },
                 onNavigateToSurahDetails = { id, name, count ->
                     navController.navigate(Screen.SurahDetails.createRoute(id, name, count))
                 },
        onSalahCardClick = { navController.navigate(Screen.Salah.route) },
        onDuaCardClick = { navController.navigate(Screen.Dua.route) },
        onTasbeehCardClick = { navController.navigate(Screen.Tasbeeh.route) },
        onCheckCardClick = { navController.navigate(Screen.Checklist.route) })
        }

        //  Azkar List Screen
        composable(Screen.Categories.route) {
            ZikrListScreen(onCategoryClick = { categoryId ->
                navController.navigate("azkar_details/$categoryId")
            })
        }

        // ِAzkar Details Screen
        composable(
            route = "azkar_details/{catId}",
            arguments = listOf(navArgument("catId") { type = NavType.StringType })
        ) { backStackEntry ->
            val catId = backStackEntry.arguments?.getString("catId") ?: ""

            ZikrDetailsScreen(
                categoryId = catId,
                onBackClick = { navController.popBackStack() }
            )
        }
        //  Quran List Screen
        composable(Screen.Quran.route) {
            SurahListScreen(
                onSurahClick = { surah ->

                    navController.navigate(
                        Screen.SurahDetails.createRoute(
                            id = surah.number,
                            name = surah.nameAr,
                            count = surah.ayahCount
                        )
                    )
                }
            )
        }

        // 2. Surah Details Screen
        composable(
            route = Screen.SurahDetails.route,
            arguments = listOf(
                navArgument("surahId") { type = NavType.IntType },
                navArgument("surahName") { type = NavType.StringType },
                navArgument("ayahCount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val surahId = backStackEntry.arguments?.getInt("surahId") ?: 1
            val surahName = backStackEntry.arguments?.getString("surahName") ?: ""
            val ayahCount = backStackEntry.arguments?.getInt("ayahCount") ?: 0

            SurahDetailsScreen(
                surahId = surahId,
                surahName = surahName,
                ayahCount = ayahCount
            )
        }
        //  Salah Screen
        composable(Screen.Salah.route) {
            PrayerScreen()
        }

        //  Tasbeeh Screen
         composable(Screen.Tasbeeh.route) {
             TasbeehScreen()
         }

        //  Checklist Screen
        composable(Screen.Checklist.route) {
            ChecklistScreen()
        }

        //  Dua Screen
        composable(Screen.Dua.route) {
            DuaListScreen(onCategoryClick = { category ->
                navController.navigate(Screen.DuaDetails.createRoute(category.id, category.title))
            })
        }

        // DuaDetails Screen
        composable(
            route = Screen.DuaDetails.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType },
                navArgument("categoryTitle") { type = NavType.StringType }
            )
        ) {
            DuaDetailsScreen(onBack = { navController.popBackStack() })
        }
    }

}
