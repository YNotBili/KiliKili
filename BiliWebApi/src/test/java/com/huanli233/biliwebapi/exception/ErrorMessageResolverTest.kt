package com.huanli233.biliwebapi.exception

import org.junit.Assert.*
import org.junit.Test

/**
 * ErrorMessageResolver单元测试
 *
 * 测试错误码映射、恢复建议、判断方法等核心功能
 */
class ErrorMessageResolverTest {

    // ==================== 错误码映射测试 ====================

    @Test
    fun `resolve success code`() {
        val result = ErrorMessageResolver.resolve(ErrorCodeDefinitions.SUCCESS)
        assertEquals("操作成功", result)
    }

    @Test
    fun `resolve null code returns unknown error`() {
        val result = ErrorMessageResolver.resolve(null)
        assertEquals("未知错误", result)
    }

    @Test
    fun `resolve unknown code returns fallback message`() {
        val result = ErrorMessageResolver.resolve(99999)
        assertEquals("操作失败 (错误码: 99999)", result)
    }

    @Test
    fun `resolve risk control errors`() {
        val riskControlFailed = ErrorMessageResolver.resolve(ErrorCodeDefinitions.RISK_CONTROL_FAILED)
        assertEquals("风控校验失败，请刷新页面或重新登录", riskControlFailed)

        val riskControlBlocked = ErrorMessageResolver.resolve(ErrorCodeDefinitions.RISK_CONTROL_BLOCKED)
        assertEquals("请求被拦截，请稍后再试", riskControlBlocked)
    }

    @Test
    fun `resolve authentication errors`() {
        val notLoggedIn = ErrorMessageResolver.resolve(ErrorCodeDefinitions.NOT_LOGGED_IN)
        assertEquals("请先登录", notLoggedIn)

        val notLoggedInAlt = ErrorMessageResolver.resolve(ErrorCodeDefinitions.NOT_LOGGED_IN_ALT)
        assertEquals("请先登录", notLoggedInAlt)

        val accessDenied = ErrorMessageResolver.resolve(ErrorCodeDefinitions.ACCESS_DENIED)
        assertEquals("权限不足，无法访问", accessDenied)

        val accountBanned = ErrorMessageResolver.resolve(ErrorCodeDefinitions.ACCOUNT_BANNED)
        assertEquals("账号已被封禁", accountBanned)

        val tokenInvalid = ErrorMessageResolver.resolve(ErrorCodeDefinitions.TOKEN_INVALID)
        assertEquals("登录状态失效，请重新登录", tokenInvalid)
    }

    @Test
    fun `resolve param errors`() {
        val badRequest = ErrorMessageResolver.resolve(ErrorCodeDefinitions.BAD_REQUEST)
        assertEquals("请求参数错误", badRequest)

        val csrfError = ErrorMessageResolver.resolve(ErrorCodeDefinitions.CSRF_TOKEN_ERROR)
        assertEquals("CSRF验证失败，请刷新页面", csrfError)
    }

    @Test
    fun `resolve business errors`() {
        val notEnoughCoin = ErrorMessageResolver.resolve(ErrorCodeDefinitions.NOT_ENOUGH_COIN)
        assertEquals("硬币不足", notEnoughCoin)

        val alreadyDone = ErrorMessageResolver.resolve(ErrorCodeDefinitions.ALREADY_DONE)
        assertEquals("已经操作过", alreadyDone)

        val videoNotExist = ErrorMessageResolver.resolve(ErrorCodeDefinitions.VIDEO_NOT_EXIST)
        assertEquals("视频不存在或已删除", videoNotExist)
    }

    @Test
    fun `resolve network errors`() {
        val networkError = ErrorMessageResolver.resolve(ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR)
        assertEquals("网络连接失败", networkError)

        val dnsError = ErrorMessageResolver.resolve(ErrorCodeDefinitions.DNS_RESOLVE_FAILED)
        assertEquals("DNS解析失败", dnsError)

        val timeout = ErrorMessageResolver.resolve(ErrorCodeDefinitions.CONNECTION_TIMEOUT)
        assertEquals("连接超时", timeout)
    }

    // ==================== resolveOrNull测试 ====================

    @Test
    fun `resolveOrNull returns null for success`() {
        val result = ErrorMessageResolver.resolveOrNull(ErrorCodeDefinitions.SUCCESS)
        assertNull(result)
    }

    @Test
    fun `resolveOrNull returns null for null code`() {
        val result = ErrorMessageResolver.resolveOrNull(null)
        assertNull(result)
    }

