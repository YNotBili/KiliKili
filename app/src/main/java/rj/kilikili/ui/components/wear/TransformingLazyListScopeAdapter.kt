package rj.kilikili.ui.components.wear

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnScope
import androidx.wear.compose.material3.lazy.TransformationSpec
import rj.kilikili.ui.components.auto.AppLazyListScope
import rj.kilikili.ui.components.auto.LocalAppLazyItemScope
import rj.kilikili.ui.components.auto.LocalAppLazyTransformationSpec

/**
 * [TransformingLazyColumnScope] → [AppLazyListScope] adapter.
 *
 * Each item lambda has [TransformingLazyColumnItemScope] as a receiver; we provide it
 * via [LocalAppLazyItemScope] and the [TransformationSpec] via [LocalAppLazyTransformationSpec],
 * so item composables can call `Modifier.transformedHeight(scope, spec)` directly.
 */
@Stable
internal class TransformingLazyListScopeAdapter(
    private val delegate: TransformingLazyColumnScope,
    private val spec: TransformationSpec,
) : AppLazyListScope {

    override fun item(
        key: Any?,
        contentType: Any?,
        content: @Composable () -> Unit,
    ) {
        delegate.item(key, contentType) {
            CompositionLocalProvider(
                LocalAppLazyItemScope provides this,
                LocalAppLazyTransformationSpec provides spec,
            ) {
                content()
            }
        }
    }

    override fun items(
        count: Int,
        key: ((Int) -> Any)?,
        contentType: (Int) -> Any?,
        itemContent: @Composable (Int) -> Unit,
    ) {
        delegate.items(
            count = count,
            key = key,
            contentType = { index: Int -> contentType(index) },
        ) { index ->
            CompositionLocalProvider(
                LocalAppLazyItemScope provides this,
                LocalAppLazyTransformationSpec provides spec,
            ) {
                itemContent(index)
            }
        }
    }

    override fun <T> items(
        data: List<T>,
        key: ((T) -> Any)?,
        contentType: ((T) -> Any?)?,
        itemContent: @Composable (T) -> Unit,
    ) {
        delegate.items(
            count = data.size,
            key = if (key != null) { index: Int -> key(data[index]) } else null,
            contentType = if (contentType != null) { index: Int -> contentType.invoke(data[index]) } else { _ -> null },
        ) { index ->
            CompositionLocalProvider(
                LocalAppLazyItemScope provides this,
                LocalAppLazyTransformationSpec provides spec,
            ) {
                itemContent(data[index])
            }
        }
    }

    override fun <T> itemsIndexed(
        data: List<T>,
        key: ((Int, T) -> Any)?,
        contentType: ((Int, T) -> Any?)?,
        itemContent: @Composable (Int, T) -> Unit,
    ) {
        delegate.items(
            count = data.size,
            key = if (key != null) { index: Int -> key(index, data[index]) } else null,
            contentType = if (contentType != null) { index: Int -> contentType(index, data[index]) } else { _ -> null },
        ) { index ->
            CompositionLocalProvider(
                LocalAppLazyItemScope provides this,
                LocalAppLazyTransformationSpec provides spec,
            ) {
                itemContent(index, data[index])
            }
        }
    }
}
