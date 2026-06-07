package com.huanli233.biliwebapi.bean.message

import com.google.gson.annotations.SerializedName

/**
 * 未读消息数
 */
data class UnreadCount(
    @SerializedName("at") val at: Int = 0,
    @SerializedName("like") val like: Int = 0,
    @SerializedName("reply") val reply: Int = 0,
    @SerializedName("sys_msg") val system: Int = 0
)