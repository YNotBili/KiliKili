package rj.kilikili.ui.objects

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import rj.kilikili.UiType
import rj.kilikili.actualUiType

/**
 * 搜索按钮 — Phone 模式显示为 IconButton（搜索图标），Wear/FreshWear 模式不渲染。
 *
 * 用于放在 TopBar 的 actions slot 中。
 */
@Composable
fun SearchActionButton(onClick: () -> Unit) {
    if (actualUiType != UiType.PHONE) return

    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "搜索"
        )
    }
}
