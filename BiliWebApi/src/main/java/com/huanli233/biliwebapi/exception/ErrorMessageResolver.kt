package com.huanli233.biliwebapi.exception

/**
 * 错误消息解析器
 *
 * 将Bilibili API错误码转换为用户友好的中文提示消息，并提供恢复建议。
 * 参考：
 * - PiliPlus错误处理机制
 * - Bilibili API公共错误码文档
 *
 * @author KiliKili Team
 * @since 1.0.0
 */
object ErrorMessageResolver {

    /**
     * 错误消息映射表
     */
    private val errorMessages: Map<Int, String> = mapOf(
        // 成功状态
        ErrorCodeDefinitions.SUCCESS to "操作成功",

        // 风控错误
        ErrorCodeDefinitions.RISK_CONTROL_FAILED to "风控校验失败，请刷新页面或重新登录",
        ErrorCodeDefinitions.RISK_CONTROL_BLOCKED to "请求被拦截，请稍后再试",

        // 认证错误
        ErrorCodeDefinitions.NOT_LOGGED_IN to "请先登录",
        ErrorCodeDefinitions.NOT_LOGGED_IN_ALT to "请先登录",
        ErrorCodeDefinitions.ACCESS_DENIED to "权限不足，无法访问",
        ErrorCodeDefinitions.RESOURCE_NOT_FOUND to "资源不存在",
        ErrorCodeDefinitions.ACCOUNT_BANNED to "账号已被封禁",
        ErrorCodeDefinitions.TOKEN_INVALID to "登录状态失效，请重新登录",

        // 参数错误
        ErrorCodeDefinitions.BAD_REQUEST to "请求参数错误",
        ErrorCodeDefinitions.REQUEST_HEADER_ERROR to "请求头缺失或签名无效",
        ErrorCodeDefinitions.CSRF_TOKEN_ERROR to "CSRF验证失败，请刷新页面",
        ErrorCodeDefinitions.PARAM_OUT_OF_RANGE to "参数超出范围",

        // 业务错误
        ErrorCodeDefinitions.NOT_ENOUGH_COIN to "硬币不足",
        ErrorCodeDefinitions.NOT_ENOUGH_BALANCE to "余额不足",
        ErrorCodeDefinitions.OUT_OF_LIMIT to "操作已达上限",
        ErrorCodeDefinitions.ALREADY_DONE to "已经操作过",
        ErrorCodeDefinitions.DUPLICATED_OPERATION to "请勿重复操作",
        ErrorCodeDefinitions.SENSITIVE_CONTENT to "内容包含敏感词，请修改后重试",
        ErrorCodeDefinitions.FREQUENCY_TOO_FAST to "操作过于频繁，请稍后再试",
        ErrorCodeDefinitions.FOLLOW_SELF_FORBIDDEN to "不能关注自己",
        ErrorCodeDefinitions.ALREADY_FOLLOWED to "已经关注过",
        ErrorCodeDefinitions.NOT_FOLLOWED to "未关注该用户",
        ErrorCodeDefinitions.BLOCKED_BY_USER to "对方设置了隐私权限",
        ErrorCodeDefinitions.VIDEO_NOT_EXIST to "视频不存在或已删除",
        ErrorCodeDefinitions.COMMENT_DELETED to "评论已删除",
        ErrorCodeDefinitions.NO_PERMISSION_TO_DELETE to "无权限删除",
        ErrorCodeDefinitions.CONTENT_UNDER_REVIEW to "内容审核中，请稍后再试",
        ErrorCodeDefinitions.COMMENT_CLOSED to "评论功能已关闭",

        // 网络错误
        ErrorCodeDefinitions.SERVER_INTERNAL_ERROR to "服务器内部错误",
        ErrorCodeDefinitions.GATEWAY_ERROR to "网关错误",
        ErrorCodeDefinitions.SERVICE_UNAVAILABLE to "服务暂不可用",
        ErrorCodeDefinitions.GATEWAY_TIMEOUT to "请求超时",
        ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR to "网络连接失败",
        ErrorCodeDefinitions.DNS_RESOLVE_FAILED to "DNS解析失败",
        ErrorCodeDefinitions.CONNECTION_TIMEOUT to "连接超时",
        ErrorCodeDefinitions.SSL_HANDSHAKE_FAILED to "SSL握手失败",
        ErrorCodeDefinitions.CERTIFICATE_ERROR to "证书验证失败",
        ErrorCodeDefinitions.CONNECTION_RESET to "连接被重置",
        ErrorCodeDefinitions.UNKNOWN_NETWORK_ERROR to "网络异常"
    )

