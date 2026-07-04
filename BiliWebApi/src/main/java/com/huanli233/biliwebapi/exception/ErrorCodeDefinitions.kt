package com.huanli233.biliwebapi.exception

/**
 * Bilibili API错误码定义
 *
 * 该文件定义了Bilibili API返回的所有错误码常量，包括：
 * - 风控错误：风控校验失败等
 * - 认证错误：未登录、访问拒绝、资源不存在等
 * - 参数错误：请求参数错误、请求头缺失等
 * - 业务错误：硬币不足、权限不足、重复操作等
 * - 网络错误：服务器内部错误、网关错误等
 *
 * 参考来源：
 * - PiliPlus错误码定义：PiliPlus/lib/http/error_msg.dart
 * - Bilibili API文档
 *
 * @author KiliKili Team
 * @since 1.0.0
 */
object ErrorCodeDefinitions {

    // ==================== 成功状态 ====================

    /** 操作成功 */
    const val SUCCESS: Int = 0

    // ==================== 风控错误 ====================

    /** 风控校验失败，需要重新验证 */
    const val RISK_CONTROL_FAILED: Int = -352

    /** 请求被拦截，触发风控策略 */
    const val RISK_CONTROL_BLOCKED: Int = -351

    // ==================== 认证错误 ====================

    /** 未登录或登录状态失效 */
    const val NOT_LOGGED_IN: Int = -101

    /** 未登录（备用错误码） */
    const val NOT_LOGGED_IN_ALT: Int = -100

    /** 访问被拒绝，权限不足 */
    const val ACCESS_DENIED: Int = -403

    /** 资源不存在 */
    const val RESOURCE_NOT_FOUND: Int = -404

    /** 账号被封禁 */
    const val ACCOUNT_BANNED: Int = -102

    /** Token失效或无效 */
    const val TOKEN_INVALID: Int = -103

    // ==================== 参数错误 ====================

    /** 请求参数错误 */
    const val BAD_REQUEST: Int = -400

    /** 请求头缺失或签名无效 */
    const val REQUEST_HEADER_ERROR: Int = -412

    /** CSRF Token验证失败 */
    const val CSRF_TOKEN_ERROR: Int = -413

    /** 参数超出范围 */
    const val PARAM_OUT_OF_RANGE: Int = -501

    // ==================== 业务错误 ====================

    /** 硬币不足 */
    const val NOT_ENOUGH_COIN: Int = -104

    /** 余额不足 */
    const val NOT_ENOUGH_BALANCE: Int = -105

    /** 操作已达上限 */
    const val OUT_OF_LIMIT: Int = 65004

    /** 已经操作过（如已点赞、已投币等） */
    const val ALREADY_DONE: Int = 65006

    /** 重复操作 */
    const val DUPLICATED_OPERATION: Int = 22001

    /** 内容包含敏感词 */
    const val SENSITIVE_CONTENT: Int = 12007

    /** 操作过于频繁 */
    const val FREQUENCY_TOO_FAST: Int = -799

    /** 不能关注自己 */
    const val FOLLOW_SELF_FORBIDDEN: Int = 22002

    /** 已经关注过 */
    const val ALREADY_FOLLOWED: Int = 22003

    /** 未关注 */
    const val NOT_FOLLOWED: Int = 22101

    /** 对方设置了隐私权限 */
    const val BLOCKED_BY_USER: Int = 22105

    /** 视频不存在 */
    const val VIDEO_NOT_EXIST: Int = 16001

    /** 评论已删除 */
    const val COMMENT_DELETED: Int = 12002

    /** 无权限删除 */
    const val NO_PERMISSION_TO_DELETE: Int = 12009

    /** 内容审核中 */
    const val CONTENT_UNDER_REVIEW: Int = 12010

    /** 评论已关闭 */
    const val COMMENT_CLOSED: Int = 12011

    // ==================== 网络错误 ====================

    /** 服务器内部错误 */
    const val SERVER_INTERNAL_ERROR: Int = -500

    /** 网关错误 */
    const val GATEWAY_ERROR: Int = -502

    /** 服务不可用 */
    const val SERVICE_UNAVAILABLE: Int = -503

    /** 网关超时 */
    const val GATEWAY_TIMEOUT: Int = -504

    /** 网络连接错误（自定义错误码） */
    const val NETWORK_CONNECTION_ERROR: Int = -1

    /** DNS解析失败（自定义错误码） */
    const val DNS_RESOLVE_FAILED: Int = -2

    /** 连接超时（自定义错误码） */
    const val CONNECTION_TIMEOUT: Int = -3

    /** SSL握手失败（自定义错误码） */
    const val SSL_HANDSHAKE_FAILED: Int = -4

    /** 证书错误（自定义错误码） */
    const val CERTIFICATE_ERROR: Int = -5

    /** 连接被重置（自定义错误码） */
    const val CONNECTION_RESET: Int = -6

    /** 未知网络错误（自定义错误码） */
    const val UNKNOWN_NETWORK_ERROR: Int = -7

    // ==================== 其他错误 ====================

    /** 未知错误 */
    const val UNKNOWN_ERROR: Int = Int.MIN_VALUE

    /**
     * 判断错误码是否表示成功
     *
     * @param code 错误码
     * @return 如果错误码表示成功则返回true，否则返回false
     */
    fun isSuccess(code: Int?): Boolean = code == SUCCESS

    /**
     * 判断错误码是否为风控错误
     *
     * @param code 错误码
     * @return 如果是风控错误则返回true，否则返回false
     */
    fun isRiskControlError(code: Int?): Boolean =
        code == RISK_CONTROL_FAILED || code == RISK_CONTROL_BLOCKED

    /**
     * 判断错误码是否为认证错误
     *
     * @param code 错误码
     * @return 如果是认证错误则返回true，否则返回false
     */
    fun isAuthenticationError(code: Int?): Boolean =
        code == NOT_LOGGED_IN || code == NOT_LOGGED_IN_ALT ||
        code == ACCESS_DENIED || code == ACCOUNT_BANNED ||
        code == TOKEN_INVALID

    /**
     * 判断错误码是否为网络错误
     *
     * @param code 错误码
     * @return 如果是网络错误则返回true，否则返回false
     */
    fun isNetworkError(code: Int?): Boolean =
        code == NETWORK_CONNECTION_ERROR || code == DNS_RESOLVE_FAILED ||
        code == CONNECTION_TIMEOUT || code == SSL_HANDSHAKE_FAILED ||
        code == CERTIFICATE_ERROR || code == CONNECTION_RESET ||
        code == UNKNOWN_NETWORK_ERROR || code == SERVER_INTERNAL_ERROR ||
        code == GATEWAY_ERROR || code == SERVICE_UNAVAILABLE ||
        code == GATEWAY_TIMEOUT
}