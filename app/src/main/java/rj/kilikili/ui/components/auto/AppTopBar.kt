package rj.kilikili.ui.components.auto

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rj.kilikili.UiType
import rj.kilikili.actualUiType
import rj.kilikili.ui.components.phone.ScrollAwareTopBar as phoneScrollAwareTopBar
import rj.kilikili.ui.components.wear.ScrollAwareTopBar as wearScrollAwareTopBar

/**
 * 统一 TopBar — wear 端走自家, phone 端走 Material3 MediumTopAppBar。
 * phone 端的 scrollBehavior 通过 [LocalPhoneTopBarScrollBehavior] CompositionLocal 自动获取。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackIcon: Boolean = true,
    showMenuIcon: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    when (actualUiType) {
        UiType.WEAR, UiType.FRESHWEAR -> wearScrollAwareTopBar(
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
            onMenuClick = onMenuClick,
            actions = actions
        )
    }
}

/**
 * AppTopBar 函数变体 — 返回 `@Composable () -> Unit` 给 AppScreenScaffold topBar slot 用。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackIcon: Boolean = true,
    showMenuIcon: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
): @Composable () -> Unit {
    return {
        AppTopBar(
            title = title,
            modifier = modifier,
            showBackIcon = showBackIcon,
            showMenuIcon = showMenuIcon,
            onBackClick = onBackClick,
            onMenuClick = onMenuClick,
            actions = actions
        )
    }
}
