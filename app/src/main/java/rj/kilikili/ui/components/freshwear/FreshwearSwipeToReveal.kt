package rj.kilikili.ui.components.freshwear

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.RevealState
import androidx.wear.compose.material3.RevealValue
import androidx.wear.compose.material3.SwipeToReveal
import androidx.wear.compose.material3.SwipeToRevealScope
import androidx.wear.compose.material3.rememberRevealState
import rj.kilikili.UiType
import rj.kilikili.actualUiType

/**
 * FRESHWEAR-only wrapper around the vendored [SwipeToReveal] composable.
 *
 * On [UiType.FRESHWEAR] it renders a swipe-to-reveal container exposing the
 * [primaryAction] slot. On [UiType.WEAR] and [UiType.PHONE] it renders [content]
 * directly with no swipe gesture, so callers can wrap items unconditionally.
 *
 * @param primaryAction The action button slot revealed by swiping (FRESHWEAR only).
 *   Use [SwipeToRevealScope.PrimaryActionButton] inside. To auto-dismiss after a
 *   click, call `revealState.animateTo(RevealValue.Covered)` inside the button's
 *   onClick handler.
 * @param onSwipePrimaryAction Callback invoked when the user performs a full swipe.
 * @param modifier Modifier applied to the outer container.
 * @param revealState Controls the reveal state; obtain via [rememberRevealState].
 * @param autoClose When `true`, automatically animates back to [RevealValue.Covered]
 *   after a full swipe triggers [onSwipePrimaryAction]. When `false`, the item stays
 *   revealed (useful for destructive actions like delete where the UI is removed).
 * @param content The main content of the item.
 */
@Composable
fun FreshwearSwipeToReveal(
    primaryAction: @Composable SwipeToRevealScope.() -> Unit,
    onSwipePrimaryAction: () -> Unit,
    modifier: Modifier = Modifier,
    revealState: RevealState = rememberRevealState(),
    autoClose: Boolean = true,
    content: @Composable () -> Unit,
) {
    when (actualUiType) {
        UiType.FRESHWEAR -> {
            SwipeToReveal(
                primaryAction = primaryAction,
                onSwipePrimaryAction = onSwipePrimaryAction,
                modifier = modifier,
                revealState = revealState,
                content = content,
            )

            if (autoClose) {
                // After a full swipe lands in a Revealed state, animate back to Covered.
                LaunchedEffect(revealState.currentValue) {
                    val v = revealState.currentValue
                    if (v == RevealValue.RightRevealed || v == RevealValue.LeftRevealed) {
                        revealState.animateTo(RevealValue.Covered)
                    }
                }
            }
        }
        else -> content()
    }
}
