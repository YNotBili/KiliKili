package rj.kilikili.ui.components.auto

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import rj.kilikili.UiType
import rj.kilikili.ui.components.phone.PhoneScrollBehaviorAdapter
import rj.kilikili.ui.components.wear.rememberEnterAlwaysScrollBehavior

/**
 * 统一 rememberAppScrollBehavior — 返回 [AppScrollBehavior] 抽象。
 * WEAR 模式: wear 自家 rememberEnterAlwaysScrollBehavior() (本身实现 AppScrollBehavior)
 * PHONE 模式: Material3 TopAppBarDefaults.enterAlwaysScrollBehavior() 包成 adapter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAppScrollBehavior(): AppScrollBehavior = when (actualUiType) {
    UiType.WEAR -> rememberEnterAlwaysScrollBehavior()
    UiType.PHONE -> PhoneScrollBehaviorAdapter(TopAppBarDefaults.enterAlwaysScrollBehavior())
}
