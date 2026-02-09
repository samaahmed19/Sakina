package com.example.sakina.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    NavItem(Screen.Home.route, "الرئيسية", Icons.Default.Home),
    NavItem(Screen.Quran.route, "قرآن", Icons.Default.MenuBook),
    NavItem(Screen.Categories.route, "أذكار", Icons.Default.Favorite),
    NavItem(Screen.Tasbeeh.route, "تسبيح", Icons.Default.Star)
)

@Composable
fun SakinaBottomBar(
    navController: NavController,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Quran.route,
        Screen.Categories.route,
        Screen.Tasbeeh.route
    )

    if (!showBar) return

    NavigationBar(
        modifier = modifier,
        containerColor = androidx.compose.ui.graphics.Color(0xFF0F172A),
        contentColor = androidx.compose.ui.graphics.Color.White
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = androidx.compose.ui.graphics.Color(0xFF00FFD1),
                    selectedTextColor = androidx.compose.ui.graphics.Color(0xFF00FFD1),
                    unselectedIconColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                    indicatorColor = androidx.compose.ui.graphics.Color(0xFF00FFD1).copy(alpha = 0.2f)
                )
            )
        }
    }
}