    @Test
    fun `resolveOrNull returns message for error code`() {
        val result = ErrorMessageResolver.resolveOrNull(ErrorCodeDefinitions.NOT_LOGGED_IN)
        assertEquals("请先登录", result)
    }

    // ==================== 恢复建议测试 ====================

    @Test
    fun `get recovery suggestion for null code`() {
        val result = ErrorMessageResolver.getRecoverySuggestion(null)
        assertEquals("请稍后再试", result)
    }

    @Test
    fun `get recovery suggestion for authentication errors`() {
        val notLoggedIn = ErrorMessageResolver.getRecoverySuggestion(ErrorCodeDefinitions.NOT_LOGGED_IN)
        assertEquals("请登录后再操作", notLoggedIn)

        val accountBanned = ErrorMessageResolver.getRecoverySuggestion(ErrorCodeDefinitions.ACCOUNT_BANNED)
        assertEquals("请联系客服了解封禁原因", accountBanned)
    }

    @Test
    fun `get recovery suggestion for network errors`() {
        val networkError = ErrorMessageResolver.getRecoverySuggestion(ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR)
        assertEquals("请检查网络连接", networkError)

        val timeout = ErrorMessageResolver.getRecoverySuggestion(ErrorCodeDefinitions.CONNECTION_TIMEOUT)
        assertEquals("请检查网络连接或稍后重试", timeout)
    }

    @Test
    fun `get recovery suggestion for unknown code`() {
        val result = ErrorMessageResolver.getRecoverySuggestion(99999)
        assertEquals("请稍后再试", result)
    }

    // ==================== shouldRetry测试 ====================

    @Test
    fun `should retry for null code returns false`() {
        val result = ErrorMessageResolver.shouldRetry(null)
        assertFalse(result)
    }

