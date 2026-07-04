package rj.kilikili.data.repository

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import rj.kilikili.api.BilibiliApiException
import com.huanli233.biliwebapi.exception.ErrorCodeDefinitions
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.ConnectException
import javax.net.ssl.SSLException
import java.net.SocketException

/**
 * BaseRepository集成测试
 *
 * 测试safeApiCall方法和网络异常转换逻辑
 */
class BaseRepositoryTest {

    /**
     * 测试用的Repository实现类
     */
    private class TestRepository : BaseRepository() {
        // 提供public访问方法用于测试
        public fun <T> testSafeApiCall(
            operation: String,
            block: () -> Result<T>
        ): Result<T> = safeApiCall(operation, block)
    }

    // ==================== safeApiCall成功测试 ====================

    @Test
    fun `safeApiCall returns success for successful operation`() {
        val repository = TestRepository()
        val result = repository.testSafeApiCall("testOperation") {
            Result.success("test data")
        }

        assertTrue(result.isSuccess)
        assertEquals("test data", result.getOrNull())
    }

    @Test
    fun `safeApiCall returns success with null data`() {
        val repository = TestRepository()
        val result = repository.testSafeApiCall<String?>("testOperation") {
            Result.success(null)
        }

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `safeApiCall handles Result failure correctly`() {
        val repository = TestRepository()
        val exception = BilibiliApiException(
            code = ErrorCodeDefinitions.NOT_LOGGED_IN,
            message = "请先登录",
            errorType = BilibiliApiException.ErrorType.AUTHENTICATION
        )
        val result = repository.testSafeApiCall<String>("testOperation") {
            Result.failure(exception)
        }

        assertTrue(result.isFailure)
        val failureException = result.exceptionOrNull()
        assertTrue(failureException is BilibiliApiException)
        assertEquals(ErrorCodeDefinitions.NOT_LOGGED_IN, (failureException as BilibiliApiException).code)
    }

    // ==================== 网络异常转换测试 ====================

    @Test
    fun `safeApiCall converts SocketTimeoutException to BilibiliApiException`() {
        val repository = TestRepository()
        val timeoutException = SocketTimeoutException("Connection timed out")
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw timeoutException
        }

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as BilibiliApiException
        assertEquals(ErrorCodeDefinitions.CONNECTION_TIMEOUT, exception.code)
        assertEquals("连接超时", exception.message)
        assertEquals(BilibiliApiException.ErrorType.NETWORK, exception.errorType)
        assertEquals(timeoutException, exception.cause)
    }

