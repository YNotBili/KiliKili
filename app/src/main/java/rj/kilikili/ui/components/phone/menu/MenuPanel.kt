package rj.kilikili.ui.components.phone.menu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import rj.kilikili.R
import rj.kilikili.data.account.AccountManager
import rj.kilikili.data.menu.MenuItem
import rj.kilikili.data.menu.menuItem

/** Phone 端 MenuPanel — ModalNavigationDrawer 侧边栏形式。 */
@Composable
fun MenuPanel(
    menuItems: List<MenuItem>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    val loggedIn = AccountManager.loggedIn()
    val filteredItems = menuItems.filter { item ->
        (!item.requireLoggedIn || loggedIn) && (!item.requireNotLoggedIn || !loggedIn) && !item.notMenuActivity
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // 如果外部是闭的，不强制 Open
        if (!drawerState.isOpen) drawerState.open()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "KiliKili",
                    modifier = Modifier.padding(24.dp)
                )
                filteredItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(stringResource(item.title)) },
                        icon = { Icon(item.icon, contentDescription = null) },
                        selected = false,
                        onClick = {
                            onSelect(item.destination)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        },
        content = content
    )

    // 对外通知关闭 — 监听 drawerState.isClosed
    LaunchedEffect(drawerState.isClosed) {
        if (drawerState.isClosed) onDismiss()
    }
}

@Preview
@Composable
fun MenuPanelPreview() {
    MenuPanel(menuItems = listOf(
        menuItem(id = "1", destination = "recommend", title = R.string.recommend, icon = Icons.AutoMirrored.Outlined.PlaylistPlay),
        menuItem(id = "2", destination = "setting", title = R.string.settings, icon = Icons.Default.Settings),
    ), onSelect = {}, onDismiss = {})
}
