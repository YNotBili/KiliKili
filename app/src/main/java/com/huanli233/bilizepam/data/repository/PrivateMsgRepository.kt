package com.huanli233.bilizepam.data.repository

import com.huanli233.bilizepam.api.apiResultNonNull
import com.huanli233.bilizepam.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IPrivateMsgApi
import com.huanli233.biliwebapi.bean.privatemessage.MessagesResult
import com.huanli233.biliwebapi.bean.privatemessage.SendMsgResult
import com.huanli233.biliwebapi.bean.privatemessage.SessionsResult
import com.huanli233.biliwebapi.bean.privatemessage.SingleUnreadResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrivateMsgRepository @Inject constructor() {

    suspend fun getSingleUnread(): Result<SingleUnreadResult> {
        return bilibiliApi.api(IPrivateMsgApi::class) {
            getSingleUnread()
        }.apiResultNonNull()
    }

    suspend fun getSessions(size: Int = 20): Result<SessionsResult> {
        return bilibiliApi.api(IPrivateMsgApi::class) {
            getSessions(size = size)
        }.apiResultNonNull()
    }

    suspend fun getNewSessions(beginTs: Long = 0, size: Int = 20): Result<SessionsResult> {
        return bilibiliApi.api(IPrivateMsgApi::class) {
            getNewSessions(beginTs = beginTs, size = size)
        }.apiResultNonNull()
    }

    suspend fun getMessages(
        talkerId: Long,
        size: Int = 50,
        beginSeqno: Long = 0,
        endSeqno: Long = 0
    ): Result<MessagesResult> {
        return bilibiliApi.api(IPrivateMsgApi::class) {
            getMessages(
                talkerId = talkerId,
                size = size,
                beginSeqno = beginSeqno,
                endSeqno = endSeqno
            )
        }.apiResultNonNull()
    }

    suspend fun sendMessage(
        senderUid: Long,
        receiverId: Long,
        content: String,
        msgType: Int = 1,
        devId: String
    ): Result<SendMsgResult> {
        return bilibiliApi.api(IPrivateMsgApi::class) {
            sendMessage(
                senderUid = senderUid,
                receiverId = receiverId,
                content = content,
                msgType = msgType,
                devId = devId,
                timestamp = System.currentTimeMillis() / 1000
            )
        }.apiResultNonNull()
    }

    suspend fun updateAck(talkerId: Long, ackSeqno: Long = 0): Result<Unit> {
        return bilibiliApi.api(IPrivateMsgApi::class) {
            updateAck(talkerId = talkerId, ackSeqno = ackSeqno)
        }.apiResultNonNull()
    }
}