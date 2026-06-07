package com.huanli233.biliwebapi.bean.privatemessage

import com.google.gson.annotations.SerializedName

/**
 * 私信会话
 */
data class PrivateMsgSession(
    @SerializedName("talker_id") val talkerUid: Long = 0,
    @SerializedName("unread_count") val unread: Int = 0,
    @SerializedName("last_msg") val lastMsg: LastMessage? = null,
    @SerializedName("account_info") val accountInfo: SessionAccountInfo? = null
)

data class LastMessage(
    @SerializedName("msg_type") val msgType: Int = 0,
    @SerializedName("content") val content: String = ""
)

data class SessionAccountInfo(
    @SerializedName("name") val name: String = "",
    @SerializedName("face") val face: String = "",
    @SerializedName("mid") val mid: Long = 0
)

/**
 * 私信消息
 */
data class PrivateMessage(
    @SerializedName("sender_uid") val senderUid: Long = 0,
    @SerializedName("msg_type") val msgType: Int = 0,
    @SerializedName("content") val content: String = "",
    @SerializedName("timestamp") val timestamp: Long = 0,
    @SerializedName("msg_key") val msgId: Long = 0,
    @SerializedName("msg_seqno") val msgSeqno: Long = 0,
    @SerializedName("msg_source") val msgSource: Int = 0
) {
    companion object {
        const val TYPE_TEXT = 1
        const val TYPE_PIC = 2
        const val TYPE_RETRACT = 5
        const val TYPE_FACE = 6
        const val TYPE_VIDEO = 7
        const val TYPE_NOMAL_CARD = 10
        const val TYPE_PIC_CARD = 13
        const val TYPE_TEXT_WITH_VIDEO = 16
        const val TYPE_SYSTEM = 18
    }
}

/**
 * 未读私信数
 */
data class SingleUnreadResult(
    @SerializedName("unfollow_unread") val unfollowUnread: Int = 0,
    @SerializedName("follow_unread") val followUnread: Int = 0,
    @SerializedName("unfollow_push_msg") val unfollowPushMsg: Int = 0,
    @SerializedName("dustbin_push_msg") val dustbinPushMsg: Int = 0,
    @SerializedName("dustbin_unread") val dustbinUnread: Int = 0,
    @SerializedName("biz_msg_unfollow_unread") val bizMsgUnfollowUnread: Int = 0,
    @SerializedName("biz_msg_follow_unread") val bizMsgFollowUnread: Int = 0,
    @SerializedName("custom_unread") val customUnread: Int = 0
) {
    val total: Int get() = unfollowUnread + followUnread + unfollowPushMsg + dustbinPushMsg +
            dustbinUnread + bizMsgUnfollowUnread + bizMsgFollowUnread + customUnread
}

/**
 * 会话列表结果
 */
data class SessionsResult(
    @SerializedName("session_list") val sessionList: List<PrivateMsgSession> = emptyList()
)

/**
 * 消息列表结果
 */
data class MessagesResult(
    @SerializedName("messages") val messages: List<PrivateMessage> = emptyList(),
    @SerializedName("e_infos") val eInfos: List<EmoteInfo> = emptyList()
)

/**
 * 私信中使用的表情信息
 */
data class EmoteInfo(
    @SerializedName("text") val text: String = "",
    @SerializedName("url") val url: String = "",
    @SerializedName("size") val size: Int = 1
)

/**
 * 发送消息结果
 */
data class SendMsgResult(
    @SerializedName("msg_key") val msgKey: Long = 0,
    @SerializedName("timestamp") val timestamp: Long = 0
)