package rj.kilikili.data.repository

import android.util.Log
import rj.kilikili.api.BilibiliApiException
import com.huanli233.biliwebapi.exception.ErrorMessageResolver
import com.huanli233.biliwebapi.exception.ErrorCodeDefinitions

/**
 * Repository基类
 *
 * 提供统一的错误处理和日志记录机制
 *
 * @author KiliKili Team
 * @since 1.0.0
 */
abstract class BaseRepository {

    companion object {
        private const val TAG = "Repository"
    }

    /**
     * 记录API错误日志
     *
     * @param operation 操作名称
     * @param exception API异常
     */
    protected fun logApiError(operation: String, exception: BilibiliApiException) {
        Log.e(TAG, "API Error in $operation:")
        Log.e(TAG, "  Code: ${exception.code}")
        Log.e(TAG, "  Type: ${exception.errorType}")
        Log.e(TAG, "  Message: ${exception.message}")
        Log.e(TAG, "  Recovery: ${exception.getRecoverySuggestion()}")

        if (exception.cause != null) {
            Log.e(TAG, "  Cause: ${exception.cause}")
            Log.e(TAG, "  StackTrace:", exception.cause)
        }

        // 根据错误类型提供诊断建议
        when (exception.errorType) {
            BilibiliApiException.ErrorType.RISK_CONTROL -> {
                Log.w(TAG, "  Diagnosis: Risk control triggered - check WBI signature, headers, or request frequency")
                Log.w(TAG, "  Solution: Refresh page, re-login, or adjust request parameters")
            }
            BilibiliApiException.ErrorType.AUTHENTICATION -> {
                Log.w(TAG, "  Diagnosis: Authentication failed - user may need to re-login")
                Log.w(TAG, "  Solution: Prompt user to login again or check cookie validity")
            }
            BilibiliApiException.ErrorType.NETWORK -> {
                Log.w(TAG, "  Diagnosis: Network error - check connectivity and server status")
                Log.w(TAG, "  Solution: Retry with backoff or prompt user to check network")
            }
            BilibiliApiException.ErrorType.PARAM -> {
                Log.w(TAG, "  Diagnosis: Request parameter error - check request format and headers")
                Log.w(TAG, "  Solution: Validate parameters and ensure proper request construction")
            }
            BilibiliApiException.ErrorType.BUSINESS -> {
                Log.w(TAG, "  Diagnosis: Business logic error - operation not allowed or resource issue")
                Log.w(TAG, "  Solution: Inform user and suggest appropriate recovery action")
            }
            else -> {
                Log.w(TAG, "  Diagnosis: Unknown error type")
            }
        }
    }

    /**
     * 记录API成功日志
     *
     * @param operation 操作名称
     * @param dataSize 返回数据大小（可选）
     */
    protected fun logApiSuccess(operation: String, dataSize: Int? = null) {
        if (dataSize != null) {
            Log.d(TAG, "API Success: $operation - returned $dataSize items")
        } else {
            Log.d(TAG, "API Success: $operation")
        }
    }

    /**
     * 记录网络请求开始日志
     *
     * @param operation 操作名称
     * @param params 请求参数（可选）
     */
    protected fun logApiRequest(operation: String, params: Map<String, Any>? = null) {
        if (params != null && params.isNotEmpty()) {
            Log.d(TAG, "API Request: $operation with params: $params")
        } else {
            Log.d(TAG, "API Request: $operation")
        }
    }

    /**
     * 处理API错误结果
     *
     * 自动记录错误日志并返回Result
     *
     * @param operation 操作名称
     * @param result API结果
     * @return 处理后的Result
     */
    protected inline fun <T> handleApiResult(
        operation: String,
        result: Result<T>
    ): Result<T> {
        result.onFailure { exception ->
            if (exception is BilibiliApiException) {
                logApiError(operation, exception)
            } else {
                Log.e(TAG, "Non-API Error in $operation:", exception)
            }
        }
        return result
    }

    /**
     * 安全执行API操作
     *
     * 自动捕获异常并转换为BilibiliApiException
     *
     * @param operation 操作名称
     * @param block API操作
     * @return Result
     */
    protected inline fun <T> safeApiCall(
        operation: String,
        block: () -> Result<T>
    ): Result<T> {
        logApiRequest(operation)
        return try {
            val result = block()
            result.onSuccess {
                logApiSuccess(operation)
            }
            handleApiResult(operation, result)
        } catch (e: Exception) {
            val exception = when (e) {
                is BilibiliApiException -> e
                is java.net.SocketTimeoutException -> {
                    BilibiliApiException.createNetworkError(
                        ErrorCodeDefinitions.CONNECTION_TIMEOUT,
                        "连接超时",
                        e
                    )
                }
                is java.net.UnknownHostException -> {
                    BilibiliApiException.createNetworkError(
                        ErrorCodeDefinitions.DNS_RESOLVE_FAILED,
                        "DNS解析失败",
                        e
                    )
                }
                is java.net.ConnectException -> {
                    BilibiliApiException.createNetworkError(
                        ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR,
                        "网络连接失败",
                        e
                    )
                }
                is javax.net.ssl.SSLException -> {
                    BilibiliApiException.createNetworkError(
                        ErrorCodeDefinitions.SSL_HANDSHAKE_FAILED,
                        "SSL握手失败",
                        e
                    )
                }
                is java.net.SocketException -> {
                    BilibiliApiException.createNetworkError(
                        ErrorCodeDefinitions.CONNECTION_RESET,
                        "连接被重置",
                        e
                    )
                }
                else -> {
                    BilibiliApiException(
                        code = Int.MIN_VALUE,
                        message = e.message ?: "未知错误",
                        errorType = BilibiliApiException.ErrorType.UNKNOWN,
                        cause = e
                    )
                }
            }
            logApiError(operation, exception)
            Result.failure(exception)
        }
    }
}