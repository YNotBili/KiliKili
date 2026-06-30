package rj.kilikili.ui.components.auto

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

/**
 * 统一 LazyList 作用域 — wear ScalingLazyListScope / phone LazyListScope 都适配成此接口。
 * screens 在 [AppLazyColumn] 的 content 里只调 item / items (Int) / items (List), 不直接 import
 * wear/phone 包, 编译期 uiType 切换底层。
 *
 * 参数名避开 [items] (跟 LazyListScope 的 items 方法同名, 重载选错) → 用 [data] / [count]。
 */
@Stable
interface AppLazyListScope {
    fun item(
        key: Any? = null,
        contentType: Any? = null,
        content: @Composable () -> Unit
    )

    fun items(
        count: Int,
        key: ((Int) -> Any)? = null,
        contentType: (Int) -> Any? = { null },
        itemContent: @Composable (Int) -> Unit
    )

    fun <T> items(
        data: List<T>,
        key: ((T) -> Any)? = null,
        contentType: ((T) -> Any?)? = null,
        itemContent: @Composable (T) -> Unit
    )

    fun <T> itemsIndexed(
        data: List<T>,
        key: ((Int, T) -> Any)? = null,
        contentType: ((Int, T) -> Any?)? = null,
        itemContent: @Composable (Int, T) -> Unit
    )
}
