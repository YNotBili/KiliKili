package rj.kilikili.utils.encode

object BiliResponseCode {
    const val SUCCESS: Int = 0
    const val NOT_LOGGED_IN: Int = -101
    const val NOT_LOGGED_IN_ALT: Int = -100
    const val NOT_ENOUGH_COIN: Int = -104
    const val NOT_ENOUGH_PERMISSION: Int = -403
    const val OUT_OF_LIMIT: Int = 65004
    const val TARGET_NOT_EXIST: Int = -404
    const val ALREADY_DONE: Int = 65006
    const val DUPLICATED_OPERATION: Int = 22001
    const val SENSITIVE_CONTENT: Int = 12007
    const val FREQ_TOO_FAST: Int = -799
    const val FOLLOW_SELF_FORBIDDEN: Int = 22002
    const val ALREADY_FOLLOWED: Int = 22003
    const val NOT_FOLLOWED: Int = 22101
    const val BLOCKED_BY_USER: Int = 22105
    const val VIDEO_NOT_EXIST: Int = 16001
    const val COMMENT_DELETED: Int = 12002
    const val NO_PERMISSION_TO_DELETE: Int = 12009
    const val NETWORK_ERROR: Int = -1
    const val SERVER_ERROR: Int = -500
}

object ErrorMessages {

    private val messages: Map<Int, String> = mapOf(
        BiliResponseCode.SUCCESS to "操作成功",
        BiliResponseCode.NOT_LOGGED_IN to "请先登录",
        BiliResponseCode.NOT_LOGGED_IN_ALT to "请先登录",
        BiliResponseCode.NOT_ENOUGH_COIN to "硬币不足",
        BiliResponseCode.NOT_ENOUGH_PERMISSION to "权限不足",
        BiliResponseCode.OUT_OF_LIMIT to "已达上限",
        BiliResponseCode.TARGET_NOT_EXIST to "目标不存在",
        BiliResponseCode.ALREADY_DONE to "已经操作过",
        BiliResponseCode.DUPLICATED_OPERATION to "请勿重复操作",
        BiliResponseCode.SENSITIVE_CONTENT to "内容包含敏感词",
        BiliResponseCode.FREQ_TOO_FAST to "操作过于频繁，请稍后再试",
        BiliResponseCode.FOLLOW_SELF_FORBIDDEN to "不能关注自己",
        BiliResponseCode.ALREADY_FOLLOWED to "已经关注过",
        BiliResponseCode.NOT_FOLLOWED to "未关注",
        BiliResponseCode.BLOCKED_BY_USER to "对方设置了隐私",
        BiliResponseCode.VIDEO_NOT_EXIST to "视频不存在",
        BiliResponseCode.COMMENT_DELETED to "评论已删除",
        BiliResponseCode.NO_PERMISSION_TO_DELETE to "无权删除",
        BiliResponseCode.NETWORK_ERROR to "网络错误",
        BiliResponseCode.SERVER_ERROR to "服务器异常"
    )

    fun resolve(code: Int?): String {
        if (code == null) return "未知错误"
        return messages[code] ?: "操作失败 ($code)"
    }

    fun resolveOrNull(code: Int?): String? = if (code == BiliResponseCode.SUCCESS) null else resolve(code)
}