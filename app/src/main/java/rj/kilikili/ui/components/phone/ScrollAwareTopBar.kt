package rj.kilikili.ui.components.phone

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Phone 端 ScrollAwareTopBar — 创建 Material3 TopAppBar (无外部 scrollBehavior)。 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollAwareTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBackIcon: Boolean = true,
    showMenuIcon: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            Row {
                if (showMenuIcon && onMenuClick != null) {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "menu")
                    }
                }
                if (showBackIcon && onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                }
            }
        },
        actions = {}
    )
}