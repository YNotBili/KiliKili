package rj.kilikili.ui.screens.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import androidx.wear.compose.materialcore.toVerticalPadding
import rj.kilikili.R
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppScrollBehavior
import rj.kilikili.ui.navigation.Screen

@Composable
fun SettingsScreen(
    navController: NavController,
    onMenuClick: () -> Unit = {}
) {
    val scrollState = rememberAppLazyListState(initialFirstVisibleItemIndex = 0)
    
    // Create ScrollBehavior for TopBar
    val scrollBehavior = rememberAppScrollBehavior()

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(id = R.string.settings),
            showBackIcon = false,
            showMenuIcon = true,
            onMenuClick = onMenuClick,
            scrollBehavior = scrollBehavior
        ),
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            ScalingLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = (scrollState as rj.kilikili.ui.components.wear.WearLazyListStateAdapter).delegate,
                contentPadding = paddingValues
            ) {
            item {
                SettingsCategory(title = stringResource(id = R.string.preference))
            }
            item {
                SettingsItem(
                    icon = Icons.Outlined.Palette,
                    title = stringResource(id = R.string.settings_ui),
                    onClick = { navController.navigate(Screen.UiSettings.route) }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Outlined.PlayArrow,
                    title = stringResource(id = R.string.settings_player),
                    onClick = { navController.navigate(Screen.PlayerSettings.route) }
                )
            }
            item {
                SettingsCategory(title = stringResource(id = R.string.advanced))
            }
            item {
                SettingsItem(
                    icon = Icons.Outlined.Build,
                    title = stringResource(id = R.string.developer_options),
                    onClick = { navController.navigate(Screen.DeveloperOptions.route) }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = stringResource(id = R.string.about),
                    onClick = { navController.navigate(Screen.About.route) }
                )
            }
            }
        }
    }
}