    /**
     * 恢复建议映射表
     */
    private val recoverySuggestions: Map<Int, String> = mapOf(
        // 风控错误
        ErrorCodeDefinitions.RISK_CONTROL_FAILED to "请尝试刷新页面或重新登录",
        ErrorCodeDefinitions.RISK_CONTROL_BLOCKED to "请等待一段时间后再尝试",

        // 认证错误
        ErrorCodeDefinitions.NOT_LOGGED_IN to "请登录后再操作",
        ErrorCodeDefinitions.NOT_LOGGED_IN_ALT to "请登录后再操作",
        ErrorCodeDefinitions.ACCESS_DENIED to "请检查是否有权限访问该内容",
        ErrorCodeDefinitions.RESOURCE_NOT_FOUND to "请检查资源是否存在或已删除",
        ErrorCodeDefinitions.ACCOUNT_BANNED to "请联系客服了解封禁原因",
        ErrorCodeDefinitions.TOKEN_INVALID to "请重新登录",

        // 参数错误
        ErrorCodeDefinitions.BAD_REQUEST to "请检查输入参数是否正确",
        ErrorCodeDefinitions.REQUEST_HEADER_ERROR to "请刷新页面或重启应用",
        ErrorCodeDefinitions.CSRF_TOKEN_ERROR to "请刷新页面",
        ErrorCodeDefinitions.PARAM_OUT_OF_RANGE to "请调整参数范围",

        // 业务错误
        ErrorCodeDefinitions.NOT_ENOUGH_COIN to "请获取更多硬币后再操作",
        ErrorCodeDefinitions.NOT_ENOUGH_BALANCE to "请充值后再操作",
        ErrorCodeDefinitions.OUT_OF_LIMIT to "今日操作已达上限，明天再试",
        ErrorCodeDefinitions.ALREADY_DONE to "无需重复操作",
        ErrorCodeDefinitions.DUPLICATED_OPERATION to "请勿重复操作",
        ErrorCodeDefinitions.SENSITIVE_CONTENT to "请修改内容后重新提交",
        ErrorCodeDefinitions.FREQUENCY_TOO_FAST to "请稍后再试",
        ErrorCodeDefinitions.FOLLOW_SELF_FORBIDDEN to "无法关注自己",
        ErrorCodeDefinitions.ALREADY_FOLLOWED to "已经关注了该用户",
        ErrorCodeDefinitions.NOT_FOLLOWED to "请先关注该用户",
        ErrorCodeDefinitions.BLOCKED_BY_USER to "对方隐私设置不允许此操作",
        ErrorCodeDefinitions.VIDEO_NOT_EXIST to "视频可能已删除或不存在",
        ErrorCodeDefinitions.COMMENT_DELETED to "评论已被删除",
        ErrorCodeDefinitions.NO_PERMISSION_TO_DELETE to "只能删除自己的内容",
        ErrorCodeDefinitions.CONTENT_UNDER_REVIEW to "请等待审核完成",
        ErrorCodeDefinitions.COMMENT_CLOSED to "该内容评论功能已关闭",

        // 网络错误
        ErrorCodeDefinitions.SERVER_INTERNAL_ERROR to "请稍后再试或联系客服",
        ErrorCodeDefinitions.GATEWAY_ERROR to "请稍后再试",
        ErrorCodeDefinitions.SERVICE_UNAVAILABLE to "服务正在维护，请稍后再试",
        ErrorCodeDefinitions.GATEWAY_TIMEOUT to "请检查网络连接后重试",
        ErrorCodeDefinitions.NETWORK_CONNECTION_ERROR to "请检查网络连接",
        ErrorCodeDefinitions.DNS_RESOLVE_FAILED to "请检查DNS设置或网络连接",
        ErrorCodeDefinitions.CONNECTION_TIMEOUT to "请检查网络连接或稍后重试",
        ErrorCodeDefinitions.SSL_HANDSHAKE_FAILED to "请检查网络安全设置",
        ErrorCodeDefinitions.CERTIFICATE_ERROR to "请检查证书设置",
        ErrorCodeDefinitions.CONNECTION_RESET to "请检查网络稳定性",
        ErrorCodeDefinitions.UNKNOWN_NETWORK_ERROR to "请检查网络连接"
    )

