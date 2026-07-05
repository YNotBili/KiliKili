package rj.kilikili.ui.components.phone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import rj.kilikili.ui.navigation.Screen

private data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Recommend.route,
        label = "推荐",
        selectedIcon = Icons.Filled.PlayCircle,
        unselectedIcon = Icons.Outlined.PlayCircle,
    ),
    BottomNavItem(
        route = Screen.PhoneMine.route,
        label = "我的",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
    ),
)

@Composable
fun PhoneBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(item.label) },
            )
        }
    }
}
