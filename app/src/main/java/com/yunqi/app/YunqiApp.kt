package com.yunqi.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yunqi.app.feature.calendar.CalendarRoute
import com.yunqi.app.feature.home.HomeRoute
import com.yunqi.app.feature.settings.SettingsRoute
import com.yunqi.app.feature.trends.TrendsRoute

@Composable
fun YunqiApp() {
    val navController = rememberNavController()
    val destinations = listOf(
        TopLevelDestination.Home,
        TopLevelDestination.Calendar,
        TopLevelDestination.Trends,
        TopLevelDestination.Settings,
    )
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == destination.route
                    } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Home.route,
            modifier = Modifier,
        ) {
            composable(TopLevelDestination.Home.route) {
                HomeRoute(contentPadding = innerPadding)
            }
            composable(TopLevelDestination.Calendar.route) {
                CalendarRoute(contentPadding = innerPadding)
            }
            composable(TopLevelDestination.Trends.route) {
                TrendsRoute(contentPadding = innerPadding)
            }
            composable(TopLevelDestination.Settings.route) {
                SettingsRoute(contentPadding = innerPadding)
            }
        }
    }
}

private sealed class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    data object Home : TopLevelDestination("home", "首页", Icons.Rounded.Home)
    data object Calendar : TopLevelDestination("calendar", "日历", Icons.Rounded.CalendarMonth)
    data object Trends : TopLevelDestination("trends", "趋势", Icons.Rounded.ShowChart)
    data object Settings : TopLevelDestination("settings", "我的", Icons.Rounded.Person)
}

