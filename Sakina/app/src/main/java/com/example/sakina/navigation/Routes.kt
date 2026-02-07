package com.example.sakina.navigation



sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    //Azkar
    object Categories : Screen("categories")
    object Details : Screen("details/{categoryId}") {
        fun createRoute(categoryId: String) = "details/$categoryId"
    }
    object Salah : Screen("Salah")
    object Tasbeeh : Screen("tasbeeh")
    object Checklist : Screen("check")
    object Dua : Screen("dua")
    object Quran : Screen("quran")
    object SurahDetails : Screen("surah_details/{surahId}/{surahName}/{ayahCount}") {
        fun createRoute(id: Int, name: String, count: Int): String {
            return "surah_details/$id/$name/$count"
        }
    }
}