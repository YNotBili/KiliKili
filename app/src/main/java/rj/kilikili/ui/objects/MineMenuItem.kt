package rj.kilikili.ui.objects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * "我的"页面菜单项数据。
 */
data class MineMenuItemData(
    val icon: ImageVector,
    val title: String,
    val route: String? = null,
)

/**
 * "我的"页面菜单项 — 列表项样式。
 */
@Composable
fun MineMenuItem(
    item: MineMenuItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
