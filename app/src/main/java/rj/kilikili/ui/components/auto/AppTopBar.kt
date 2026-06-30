package rj.kilikili.ui.components.auto

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rj.kilikili.UiType
import rj.kilikili.uiType
import rj.kilikili.ui.components.phone.ScrollAwareTopBar as phoneScrollAwareTopBar
import rj.kilikili.ui.components.wear.ScrollAwareTopBar as wearScrollAwareTopBar

/**
 * 统一 TopBar — wear 端走自家, phone 端走 Material3 TopAppBar。
 */
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackIcon: Boolean = true,
    showMenuIcon: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    when (actualUiType) {
        UiType.WEAR -> wearScrollAwareTopBar(
            title = title,
            modifier = modifier,
            showBackIcon = showBackIcon,
            showMenuIcon = showMenuIcon,
            onBackClick = onBackClick,
            onMenuClick = onMenuClick
        )
        UiType.PHONE -> phoneScrollAwareTopBar(
            title = title,
            modifier = modifier,
            showBackIcon = showBackIcon,
            showMenuIcon = showMenuIcon,
            onBackClick = onBackClick,
            onMenuClick = onMenuClick
        )
    }
}

/**
 * AppTopBar 函数变体 — 返回 `@Composable () -> Unit` 给 AppScreenScaffold topBar slot 用。
 */
@Composable
fun appTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackIcon: Boolean = true,
    showMenuIcon: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
): @Composable () -> Unit {
    if (actualUiType == UiType.WEAR) {
        return { AppTopBar(title, modifier, showBackIcon = showBackIcon, showMenuIcon = showMenuIcon, onBackClick = onBackClick, onMenuClick = onMenuClick) }
    } else {
        return { AppTopBar(title, modifier, showBackIcon = showBackIcon, showMenuIcon = showMenuIcon, onBackClick = onBackClick, onMenuClick = onMenuClick) }
    }
}
