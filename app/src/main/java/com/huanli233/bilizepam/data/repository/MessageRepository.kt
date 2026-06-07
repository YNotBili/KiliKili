package com.huanli233.bilizepam.data.repository

import com.huanli233.bilizepam.api.apiResultNonNull
import com.huanli233.bilizepam.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IMessageApi
import com.huanli233.biliwebapi.bean.message.AtMessagesResult
import com.huanli233.biliwebapi.bean.message.LikeMessagesResult
import com.huanli233.biliwebapi.bean.message.ReplyMessagesResult
import com.huanli233.biliwebapi.bean.message.SystemMessagesResult
import com.huanli233.biliwebapi.bean.message.UnreadCount
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor() {

    suspend fun getUnreadCount(): Result<UnreadCount> {
        return bilibiliApi.api(IMessageApi::class) {
            getUnreadCount()
        }.apiResultNonNull()
    }

    suspend fun getLikeMessages(id: Long = 0, replyTime: Long = 0): Result<LikeMessagesResult> {
        return bilibiliApi.api(IMessageApi::class) {
            getLikeMessages(id = id, replyTime = replyTime)
        }.apiResultNonNull()
    }

    suspend fun getReplyMessages(id: Long = 0, replyTime: Long = 0): Result<ReplyMessagesResult> {
        return bilibiliApi.api(IMessageApi::class) {
            getReplyMessages(id = id, replyTime = replyTime)
        }.apiResultNonNull()
    }

    suspend fun getAtMessages(id: Long = 0, atTime: Long = 0): Result<AtMessagesResult> {
        return bilibiliApi.api(IMessageApi::class) {
            getAtMessages(id = id, atTime = atTime)
        }.apiResultNonNull()
    }

    suspend fun getSystemMessages(pageSize: Int = 35): Result<SystemMessagesResult> {
        return bilibiliApi.api(IMessageApi::class) {
            getSystemMessages(pageSize = pageSize)
        }.apiResultNonNull()
    }
}