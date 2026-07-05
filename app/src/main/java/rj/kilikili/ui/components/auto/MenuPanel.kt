package rj.kilikili.ui.components.auto

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import rj.kilikili.UiType
import rj.kilikili.actualUiType
import rj.kilikili.data.menu.MenuItem
import rj.kilikili.ui.components.phone.menu.PhoneMenuDrawerContent
import rj.kilikili.ui.components.wear.menu.MenuPanel as wearMenuPanel

/**
 * Auto: 菜单面板 — wear 端全屏 Chip 列表, phone 端返回 drawer content。
 *
 * phone 端的 drawer content 由 [PhoneMenuDrawerContent] 提供, 由 MainScreen 的 [ModalNavigationDrawer] 包装。
 * wear 端全屏 panel, 受外层 AnimatedVisibility 控制显隐。
 *
 * @param drawerState phone 端需要, wear 端忽略。
 * @param scope phone 端需要 (用于 close drawer), wear 端忽略。
 */
@Composable
fun AppMenuPanel(
    modifier: Modifier = Modifier,
    menuItems: List<MenuItem>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit = {},
    drawerState: DrawerState? = null,
    scope: CoroutineScope? = null,
    onLoginClick: () -> Unit = {}
) {
    when (actualUiType) {
        UiType.WEAR, UiType.FRESHWEAR -> {
            wearMenuPanel(
                menuItems = menuItems,
                onSelect = onSelect,
                onDismiss = onDismiss
            )
        }
        UiType.PHONE -> {
            if (drawerState != null && scope != null) {
                PhoneMenuDrawerContent(
                    menuItems = menuItems,
                    drawerState = drawerState,
                    scope = scope,
                    onSelect = onSelect,
                    onLoginClick = onLoginClick
                )
            }
        }
    }
}
