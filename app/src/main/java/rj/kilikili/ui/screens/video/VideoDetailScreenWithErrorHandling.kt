package rj.kilikili.ui.screens.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rj.kilikili.api.BilibiliApiException
import rj.kilikili.ui.objects.ErrorDialog
import rj.kilikili.ui.objects.Loading
import rj.kilikili.ui.objects.LoadingState

/**
 * VideoDetailScreen错误处理示例
 *
 * 展示如何在关键页面集成ErrorDialog和重试机制
 */

/**
 * 错误处理状态
 */
data class ErrorHandlingState(
    val showDialog: Boolean = false,
    val exception: BilibiliApiException? = null
)

/**
 * 示例：在VideoDetailScreen中集成错误处理
 */
@Composable
fun VideoDetailScreenWithErrorHandling(
    viewModel: VideoDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle(initialValue = null)

    // 错误处理状态
    var errorState by remember { mutableStateOf(ErrorHandlingState()) }

    // 处理加载状态
    when {
        uiState.isLoading -> {
            Loading(
                state = LoadingState.Loading,
                modifier = modifier.fillMaxSize()
            )
        }
        uiState.error != null -> {
            // 显示错误界面
            Loading(
                state = LoadingState.Error,
                errorMessage = uiState.error,
                onRetry = { viewModel.fetchData() },
                modifier = modifier.fillMaxSize()
            )
        }
        else -> {
            // 正常显示内容
            Box(modifier = modifier.fillMaxSize()) {
                // 这里原本的VideoDetailScreen内容
                // VideoDetailContent(uiState = uiState, viewModel = viewModel)

                // 错误事件处理
                LaunchedEffect(events) {
                    events?.let { event ->
                        when (event) {
                            is VideoDetailEvent.LikeFailed -> {
                                // 将消息转换为BilibiliApiException
                                val exception = BilibiliApiException(
                                    code = Int.MIN_VALUE,
                                    message = event.message ?: "点赞失败",
                                    errorType = BilibiliApiException.ErrorType.BUSINESS
                                )
                                errorState = ErrorHandlingState(
                                    showDialog = true,
                                    exception = exception
                                )
                            }
                            is VideoDetailEvent.NotLoggedIn -> {
                                val exception = BilibiliApiException(
                                    code = -101,
                                    message = "请先登录",
                                    errorType = BilibiliApiException.ErrorType.AUTHENTICATION
                                )
                                errorState = ErrorHandlingState(
                                    showDialog = true,
                                    exception = exception
                                )
                            }
                            is VideoDetailEvent.OperationFailed -> {
                                val exception = BilibiliApiException(
                                    code = Int.MIN_VALUE,
                                    message = event.message ?: "操作失败",
                                    errorType = BilibiliApiException.ErrorType.BUSINESS
                                )
                                errorState = ErrorHandlingState(
                                    showDialog = true,
                                    exception = exception
                                )
                            }
                            // 其他成功事件不需要显示对话框
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    // 显示错误对话框
    if (errorState.showDialog && errorState.exception != null) {
        ErrorDialog(
            exception = errorState.exception,
            onRetry = {
                // 根据错误类型决定重试行为
                when (errorState.exception!!.errorType) {
                    BilibiliApiException.ErrorType.NETWORK -> {
                        viewModel.fetchData()
                    }
                    BilibiliApiException.ErrorType.AUTHENTICATION -> {
                        // TODO: 跳转到登录页面
                    }
                    else -> {
                        // 其他错误不需要重试
                    }
                }
            },
            onDismiss = {
                errorState = ErrorHandlingState()
            }
        )
    }
}

/**
 * 扩展函数：将异常转换为ErrorDialogState
 */
fun Throwable.toErrorDialogState(): ErrorHandlingState {
    val exception = when (this) {
        is BilibiliApiException -> this
        else -> BilibiliApiException(
            code = Int.MIN_VALUE,
            message = message ?: "未知错误",
            errorType = BilibiliApiException.ErrorType.UNKNOWN,
            cause = this
        )
    }
    return ErrorHandlingState(
        showDialog = true,
        exception = exception
    )
}