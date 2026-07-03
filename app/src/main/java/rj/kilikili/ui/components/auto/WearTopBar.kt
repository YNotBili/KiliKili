package rj.kilikili.ui.components.auto

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import rj.kilikili.UiType
import rj.kilikili.actualUiType
import rj.kilikili.data.menu.MenuItem
import rj.kilikili.ui.components.wear.WearTopBar as wearWearTopBar
import rj.kilikili.ui.components.phone.ScrollAwareTopBar as phoneTopBar

/** Auto: WearTopBar 透传 — wear 端走自家 WearTopBar, phone 端走 Material3 TopAppBar 内部组件。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WearTopBar(
    title: String,
    showBackIcon: Boolean = true,
    showMenuIcon: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) = when (actualUiType) {
    UiType.WEAR, UiType.FRESHWEAR -> wearWearTopBar(
        title = title,
        showBackIcon = showBackIcon,
        showMenuIcon = showMenuIcon,
        onBackClick = onBackClick,
        onMenuClick = onMenuClick
    )
    UiType.PHONE -> phoneTopBar(
        title = title,
        showBackIcon = showBackIcon,
        showMenuIcon = showMenuIcon,
        onBackClick = onBackClick,
        onMenuClick = onMenuClick
    )
}
