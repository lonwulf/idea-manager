package com.lonwulf.ideamanager.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

object DestinationsConstants {
    const val TASKS_SCREEN = "My Tasks"
    const val HOME_SCREEN = "Home"
    const val SETTINGS_SCREEN = "Settings"
    const val TASKS_DETAILS_SCREEN = "_TASKS_DETAILS_SCREEN"
    const val CREATE_TASKS_SCREEN = "Create New Task"
    const val STATISTICS_SCREEN = "Statistics"
}

sealed class Destinations(val route: String, val title: String) {
    object TaskDetailsScreen : Destinations(DestinationsConstants.TASKS_DETAILS_SCREEN, "Details")
    object CreateTasksScreen :
        Destinations(DestinationsConstants.CREATE_TASKS_SCREEN, "Create New Task")
}

sealed class TopLevelDestinations(val route: String, val icon: ImageVector, val title: String) {
    object HomeScreen :
        TopLevelDestinations(DestinationsConstants.HOME_SCREEN, Icons.Outlined.Home, "Home")

    object SettingsScreen : TopLevelDestinations(
        DestinationsConstants.SETTINGS_SCREEN,
        Icons.Outlined.Settings,
        "Settings"
    )

    object AllTasksScreen : TopLevelDestinations(
        DestinationsConstants.TASKS_SCREEN,
        Icons.Outlined.Build,
        "All Tasks"
    )

    object StatisticsScreen : TopLevelDestinations(
        DestinationsConstants.STATISTICS_SCREEN,
        Icons.Outlined.Info,
        "Statistics"
    )

}