    /**
     * 解析错误码为用户友好的消息
     *
     * @param code 错误码
     * @return 用户友好的错误消息
     */
    fun resolve(code: Int?): String {
        if (code == null) return "未知错误"
        return errorMessages[code] ?: "操作失败 (错误码: $code)"
    }

    /**
     * 解析错误码为用户友好的消息，成功时返回null
     *
     * @param code 错误码
     * @return 用户友好的错误消息，成功时返回null
     */
    fun resolveOrNull(code: Int?): String? {
        if (code == null || code == ErrorCodeDefinitions.SUCCESS) return null
        return resolve(code)
    }

    /**
     * 获取恢复建议
     *
     * @param code 错误码
     * @return 恢复建议
     */
    fun getRecoverySuggestion(code: Int?): String {
        if (code == null) return "请稍后再试"
        return recoverySuggestions[code] ?: "请稍后再试"
    }

    /**
     * 判断是否需要重试
     *
     * @param code 错误码
     * @return 是否需要重试
     */
    fun shouldRetry(code: Int?): Boolean {
        if (code == null) return false
        return ErrorCodeDefinitions.isNetworkError(code) ||
               code == ErrorCodeDefinitions.FREQUENCY_TOO_FAST ||
               code == ErrorCodeDefinitions.SERVICE_UNAVAILABLE ||
               code == ErrorCodeDefinitions.GATEWAY_TIMEOUT ||
               code == ErrorCodeDefinitions.RISK_CONTROL_BLOCKED
    }

    /**
     * 判断是否需要重新登录
     *
     * @param code 错误码
     * @return 是否需要重新登录
     */
    fun needReLogin(code: Int?): Boolean {
        return code == ErrorCodeDefinitions.NOT_LOGGED_IN ||
               code == ErrorCodeDefinitions.NOT_LOGGED_IN_ALT ||
               code == ErrorCodeDefinitions.TOKEN_INVALID ||
               code == ErrorCodeDefinitions.ACCOUNT_BANNED
    }

    /**
     * 判断是否是可恢复的错误
     *
     * @param code 错误码
     * @return 是否可恢复
     */
    fun isRecoverable(code: Int?): Boolean {
        if (code == null) return false
        return !isFatalError(code)
    }

    /**
     * 判断是否是致命错误（无法恢复）
     *
     * @param code 错误码
     * @return 是否是致命错误
     */
    private fun isFatalError(code: Int?): Boolean {
        return code == ErrorCodeDefinitions.ACCOUNT_BANNED ||
               code == ErrorCodeDefinitions.VIDEO_NOT_EXIST ||
               code == ErrorCodeDefinitions.RESOURCE_NOT_FOUND
    }

    /**
     * 获取完整的错误信息（包含消息和建议）
     *
     * @param code 错误码
     * @return 完整的错误信息
     */
    fun getFullErrorMessage(code: Int?): String {
        val message = resolve(code)
        val suggestion = getRecoverySuggestion(code)
        return if (suggestion.isNotEmpty()) {
            "$message\n建议：$suggestion"
        } else {
            message
        }
    }
}