package rj.kilikili.ui.components.phone

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import rj.kilikili.ui.components.auto.AppLazyListScope

/** LazyListScope → AppLazyListScope 适配。 */
@Stable
internal class PhoneLazyListScopeAdapter(
    private val delegate: LazyListScope
) : AppLazyListScope {

    override fun item(
        key: Any?,
        contentType: Any?,
        content: @Composable () -> Unit
    ) {
        delegate.item(key, contentType) {
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
        delegate.items<T>(data, key, contentType) { item ->
            itemContent(item)
        }
    }
}
