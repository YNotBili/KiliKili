package rj.kilikili.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import rj.kilikili.ui.navigation.appComposable
import rj.kilikili.ui.screens.setting.AboutScreen
import rj.kilikili.ui.screens.setting.DeveloperOptionsScreen
import rj.kilikili.ui.screens.setting.PlayerSettingsScreen
import rj.kilikili.ui.screens.setting.SettingsScreen
import rj.kilikili.ui.screens.setting.ThemeColorScreen
import rj.kilikili.ui.screens.setting.UiSettingsScreen

fun NavGraphBuilder.settingsGraph(navController: NavController, onMenuClick: () -> Unit = {}) {
    appComposable(Screen.Settings.route) {
        SettingsScreen(
            navController = navController,
            onMenuClick = onMenuClick
        )
    }
    appComposable(Screen.UiSettings.route) {
        UiSettingsScreen(navController = navController)
    }
    appComposable(Screen.PlayerSettings.route) {
        PlayerSettingsScreen(navController = navController)
    }
    appComposable(Screen.About.route) {
        AboutScreen(navController = navController)
    }
    appComposable(Screen.ThemeColor.route) {
        ThemeColorScreen(navController = navController)
    }
    appComposable(Screen.ViewPreview.route) {
        Box(modifier = Modifier.fillMaxSize())
    }
    appComposable(Screen.DeveloperOptions.route) {
        DeveloperOptionsScreen(navController = navController)
    }
}