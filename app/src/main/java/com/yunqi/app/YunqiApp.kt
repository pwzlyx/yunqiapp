package com.yunqi.app

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yunqi.app.feature.calendar.CalendarRoute
import com.yunqi.app.domain.calendar.CalendarRecordType
import com.yunqi.app.feature.home.HomeRoute
import com.yunqi.app.feature.setup.PregnancySetupRoute
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
                        it.route?.substringBefore("?") == destination.route
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
                                contentDescription = stringResource(destination.labelResId),
                            )
                        },
                        label = { Text(stringResource(destination.labelResId)) },
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
                HomeRoute(
                    contentPadding = innerPadding,
                    onSetProfileClick = {
                        navController.navigate(InternalDestination.PregnancySetup.route)
                    },
                    onQuickRecordClick = { recordType ->
                        navController.navigate(TopLevelDestination.Calendar.routeFor(recordType)) {
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(
                route = TopLevelDestination.Calendar.routePattern,
                arguments = listOf(
                    navArgument(RECORD_TYPE_ARGUMENT) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) { entry ->
                CalendarRoute(
                    contentPadding = innerPadding,
                    initialRecordType = entry.arguments
                        ?.getString(RECORD_TYPE_ARGUMENT)
                        ?.toCalendarRecordTypeOrNull(),
                )
            }
            composable(TopLevelDestination.Trends.route) {
                TrendsRoute(
                    contentPadding = innerPadding,
                    onRecordClick = { recordType ->
                        navController.navigate(TopLevelDestination.Calendar.routeFor(recordType)) {
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(TopLevelDestination.Settings.route) {
                SettingsRoute(
                    contentPadding = innerPadding,
                    onPregnancyProfileClick = {
                        navController.navigate(InternalDestination.PregnancySetup.route)
                    },
                )
            }
            composable(InternalDestination.PregnancySetup.route) {
                PregnancySetupRoute(
                    contentPadding = innerPadding,
                    onProfileSaved = {
                        navController.navigate(TopLevelDestination.Home.route) {
                            popUpTo(TopLevelDestination.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}

private sealed class InternalDestination(val route: String) {
    data object PregnancySetup : InternalDestination("pregnancy_setup")
}

private const val RECORD_TYPE_ARGUMENT = "recordType"

private sealed class TopLevelDestination(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    data object Home : TopLevelDestination("home", R.string.nav_home, Icons.Rounded.Home)
    data object Calendar : TopLevelDestination("calendar", R.string.nav_calendar, Icons.Rounded.CalendarMonth) {
        val routePattern = "$route?$RECORD_TYPE_ARGUMENT={$RECORD_TYPE_ARGUMENT}"

        fun routeFor(recordType: CalendarRecordType): String = "$route?$RECORD_TYPE_ARGUMENT=${recordType.name}"
    }
    data object Trends : TopLevelDestination("trends", R.string.nav_trends, Icons.AutoMirrored.Rounded.ShowChart)
    data object Settings : TopLevelDestination("settings", R.string.nav_settings, Icons.Rounded.Person)
}

private fun String.toCalendarRecordTypeOrNull(): CalendarRecordType? =
    runCatching { CalendarRecordType.valueOf(this) }.getOrNull()
