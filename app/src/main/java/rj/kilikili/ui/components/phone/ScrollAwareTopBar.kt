package rj.kilikili.ui.components.phone

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import rj.kilikili.ui.components.auto.AppScrollBehavior

/** Phone 端 ScrollAwareTopBar — 接受 [AppScrollBehavior] 抽象, 内部 cast 出 Material3 类型。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollAwareTopBar(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: AppScrollBehavior? = null,
    showBackIcon: Boolean = true,
    showMenuIcon: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val m3Behavior = (scrollBehavior as? PhoneScrollBehaviorAdapter)?.delegate
    TopAppBar(
        modifier = modifier.then(
            if (m3Behavior != null) Modifier.nestedScroll(m3Behavior.nestedScrollConnection)
            else Modifier
        ),
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (showBackIcon && onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                }
            }
        },
        actions = {
            if (showMenuIcon && onMenuClick != null) {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.MoreVert, contentDescription = "menu")
                }
            }
        },
        scrollBehavior = m3Behavior
    )
}
