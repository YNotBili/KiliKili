package rj.kilikili.ui.objects

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import rj.kilikili.R
import rj.kilikili.api.BilibiliApiException

/**
 * ErrorDialog - 错误提示对话框组件
 *
 * 显示错误信息和恢复建议，提供重试机制。
 *
 * @param exception Bilibili API异常
 * @param onRetry 重试回调
 * @param onDismiss 关闭回调
 * @param showRetryButton 是否显示重试按钮
 * @param modifier Modifier
 */
@Composable
fun ErrorDialog(
    exception: BilibiliApiException?,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    showRetryButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (exception == null) return

    val shouldRetry = exception.shouldRetry()
    val needReLogin = exception.needReLogin()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 错误图标
                Image(
                    painter = painterResource(id = R.drawable.loading_2233_error),
                    contentDescription = "Error",
                    modifier = Modifier.size(80.dp)
                )

                // 错误类型标签
                ErrorTypeLabel(errorType = exception.errorType)

                // 错误消息
                Text(
                    text = exception.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // 恢复建议
                val suggestion = exception.getRecoverySuggestion()
                if (suggestion.isNotEmpty()) {
                    Text(
                        text = "建议：$suggestion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                // 错误码（可选显示）
                if (exception.code != Int.MIN_VALUE) {
                    Text(
                        text = "错误码: ${exception.code}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }

                // 操作按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    // 关闭按钮
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("关闭")
                    }

                    // 重试按钮（如果需要重试）
                    if (showRetryButton && shouldRetry) {
                        Button(
                            onClick = {
                                onDismiss()
                                onRetry()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("重试")
                        }
                    }

                    // 重新登录按钮（如果需要重新登录）
                    if (needReLogin) {
                        Button(
                            onClick = {
                                onDismiss()
                                // TODO: 跳转到登录页面
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("重新登录")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 错误类型标签
 */
@Composable
private fun ErrorTypeLabel(errorType: BilibiliApiException.ErrorType) {
    val (text, color) = when (errorType) {
        BilibiliApiException.ErrorType.RISK_CONTROL -> "风控错误" to MaterialTheme.colorScheme.error
        BilibiliApiException.ErrorType.AUTHENTICATION -> "认证错误" to MaterialTheme.colorScheme.error
        BilibiliApiException.ErrorType.NETWORK -> "网络错误" to MaterialTheme.colorScheme.tertiary
        BilibiliApiException.ErrorType.PARAM -> "参数错误" to MaterialTheme.colorScheme.secondary
        BilibiliApiException.ErrorType.BUSINESS -> "业务错误" to MaterialTheme.colorScheme.primary
        else -> "未知错误" to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * 简化的错误对话框，仅显示错误消息
 *
 * @param errorMessage 错误消息
 * @param onDismiss 关闭回调
 * @param modifier Modifier
 */
@Composable
fun SimpleErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 错误图标
                Image(
                    painter = painterResource(id = R.drawable.loading_2233_error),
                    contentDescription = "Error",
                    modifier = Modifier.size(80.dp)
                )

                // 错误消息
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                // 关闭按钮
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("关闭")
                }
            }
        }
    }
}