package rj.kilikili.ui.components.auto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn as PhoneLazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rj.kilikili.UiType
import rj.kilikili.actualUiType
import rj.kilikili.ui.components.phone.PhoneLazyListScopeAdapter
import rj.kilikili.ui.components.phone.PhoneLazyListStateAdapter
import rj.kilikili.ui.components.wear.TransformingLazyListScopeAdapter
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn as WearTransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.lazy.TransformationSpec
import androidx.wear.compose.material3.lazy.rememberTransformationSpec

/**
 * 通用 LazyColumn 入口 — wear 端走 TransformingLazyColumn, phone 端走 LazyColumn。
 * screens 调用此函数不用关心 uiType。
 *
 * @param state 可选 [AppLazyListState], 传 null 时内部自己 remember
 * @param content 在 [AppLazyListScope] 内写 item/items, 不直接 import wear/phone 的 lazy 扩展
 * @param transformationSpec wear 端 TransformingLazyColumn 使用的 TransformationSpec,
 *        phone 端忽略。默认使用 [rememberTransformationSpec]。
 */
@Composable
fun AppLazyColumn(
    modifier: Modifier = Modifier,
    state: AppLazyListState? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    transformationSpec: TransformationSpec? = null,
    content: AppLazyListScope.() -> Unit,
) {
    if (actualUiType == UiType.WEAR) {
        val spec = transformationSpec ?: rememberTransformationSpec()
        val wearState = state?.asTransformingLazyColumnState()
            ?: rememberTransformingLazyColumnState()
        CompositionLocalProvider(LocalAppLazyTransformationSpec provides spec) {
            WearTransformingLazyColumn(
                modifier = modifier,
                state = wearState,
                contentPadding = contentPadding,
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
            ) {
                TransformingLazyListScopeAdapter(this, spec).content()
            }
        }
    } else {
        val phoneState = state?.asPhoneLazyListState() ?: rememberLazyListState()
        PhoneLazyColumn(
            modifier = modifier,
            state = phoneState,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
        ) {
            PhoneLazyListScopeAdapter(this).content()
        }
    }
}
