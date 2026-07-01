package rj.kilikili.ui.components.auto

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rj.kilikili.UiType
import rj.kilikili.actualUiType
import rj.kilikili.data.menu.MenuItem
import rj.kilikili.ui.components.wear.menu.MenuPanel as wearMenuPanel

/**
 * Auto: 菜单面板 — wear 端全屏 Chip 列表, phone 端只返回 drawer content。
 *
 * phone 端的 drawer content 由 [PhoneMenuDrawerContent] 提供, 由 MainScreen 的 [ModalNavigationDrawer] 包装。
 * wear 端全屏 panel, 受外层 AnimatedVisibility 控制显隐。
 */
@Composable
fun AppMenuPanel(
    modifier: Modifier = Modifier,
    menuItems: List<MenuItem>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit = {}
) {
    if (actualUiType == UiType.WEAR) {
        wearMenuPanel(
            menuItems = menuItems,
            onSelect = onSelect,
            onDismiss = onDismiss
        )
    }
    // phone 端: 由 MainScreen 用 ModalNavigationDrawer 包, 这里不渲染任何东西。
}