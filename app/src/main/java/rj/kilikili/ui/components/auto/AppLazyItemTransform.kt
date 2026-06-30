package rj.kilikili.ui.components.auto

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnItemScope
import androidx.wear.compose.material3.lazy.TransformationSpec

/**
 * CompositionLocal holding the current [TransformingLazyColumnItemScope] inside
 * [AppLazyColumn]'s wear path. Item composables can read this within `@Composable` context
 * to access [TransformingLazyColumnItemScope.transformedHeight] directly.
 * On phone this is always `null`.
 */
val LocalAppLazyItemScope = staticCompositionLocalOf<TransformingLazyColumnItemScope?> { null }

/**
 * CompositionLocal providing the current [TransformationSpec] from [AppLazyColumn] so item
 * composables can pass it to [TransformingLazyColumnItemScope.transformedHeight].
 * On phone always `null`.
 */
val LocalAppLazyTransformationSpec = staticCompositionLocalOf<TransformationSpec?> { null }

/**
 * Modifier that applies [TransformationSpec]-based height transformation on wear
 * (via [TransformingLazyColumnItemScope.transformedHeight]), and is a noop on phone.
 *
 * Note: Because [LocalAppLazyItemScope] and [LocalAppLazyTransformationSpec] are composable-only,
 * item composables should read them directly inside their `@Composable` block and call
 * `Modifier.transformedHeight(scope, spec)`. This modifier is a pass-through kept for API
 * compatibility; actual transformation should be applied using the scope's API directly.
 */
fun Modifier.appTransformedHeight(): Modifier = this
