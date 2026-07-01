package rj.kilikili.ui.components.phone.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rj.kilikili.R
import rj.kilikili.data.account.AccountManager
import rj.kilikili.data.menu.MenuItem

/**
 * Phone 端 drawer content — 返回 ModalDrawerSheet 内容, 由 MainScreen 外层 ModalNavigationDrawer 包装。
 *
 * 不包含 ModalNavigationDrawer, 这样 MainScreen 才能用同一个 drawerState 包 NavHost。
 *
 * @param drawerState MainScreen hoisted DrawerState
 * @param scope MainScreen hoisted CoroutineScope (用于 close drawer)
 * @param onSelect 菜单项点击, 路由字符串. 调用方负责导航 + 清栈.
 */
@Composable
fun PhoneMenuDrawerContent(
    menuItems: List<MenuItem>,
    drawerState: DrawerState,
    scope: CoroutineScope,
    onSelect: (String) -> Unit
) {
    val loggedIn = AccountManager.loggedIn()
    val filteredItems = menuItems.filter { item ->
        (!item.requireLoggedIn || loggedIn) &&
            (!item.requireNotLoggedIn || !loggedIn) &&
            !item.notMenuActivity
    }

    ModalDrawerSheet(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
    ) {
        DrawerHeader(loggedIn = loggedIn)
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
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
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun DrawerHeader(loggedIn: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (loggedIn) {
                    Icons.AutoMirrored.Outlined.Logout
                } else {
                    Icons.AutoMirrored.Outlined.Login
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "KiliKili",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (loggedIn) "已登录" else "未登录",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}