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
        // LazyListScope 的 items(List<T>) 在这个版本里是 extension function，
        // 直接调用容易重载选错。用 count 版本按索引展开，保留 key/contentType。
        delegate.items(
            count = data.size,
            key = key?.let { k -> { index: Int -> k(data[index]) } },
            contentType = contentType?.let { c -> { index: Int -> c(data[index]) } } ?: { null }
        ) { index ->
            itemContent(data[index])
        }
    }
}
