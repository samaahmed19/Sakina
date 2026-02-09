package com.example.sakina.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    NavItem(Screen.Home.route, "هوم", Icons.Default.Home),
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

    val barColor = Color(0xFF0F172A)
    val accentColor = Color(0xFF00FFD1)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        NavigationBar(
            modifier = modifier.height(60.dp),
            containerColor = barColor,
            contentColor = Color.White,
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    },
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
                        selectedIconColor = accentColor,
                        selectedTextColor = accentColor,
                        unselectedIconColor = Color.White.copy(alpha = 0.75f),
                        unselectedTextColor = Color.White.copy(alpha = 0.75f),
                        indicatorColor = accentColor.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}
