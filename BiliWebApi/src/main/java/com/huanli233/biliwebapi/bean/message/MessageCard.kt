package com.huanli233.biliwebapi.bean.message

import com.google.gson.annotations.SerializedName

/**
 * 消息卡片 - 通用的消息项
 */
data class MessageCard(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("users") val users: List<MessageUser> = emptyList(),
    @SerializedName("like_time") val likeTime: Long = 0,
    @SerializedName("reply_time") val replyTime: Long = 0,
    @SerializedName("at_time") val atTime: Long = 0,
    @SerializedName("counts") val counts: Int = 0,
    @SerializedName("item") val item: MessageItem? = null
)

data class MessageUser(
    @SerializedName("mid") val mid: Long,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("avatar") val avatar: String = "",
    @SerializedName("fans") val fans: Int = 0,
    @SerializedName("follow") val follow: Boolean = false
)

data class MessageItem(
    @SerializedName("business_id") val businessId: Int = 0,
    @SerializedName("item_id") val itemId: Long = 0,
    @SerializedName("source_id") val sourceId: Long = -1,
    @SerializedName("root_id") val rootId: Long = -1,
    @SerializedName("target_id") val targetId: Long = -1,
    @SerializedName("type") val type: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("image") val image: String = "",
    @SerializedName("uri") val uri: String = "",
    @SerializedName("source_content") val sourceContent: String = ""
)

data class MessageCursor(
    @SerializedName("is_end") val isEnd: Boolean = true,
    @SerializedName("id") val id: Long = -1,
    @SerializedName("time") val time: Long = -1
)

/**
 * 点赞消息结果
 */
data class LikeMessagesResult(
    @SerializedName("total") val total: LikeMessageGroup? = null,
    @SerializedName("cursor") val cursor: MessageCursor? = null
)

data class LikeMessageGroup(
    @SerializedName("items") val items: List<MessageCard> = emptyList()
)

/**
 * 回复消息结果
 */
data class ReplyMessagesResult(
    @SerializedName("items") val items: List<ReplyMessageCard> = emptyList(),
    @SerializedName("cursor") val cursor: MessageCursor? = null
)

data class ReplyMessageCard(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("user") val user: MessageUser? = null,
    @SerializedName("reply_time") val replyTime: Long = 0,
    @SerializedName("item") val item: MessageItem? = null
)

/**
 * @消息结果
 */
data class AtMessagesResult(
    @SerializedName("items") val items: List<AtMessageCard> = emptyList(),
    @SerializedName("cursor") val cursor: MessageCursor? = null
)

data class AtMessageCard(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("user") val user: MessageUser? = null,
    @SerializedName("at_time") val atTime: Long = 0,
    @SerializedName("item") val item: MessageItem? = null
)

/**
 * 系统通知结果
 */
data class SystemMessagesResult(
    @SerializedName("system_notify_list") val systemNotifyList: List<SystemMessageItem> = emptyList()
)

data class SystemMessageItem(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("time_at") val timeAt: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("content") val content: String = ""
)