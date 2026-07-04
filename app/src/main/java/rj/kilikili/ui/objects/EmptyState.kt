package rj.kilikili.ui.objects

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import rj.kilikili.R

/**
 * EmptyState - 通用空状态显示组件
 *
 * 提供统一的空状态显示界面，适用于列表无数据、搜索无结果等场景。
 *
 * @param message 自定义空状态消息（可选）
 * @param modifier Modifier
 * @param showImage 是否显示空状态图片（默认 true）
 */
@Composable
fun EmptyState(
    message: String? = null,
    modifier: Modifier = Modifier,
    showImage: Boolean = true
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                    scaleIn(initialScale = 0.9f, animationSpec = tween(durationMillis = 300))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                if (showImage) {
                    Image(
                        painter = painterResource(id = R.drawable.loading_2233_empty),
                        contentDescription = "Empty",
                        modifier = Modifier.size(120.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = message ?: stringResource(id = R.string.empty_tip),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}