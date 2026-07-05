package rj.kilikili.ui.objects

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import rj.kilikili.UiType
import rj.kilikili.actualUiType

/**
 * PiliPlus 风格的分类 Tab 行 — 仅 Phone 模式下渲染。
 *
 * Wear / FreshWear 调用时不渲染任何内容，直接返回。
 *
 * @param categories Tab 标签文字列表
 * @param selectedIndex 当前选中的 Tab 索引
 * @param onSelect Tab 选中回调
 */
@Composable
fun PhoneCategoryTabRow(
    categories: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    if (actualUiType != UiType.PHONE) return

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier.height(40.dp),
        edgePadding = 12.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    height = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        divider = {}
    ) {
        categories.forEachIndexed { index, title ->
            val selected = selectedIndex == index
            Tab(
                selected = selected,
                onClick = { onSelect(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            )
        }
    }
}
