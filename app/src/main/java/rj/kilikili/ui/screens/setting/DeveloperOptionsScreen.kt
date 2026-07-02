package rj.kilikili.ui.screens.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.AppSelectionDialog
import rj.kilikili.R
import rj.kilikili.data.proto.DanmakuSource
import rj.kilikili.data.setting.edit
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.dialog.AdaptDialog
import rj.kilikili.data.setting.LocalData

@Composable
fun DeveloperOptionsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var showClearSettingsDialog by remember { mutableStateOf(false) }
    var showDanmakuSourceDialog by remember { mutableStateOf(false) }

    val settings by LocalData.settingsStateFlow.collectAsState()
    val currentSource = settings?.playerSettings?.danmakuSource
        ?: DanmakuSource.DANMAKU_SOURCE_PROTOBUF

    val scrollState = rememberAppLazyListState()

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(id = R.string.developer_options),
            showBackIcon = true,
            onBackClick = { navController.popBackStack() }
        )
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AppLazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState,
                contentPadding = paddingValues
            ) {
                item {
                    SettingsCategory(title = stringResource(id = R.string.experimental_features))
                }

                item {
                    SettingsItem(
                        icon = Icons.Outlined.Science,
                        title = stringResource(id = R.string.danmaku_source),
                        summary = stringResource(id = R.string.danmaku_source_desc) +
                            " · " + danmakuSourceLabel(currentSource),
                        onClick = { showDanmakuSourceDialog = true }
                    )
                }

                item {
                    SettingsCategory(title = stringResource(id = R.string.data_management))
                }

                item {
                    SettingsItem(
                        icon = Icons.Outlined.Delete,
                        title = stringResource(id = R.string.clear_settings),
                        summary = stringResource(id = R.string.clear_settings_desc),
                        onClick = { showClearSettingsDialog = true }
                    )
                }
            }
        }
    }

    if (showClearSettingsDialog) {
        AdaptDialog(
            onDismissRequest = { showClearSettingsDialog = false },
            title = { Text(stringResource(id = R.string.clear_settings)) },
            text = { Text(stringResource(id = R.string.clear_settings_warning)) },
            confirmButton = { close ->
                TextButton(
                    onClick = {
                        viewModel.clearAllSettings()
                        close()
                    }
                ) {
                    Text(stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearSettingsDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    if (showDanmakuSourceDialog) {
        AppSelectionDialog(
            title = stringResource(id = R.string.danmaku_source),
            options = listOf(
                DanmakuSource.DANMAKU_SOURCE_PROTOBUF to stringResource(id = R.string.danmaku_source_protobuf),
                DanmakuSource.DANMAKU_SOURCE_XML to stringResource(id = R.string.danmaku_source_xml)
            ),
            currentValue = currentSource,
            onDismiss = { showDanmakuSourceDialog = false },
            onConfirm = { picked ->
                viewModel.updatePlayerSettings(
                    viewModel.settingsState.value?.playerSettings?.edit {
                        danmakuSource = picked
                    } ?: return@AppSelectionDialog
                )
                showDanmakuSourceDialog = false
            }
        )
    }
}

@Composable
private fun danmakuSourceLabel(source: DanmakuSource): String = when (source) {
    DanmakuSource.DANMAKU_SOURCE_PROTOBUF -> stringResource(id = R.string.danmaku_source_protobuf)
    DanmakuSource.DANMAKU_SOURCE_XML -> stringResource(id = R.string.danmaku_source_xml)
    else -> stringResource(id = R.string.danmaku_source_protobuf)
}

