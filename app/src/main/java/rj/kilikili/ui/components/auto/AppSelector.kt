package rj.kilikili.ui.components.auto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rj.kilikili.UiType
import rj.kilikili.actualUiType

/**
 * 单选选择器 — WEAR 端走水平 Chip 列表, PHONE 端走 Material3 下拉选择。
 *
 * @param options 选项列表 (id -> label)
 * @param selected 当前选中项
 * @param onSelected 选中回调
 * @param label 下拉框的 placeholder 标签
 */
@Composable
fun <T> AppSelector(
    options: List<Pair<T, String>>,
    selected: T,
    onSelected: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    when (actualUiType) {
        UiType.WEAR, UiType.FRESHWEAR -> WearSelector(options, selected, onSelected, label, modifier)
        UiType.PHONE -> PhoneSelector(options, selected, onSelected, label, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> PhoneSelector(
    options: List<Pair<T, String>>,
    selected: T,
    onSelected: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selected }?.second
        ?: options.firstOrNull()?.second.orEmpty()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (id, lbl) ->
                DropdownMenuItem(
                    text = { Text(lbl) },
                    onClick = {
                        onSelected(id)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> WearSelector(
    options: List<Pair<T, String>>,
    selected: T,
    onSelected: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(end = 4.dp, top = 8.dp)
            )
        }
        items(
            count = options.size,
            key = { i -> options[i].first.toString() }
        ) { i ->
            val (id, lbl) = options[i]
            FilterChip(
                selected = id == selected,
                onClick = { onSelected(id) },
                label = { Text(lbl, maxLines = 1) }
            )
        }
    }
}