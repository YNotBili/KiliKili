package rj.kilikili.ui.components.auto

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal 持有 phone 端 MediumTopAppBar 的 [TopAppBarScrollBehavior]，
 * 由 [AppScreenScaffold] 在 PHONE 分支内 provide，供 [rj.kilikili.ui.components.phone.ScrollAwareTopBar]
 * 读取以实现 nested-scroll 联动。
 */
@OptIn(ExperimentalMaterial3Api::class)
val LocalPhoneTopBarScrollBehavior = staticCompositionLocalOf<TopAppBarScrollBehavior?> { null }
