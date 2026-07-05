package rj.kilikili.ui.screens.mine

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import rj.kilikili.R
import rj.kilikili.data.account.AccountManager
import rj.kilikili.ui.components.auto.AppLazyColumn
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.objects.MineMenuItem
import rj.kilikili.ui.objects.MineMenuItemData
import rj.kilikili.ui.navigation.Screen

/**
 * Phone 模式个人中心页面。
 *
 * 顶部操作按钮行：消息、设置（不含搜索）。
 * 功能列表分组：动态、下载管理、消息中心、关注直播、设置。
 * 不登录时隐藏登录相关项（动态、消息中心等），只显示基本项（下载管理、设置）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneMineScreen(
    onNavigateToDynamic: () -> Unit = {},
    onNavigateToDownload: () -> Unit = {},
    onNavigateToMessageCenter: () -> Unit = {},
    onNavigateToFollowedLive: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onMenuClick: () -> Unit = {},
) {
    val scrollState = rememberAppLazyListState()
    val isLoggedIn = AccountManager.loggedIn()

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = appTopBar(
            title = stringResource(R.string.phone_mine),
            showBackIcon = false,
            showMenuIcon = true,
            onMenuClick = onMenuClick,
        )
    ) { paddingValues ->
        AppLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = scrollState,
        ) {
            // 登录相关项 — 仅登录时显示
            if (isLoggedIn) {
                item {
                    SectionHeader(title = "个人中心")
                }
                item {
                    MineMenuItem(
                        item = MineMenuItemData(
                            icon = Icons.Filled.DynamicFeed,
                            title = stringResource(R.string.mine_dynamic),
                            route = Screen.Dynamic.route,
                        ),
                        onClick = onNavigateToDynamic,
                    )
                }
                item {
                    MineMenuItem(
                        item = MineMenuItemData(
                            icon = Icons.Filled.Email,
                            title = stringResource(R.string.mine_message_center),
                            route = Screen.MessageCenter.route,
                        ),
                        onClick = onNavigateToMessageCenter,
                    )
                }
                item {
                    MineMenuItem(
                        item = MineMenuItemData(
                            icon = Icons.Filled.LiveTv,
                            title = stringResource(R.string.mine_followed_live),
                            route = Screen.FollowedLive.route,
                        ),
                        onClick = onNavigateToFollowedLive,
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }

            // 基础项 — 始终显示
            item {
                SectionHeader(title = "工具")
            }
            item {
                MineMenuItem(
                    item = MineMenuItemData(
                        icon = Icons.Filled.Download,
                        title = stringResource(R.string.mine_download),
                        route = Screen.DownloadList.route,
                    ),
                    onClick = onNavigateToDownload,
                )
            }
            item {
                MineMenuItem(
                    item = MineMenuItemData(
                        icon = Icons.Filled.Settings,
                        title = stringResource(R.string.mine_settings),
                        route = Screen.Settings.route,
                    ),
                    onClick = onNavigateToSettings,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
    )
}