    @Test
    fun `should retry for network errors`() {
        assertTrue(ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR))
        assertTrue(ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.DNS_RESOLVE_FAILED))
        assertTrue(ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.CONNECTION_TIMEOUT))
        assertTrue(ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.SERVICE_UNAVAILABLE))
        assertTrue(ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.GATEWAY_TIMEOUT))
    }

    @Test
    fun `should retry for frequency too fast`() {
        val result = ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.FREQUENCY_TOO_FAST)
        assertTrue(result)
    }

    @Test
    fun `should retry for risk control blocked`() {
        val result = ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.RISK_CONTROL_BLOCKED)
        assertTrue(result)
    }

    @Test
    fun `should not retry for authentication errors`() {
        assertFalse(ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.NOT_LOGGED_IN))
        assertFalse(ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.ACCOUNT_BANNED))
    }

    @Test
    fun `should not retry for business errors`() {
        assertFalse(ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.NOT_ENOUGH_COIN))
        assertFalse(ErrorMessageResolver.shouldRetry(ErrorCodeDefinitions.VIDEO_NOT_EXIST))
    }

    // ==================== needReLogin测试 ====================

    @Test
    fun `need re-login for not logged in`() {
        assertTrue(ErrorMessageResolver.needReLogin(ErrorCodeDefinitions.NOT_LOGGED_IN))
        assertTrue(ErrorMessageResolver.needReLogin(ErrorCodeDefinitions.NOT_LOGGED_IN_ALT))
    }

    @Test
    fun `need re-login for token invalid`() {
        val result = ErrorMessageResolver.needReLogin(ErrorCodeDefinitions.TOKEN_INVALID)
        assertTrue(result)
    }

    @Test
    fun `need re-login for account banned`() {
        val result = ErrorMessageResolver.needReLogin(ErrorCodeDefinitions.ACCOUNT_BANNED)
        assertTrue(result)
    }

    @Test
    fun `no need re-login for other errors`() {
        assertFalse(ErrorMessageResolver.needReLogin(ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR))
        assertFalse(ErrorMessageResolver.needReLogin(ErrorCodeDefinitions.NOT_ENOUGH_COIN))
        assertFalse(ErrorMessageResolver.needReLogin(null))
    }

    // ==================== isRecoverable测试 ====================

    @Test
    fun `null code is not recoverable`() {
        val result = ErrorMessageResolver.isRecoverable(null)
        assertFalse(result)
    }

    @Test
    fun `fatal errors are not recoverable`() {
        assertFalse(ErrorMessageResolver.isRecoverable(ErrorCodeDefinitions.ACCOUNT_BANNED))
        assertFalse(ErrorMessageResolver.isRecoverable(ErrorCodeDefinitions.VIDEO_NOT_EXIST))
        assertFalse(ErrorMessageResolver.isRecoverable(ErrorCodeDefinitions.RESOURCE_NOT_FOUND))
    }

    @Test
    fun `other errors are recoverable`() {
        assertTrue(ErrorMessageResolver.isRecoverable(ErrorCodeDefinitions.NOT_LOGGED_IN))
        assertTrue(ErrorMessageResolver.isRecoverable(ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR))
        assertTrue(ErrorMessageResolver.isRecoverable(ErrorCodeDefinitions.NOT_ENOUGH_COIN))
    }

    // ==================== getFullErrorMessage测试 ====================

    @Test
    fun `get full error message includes suggestion`() {
        val result = ErrorMessageResolver.getFullErrorMessage(ErrorCodeDefinitions.NOT_LOGGED_IN)
        assertTrue(result.contains("请先登录"))
        assertTrue(result.contains("建议："))
        assertTrue(result.contains("请登录后再操作"))
    }

    @Test
    fun `get full error message for unknown code`() {
        val result = ErrorMessageResolver.getFullErrorMessage(null)
        assertEquals("未知错误\n建议：请稍后再试", result)
    }

    @Test
    fun `get full error message format check`() {
        val result = ErrorMessageResolver.getFullErrorMessage(ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR)
        assertTrue(result.startsWith("网络连接失败"))
        assertTrue(result.contains("\n建议："))
        assertTrue(result.endsWith("请检查网络连接"))
    }

    // ==================== ErrorCodeDefinitions辅助方法测试 ====================

    @Test
    fun `isSuccess for success code`() {
        assertTrue(ErrorCodeDefinitions.isSuccess(ErrorCodeDefinitions.SUCCESS))
        assertFalse(ErrorCodeDefinitions.isSuccess(ErrorCodeDefinitions.NOT_LOGGED_IN))
        assertFalse(ErrorCodeDefinitions.isSuccess(null))
    }

    @Test
    fun `isRiskControlError for risk control codes`() {
        assertTrue(ErrorCodeDefinitions.isRiskControlError(ErrorCodeDefinitions.RISK_CONTROL_FAILED))
        assertTrue(ErrorCodeDefinitions.isRiskControlError(ErrorCodeDefinitions.RISK_CONTROL_BLOCKED))
        assertFalse(ErrorCodeDefinitions.isRiskControlError(ErrorCodeDefinitions.NOT_LOGGED_IN))
        assertFalse(ErrorCodeDefinitions.isRiskControlError(null))
    }

    @Test
    fun `isAuthenticationError for authentication codes`() {
        assertTrue(ErrorCodeDefinitions.isAuthenticationError(ErrorCodeDefinitions.NOT_LOGGED_IN))
        assertTrue(ErrorCodeDefinitions.isAuthenticationError(ErrorCodeDefinitions.NOT_LOGGED_IN_ALT))
        assertTrue(ErrorCodeDefinitions.isAuthenticationError(ErrorCodeDefinitions.ACCESS_DENIED))
        assertTrue(ErrorCodeDefinitions.isAuthenticationError(ErrorCodeDefinitions.ACCOUNT_BANNED))
        assertTrue(ErrorCodeDefinitions.isAuthenticationError(ErrorCodeDefinitions.TOKEN_INVALID))
        assertFalse(ErrorCodeDefinitions.isAuthenticationError(ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR))
        assertFalse(ErrorCodeDefinitions.isAuthenticationError(null))
    }

    @Test
    fun `isNetworkError for network codes`() {
        assertTrue(ErrorCodeDefinitions.isNetworkError(ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR))
        assertTrue(ErrorCodeDefinitions.isNetworkError(ErrorCodeDefinitions.DNS_RESOLVE_FAILED))
        assertTrue(ErrorCodeDefinitions.isNetworkError(ErrorCodeDefinitions.CONNECTION_TIMEOUT))
        assertTrue(ErrorCodeDefinitions.isNetworkError(ErrorCodeDefinitions.SSL_HANDSHAKE_FAILED))
        assertTrue(ErrorCodeDefinitions.isNetworkError(ErrorCodeDefinitions.SERVER_INTERNAL_ERROR))
        assertTrue(ErrorCodeDefinitions.isNetworkError(ErrorCodeDefinitions.GATEWAY_ERROR))
        assertTrue(ErrorCodeDefinitions.isNetworkError(ErrorCodeDefinitions.SERVICE_UNAVAILABLE))
        assertTrue(ErrorCodeDefinitions.isNetworkError(ErrorCodeDefinitions.GATEWAY_TIMEOUT))
        assertFalse(ErrorCodeDefinitions.isNetworkError(ErrorCodeDefinitions.NOT_LOGGED_IN))
        assertFalse(ErrorCodeDefinitions.isNetworkError(null))
    }
}