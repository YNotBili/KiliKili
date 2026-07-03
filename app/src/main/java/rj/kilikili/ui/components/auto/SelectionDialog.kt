package rj.kilikili.ui.components.auto

import androidx.compose.runtime.Composable
import rj.kilikili.UiType
import rj.kilikili.actualUiType
import rj.kilikili.ui.components.phone.SelectionDialog as phoneSelectionDialog
import rj.kilikili.ui.components.wear.SelectionDialog as wearSelectionDialog

/** Auto dispatch: 通用签名 <T> SelectionDialog — wear/phone 各自实现。 */
@Composable
fun <T> AppSelectionDialog(
    title: String,
    options: List<Pair<T, String>>,
    currentValue: T,
    onDismiss: () -> Unit,
    onConfirm: (T) -> Unit
) = when (actualUiType) {
    UiType.WEAR, UiType.FRESHWEAR -> wearSelectionDialog(title, options, currentValue, onDismiss, onConfirm)
    UiType.PHONE -> phoneSelectionDialog(title, options, currentValue, onDismiss, onConfirm)
}
