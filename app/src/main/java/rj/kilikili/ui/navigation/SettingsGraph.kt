package rj.kilikili.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.wear.compose.navigation.composable
import rj.kilikili.ui.screens.setting.AboutScreen
import rj.kilikili.ui.screens.setting.DeveloperOptionsScreen
import rj.kilikili.ui.screens.setting.PlayerSettingsScreen
import rj.kilikili.ui.screens.setting.SettingsScreen
import rj.kilikili.ui.screens.setting.ThemeColorScreen
import rj.kilikili.ui.screens.setting.UiSettingsScreen

fun NavGraphBuilder.settingsGraph(navController: NavController, onMenuClick: () -> Unit = {}) {
    composable(Screen.Settings.route) {
        SettingsScreen(
            navController = navController,
            onMenuClick = onMenuClick
        )
    }
    composable(Screen.UiSettings.route) {
        UiSettingsScreen(navController = navController)
    }
    composable(Screen.PlayerSettings.route) {
        PlayerSettingsScreen(navController = navController)
    }
    composable(Screen.About.route) {
        AboutScreen(navController = navController)
    }
    composable(Screen.ThemeColor.route) {
        ThemeColorScreen(navController = navController)
    }
    composable(Screen.ViewPreview.route) {
        Box(modifier = Modifier.fillMaxSize())
    }
    composable(Screen.DeveloperOptions.route) {
        DeveloperOptionsScreen(navController = navController)
    }
}