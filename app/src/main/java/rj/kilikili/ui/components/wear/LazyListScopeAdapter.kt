package rj.kilikili.ui.components.wear

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import rj.kilikili.ui.components.auto.AppLazyListScope

/**
 * ScalingLazyListScope → AppLazyListScope 适配。
 *
 * 注: ScalingLazyListScope.item() 和 items(List<T>) 都没有 contentType 参数 (跟 LazyListScope 不一样),
 * 所以这些地方丢弃 contentType。
 */
@Stable
internal class WearLazyListScopeAdapter(
    private val delegate: ScalingLazyListScope
) : AppLazyListScope {

    override fun item(
        key: Any?,
        contentType: Any?,
        content: @Composable () -> Unit
    ) {
        delegate.item(key) {
            content()
        }
    }

    override fun items(
        count: Int,
        key: ((Int) -> Any)?,
        contentType: (Int) -> Any?,
        itemContent: @Composable (Int) -> Unit
    ) {
        delegate.items(count, key, contentType) { index ->
            itemContent(index)
        }
    }

    override fun <T> items(
        data: List<T>,
        key: ((T) -> Any)?,
        contentType: ((T) -> Any?)?,
        itemContent: @Composable (T) -> Unit
    ) {
        delegate.items(data, key) { item ->
            itemContent(item)
        }
    }
}
