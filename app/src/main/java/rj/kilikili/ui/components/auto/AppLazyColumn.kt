package rj.kilikili.ui.components.auto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn as PhoneLazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rj.kilikili.UiType
import rj.kilikili.ui.components.phone.PhoneLazyListScopeAdapter
import rj.kilikili.ui.components.phone.PhoneLazyListStateAdapter
import rj.kilikili.ui.components.wear.WearLazyListScopeAdapter
import rj.kilikili.ui.components.wear.WearLazyListStateAdapter
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn as WearScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState

/**
 * 通用 LazyColumn 入口 — wear 端走 ScalingLazyColumn, phone 端走 LazyColumn。
 * screens 调用此函数不用关心 uiType。
 *
 * @param state 可选 [AppLazyListState], 传 null 时内部自己 remember
 * @param content 在 [AppLazyListScope] 内写 item/items, 不直接 import wear/phone 的 lazy 扩展
 */
@Composable
fun AppLazyColumn(
    modifier: Modifier = Modifier,
    state: AppLazyListState? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: AppLazyListScope.() -> Unit
) {
    if (actualUiType == UiType.WEAR) {
        val wearState = state?.asScalingLazyListState() ?: rememberScalingLazyListState()
        WearScalingLazyColumn(
            modifier = modifier,
            state = wearState,
            contentPadding = contentPadding,
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            WearLazyListScopeAdapter(this).content()
        }
    } else {
        val phoneState = state?.asPhoneLazyListState() ?: rememberLazyListState()
        PhoneLazyColumn(
            modifier = modifier,
            state = phoneState,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) {
            PhoneLazyListScopeAdapter(this).content()
        }
    }
}