    @Test
    fun `safeApiCall converts UnknownHostException to BilibiliApiException`() {
        val repository = TestRepository()
        val unknownHostException = UnknownHostException("Unable to resolve host")
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw unknownHostException
        }

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as BilibiliApiException
        assertEquals(ErrorCodeDefinitions.DNS_RESOLVE_FAILED, exception.code)
        assertEquals("DNS解析失败", exception.message)
        assertEquals(BilibiliApiException.ErrorType.NETWORK, exception.errorType)
        assertEquals(unknownHostException, exception.cause)
    }

    @Test
    fun `safeApiCall converts ConnectException to BilibiliApiException`() {
        val repository = TestRepository()
        val connectException = ConnectException("Failed to connect")
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw connectException
        }

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as BilibiliApiException
        assertEquals(ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR, exception.code)
        assertEquals("网络连接失败", exception.message)
        assertEquals(BilibiliApiException.ErrorType.NETWORK, exception.errorType)
        assertEquals(connectException, exception.cause)
    }

    @Test
    fun `safeApiCall converts SSLException to BilibiliApiException`() {
        val repository = TestRepository()
        val sslException = SSLException("SSL handshake failed")
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw sslException
        }

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as BilibiliApiException
        assertEquals(ErrorCodeDefinitions.SSL_HANDSHAKE_FAILED, exception.code)
        assertEquals("SSL握手失败", exception.message)
        assertEquals(BilibiliApiException.ErrorType.NETWORK, exception.errorType)
        assertEquals(sslException, exception.cause)
    }

    @Test
    fun `safeApiCall converts SocketException to BilibiliApiException`() {
        val repository = TestRepository()
        val socketException = SocketException("Connection reset")
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw socketException
        }

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as BilibiliApiException
        assertEquals(ErrorCodeDefinitions.CONNECTION_RESET, exception.code)
        assertEquals("连接被重置", exception.message)
        assertEquals(BilibiliApiException.ErrorType.NETWORK, exception.errorType)
        assertEquals(socketException, exception.cause)
    }

    @Test
    fun `safeApiCall converts unknown exception to BilibiliApiException`() {
        val repository = TestRepository()
        val unknownException = RuntimeException("Unknown error occurred")
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw unknownException
        }

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as BilibiliApiException
        assertEquals(Int.MIN_VALUE, exception.code)
        assertEquals("Unknown error occurred", exception.message)
        assertEquals(BilibiliApiException.ErrorType.UNKNOWN, exception.errorType)
        assertEquals(unknownException, exception.cause)
    }

    @Test
    fun `safeApiCall preserves BilibiliApiException`() {
        val repository = TestRepository()
        val originalException = BilibiliApiException(
            code = ErrorCodeDefinitions.NOT_ENOUGH_COIN,
            message = "硬币不足",
            errorType = BilibiliApiException.ErrorType.BUSINESS
        )
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw originalException
        }

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as BilibiliApiException
        assertEquals(ErrorCodeDefinitions.NOT_ENOUGH_COIN, exception.code)
        assertEquals("硬币不足", exception.message)
        assertEquals(BilibiliApiException.ErrorType.BUSINESS, exception.errorType)
        assertEquals(originalException, exception)
    }

    // ==================== 错误恢复建议测试 ====================

    @Test
    fun `network error exception provides recovery suggestion`() {
        val repository = TestRepository()
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw SocketTimeoutException("timeout")
        }

        val exception = result.exceptionOrNull() as BilibiliApiException
        val suggestion = exception.getRecoverySuggestion()
        assertTrue(suggestion.isNotEmpty())
        assertTrue(suggestion.contains("重试") || suggestion.contains("检查"))
    }

    @Test
    fun `authentication error exception provides recovery suggestion`() {
        val repository = TestRepository()
        val authException = BilibiliApiException(
            code = ErrorCodeDefinitions.NOT_LOGGED_IN,
            message = "请先登录",
            errorType = BilibiliApiException.ErrorType.AUTHENTICATION
        )
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw authException
        }

        val exception = result.exceptionOrNull() as BilibiliApiException
        val suggestion = exception.getRecoverySuggestion()
        assertTrue(suggestion.isNotEmpty())
        assertTrue(suggestion.contains("登录"))
    }

    // ==================== 错误判断方法测试 ====================

    @Test
    fun `network exception should retry`() {
        val repository = TestRepository()
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw SocketTimeoutException("timeout")
        }

        val exception = result.exceptionOrNull() as BilibiliApiException
        assertTrue(exception.shouldRetry())
    }

    @Test
    fun `authentication exception need re-login`() {
        val repository = TestRepository()
        val authException = BilibiliApiException(
            code = ErrorCodeDefinitions.NOT_LOGGED_IN,
            message = "请先登录",
            errorType = BilibiliApiException.ErrorType.AUTHENTICATION
        )
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw authException
        }

        val exception = result.exceptionOrNull() as BilibiliApiException
        assertTrue(exception.needReLogin())
    }

    @Test
    fun `business exception is recoverable`() {
        val repository = TestRepository()
        val businessException = BilibiliApiException(
            code = ErrorCodeDefinitions.NOT_ENOUGH_COIN,
            message = "硬币不足",
            errorType = BilibiliApiException.ErrorType.BUSINESS
        )
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw businessException
        }

        val exception = result.exceptionOrNull() as BilibiliApiException
        assertTrue(exception.isRecoverable())
    }

    @Test
    fun `fatal error is not recoverable`() {
        val repository = TestRepository()
        val fatalException = BilibiliApiException(
            code = ErrorCodeDefinitions.ACCOUNT_BANNED,
            message = "账号已被封禁",
            errorType = BilibiliApiException.ErrorType.AUTHENTICATION
        )
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw fatalException
        }

        val exception = result.exceptionOrNull() as BilibiliApiException
        assertFalse(exception.isRecoverable())
    }

    // ==================== 错误类型推断测试 ====================

    @Test
    fun `ErrorType from code for network error`() {
        val errorType = BilibiliApiException.ErrorType.fromCode(ErrorCodeDefinitions.CONNECTION_TIMEOUT)
        assertEquals(BilibiliApiException.ErrorType.NETWORK, errorType)
    }

    @Test
    fun `ErrorType from code for authentication error`() {
        val errorType = BilibiliApiException.ErrorType.fromCode(ErrorCodeDefinitions.NOT_LOGGED_IN)
        assertEquals(BilibiliApiException.ErrorType.AUTHENTICATION, errorType)
    }

    @Test
    fun `ErrorType from code for business error`() {
        val errorType = BilibiliApiException.ErrorType.fromCode(ErrorCodeDefinitions.NOT_ENOUGH_COIN)
        assertEquals(BilibiliApiException.ErrorType.BUSINESS, errorType)
    }

    @Test
    fun `ErrorType from code for risk control error`() {
        val errorType = BilibiliApiException.ErrorType.fromCode(ErrorCodeDefinitions.RISK_CONTROL_FAILED)
        assertEquals(BilibiliApiException.ErrorType.RISK_CONTROL, errorType)
    }

    @Test
    fun `ErrorType from code for success`() {
        val errorType = BilibiliApiException.ErrorType.fromCode(ErrorCodeDefinitions.SUCCESS)
        assertEquals(BilibiliApiException.ErrorType.SUCCESS, errorType)
    }

    // ==================== BilibiliApiException createNetworkError测试 ====================

    @Test
    fun `createNetworkError produces correct exception`() {
        val cause = SocketTimeoutException("timeout")
        val exception = BilibiliApiException.createNetworkError(
            ErrorCodeDefinitions.CONNECTION_TIMEOUT,
            "连接超时",
            cause
        )

        assertEquals(ErrorCodeDefinitions.CONNECTION_TIMEOUT, exception.code)
        assertEquals("连接超时", exception.message)
        assertEquals(BilibiliApiException.ErrorType.NETWORK, exception.errorType)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `createNetworkError provides recovery suggestion`() {
        val exception = BilibiliApiException.createNetworkError(
            ErrorCodeDefinitions.DNS_RESOLVE_FAILED,
            "DNS解析失败",
            UnknownHostException("unknown host")
        )

        val suggestion = exception.getRecoverySuggestion()
        assertTrue(suggestion.isNotEmpty())
        assertTrue(suggestion.contains("DNS") || suggestion.contains("网络"))
    }

    // ==================== 边界情况测试 ====================

    @Test
    fun `safeApiCall handles exception without message`() {
        val repository = TestRepository()
        val exception = RuntimeException()
        val result = repository.testSafeApiCall<String>("testOperation") {
            throw exception
        }

        assertTrue(result.isFailure)
        val apiException = result.exceptionOrNull() as BilibiliApiException
        assertEquals("未知错误", apiException.message)
    }

    @Test
    fun `safeApiCall with complex operation name`() {
        val repository = TestRepository()
        val operationName = "fetchVideoDetail/video_12345"
        val result = repository.testSafeApiCall(operationName) {
            Result.success("video data")
        }

        assertTrue(result.isSuccess)
    }
}