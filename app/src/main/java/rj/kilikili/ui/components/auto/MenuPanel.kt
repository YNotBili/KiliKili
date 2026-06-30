package rj.kilikili.ui.components.auto

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rj.kilikili.UiType
import rj.kilikili.uiType
import rj.kilikili.data.menu.MenuItem
import rj.kilikili.ui.components.wear.menu.MenuPanel as wearMenuPanel
import rj.kilikili.ui.components.phone.menu.MenuPanel as phoneMenuPanel

/** Auto: 菜单面板 — wear 端全屏 ScalingLazyColumn, phone 端 ModalNavigationDrawer。 */
@Composable
fun AppMenuPanel(
    modifier: Modifier = Modifier,
    menuItems: List<MenuItem>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit = {}
) = when (actualUiType) {
    UiType.WEAR -> wearMenuPanel(
        menuItems = menuItems,
        onSelect = onSelect,
        onDismiss = onDismiss
    )
    UiType.PHONE -> phoneMenuPanel(
        menuItems = menuItems,
        onSelect = onSelect,
        onDismiss = onDismiss,
        content = content
    )
}
