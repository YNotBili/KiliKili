package rj.kilikili.ui.components.phone

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rj.kilikili.ui.components.auto.LocalPhoneTopBarScrollBehavior

/**
 * Phone 端 ScrollAwareTopBar — Material3 MediumTopAppBar，
 * 大字标题（headlineSmall）向下滚动时折叠为小标题（titleLarge）。
 *
 * scrollBehavior 优先使用外部传入的 [scrollBehavior] 参数，
 * 其次从 [LocalPhoneTopBarScrollBehavior] CompositionLocal 获取（由 AppScreenScaffold 提供），
 * 最后回退到内部自建的 enterAlwaysScrollBehavior。
 *
 * 使用 enterAlwaysScrollBehavior 而非 exitUntilCollapsedScrollBehavior，
 * 确保下拉时 TopBar 先展开，展开完毕后再触发列表下拉刷新，避免手势冲突。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollAwareTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackIcon: Boolean = true,
    showMenuIcon: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    navigationIcon: @Composable () -> Unit = {
        Row {
            if (showMenuIcon && onMenuClick != null) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "menu")
                }
            }
            if (showBackIcon && onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                }
            }
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    topAppBarState: TopAppBarState = rememberTopAppBarState()
) {
    val localBehavior = LocalPhoneTopBarScrollBehavior.current
    val resolvedBehavior = scrollBehavior
        ?: localBehavior
        ?: TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    MediumTopAppBar(
        modifier = modifier,
        title = { Text(title, style = MaterialTheme.typography.headlineSmall) },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors,
        scrollBehavior = resolvedBehavior,
    )
}
