package rj.kilikili.ui.objects

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import rj.kilikili.R

/**
 * Loading - 通用加载状态组件
 *
 * 从 ui/screens/recommend/LoadingView 抽象而来，提供统一的加载状态显示。
 * 支持 Loading、Error、Empty 三种状态。
 *
 * @param state 加载状态
 * @param errorMessage 错误消息（仅在 Error 状态时有效）
 * @param onRetry 重试回调（仅在 Error 状态时有效）
 * @param modifier Modifier
 */
@Composable
fun Loading(
    state: LoadingState,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (state) {
        LoadingState.Loading -> LoadingContent(modifier)
        LoadingState.Error -> ErrorContent(errorMessage, onRetry, modifier)
        LoadingState.Empty -> EmptyContent(modifier)
        LoadingState.Hidden -> Unit
    }
}

/**
 * 加载状态枚举
 */
enum class LoadingState {
    Loading,
    Hidden,
    Error,
    Empty
}

/**
 * 加载中内容
 */
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                    scaleIn(initialScale = 0.8f, animationSpec = tween(durationMillis = 300))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 错误内容
 */
@Composable
private fun ErrorContent(
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            Log.e("Loading", "Error: $errorMessage")
        }
    }

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
                modifier = Modifier
                    .padding(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onRetry)
                    .padding(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.loading_2233_error),
                    contentDescription = "Error",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "点击重试",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 空状态内容（由 EmptyState 组件提供）
 */
@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    EmptyState(modifier = modifier)
}