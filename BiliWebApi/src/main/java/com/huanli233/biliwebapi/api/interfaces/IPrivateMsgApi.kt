package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.privatemessage.MessagesResult
import com.huanli233.biliwebapi.bean.privatemessage.SendMsgResult
import com.huanli233.biliwebapi.bean.privatemessage.SessionsResult
import com.huanli233.biliwebapi.bean.privatemessage.SingleUnreadResult
import com.huanli233.biliwebapi.httplib.Domains
import com.huanli233.biliwebapi.httplib.annotation.API
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IPrivateMsgApi {

    /**
     * 获取私信未读数
     */
    @API(Domains.VC_API_URL)
    @GET("/session_svr/v1/session_svr/single_unread")
    suspend fun getSingleUnread(): ApiResponse<SingleUnreadResult>

    /**
     * 获取会话列表
     */
    @API(Domains.VC_API_URL)
    @GET("/session_svr/v1/session_svr/get_sessions")
    suspend fun getSessions(
        @Query("session_type") sessionType: Int = 1,
        @Query("size") size: Int = 20,
        @Query("group") group: Int = 1
    ): ApiResponse<SessionsResult>

    /**
     * 获取新会话列表
     */
    @API(Domains.VC_API_URL)
    @GET("/session_svr/v1/session_svr/new_sessions")
    suspend fun getNewSessions(
        @Query("begin_ts") beginTs: Long = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<SessionsResult>

    /**
     * 获取私信消息列表
     */
    @API(Domains.VC_API_URL)
    @GET("/svr_sync/v1/svr_sync/fetch_session_msgs")
    suspend fun getMessages(
        @Query("session_type") sessionType: Int = 1,
        @Query("talker_id") talkerId: Long,
        @Query("size") size: Int = 50,
        @Query("begin_seqno") beginSeqno: Long = 0,
        @Query("end_seqno") endSeqno: Long = 0
    ): ApiResponse<MessagesResult>

    /**
     * 发送私信
     */
    @API(Domains.VC_API_URL)
    @POST("/web_im/v1/web_im/send_msg")
    @FormUrlEncoded
    @Csrf
    suspend fun sendMessage(
        @Field("msg[sender_uid]") senderUid: Long,
        @Field("msg[receiver_id]") receiverId: Long,
        @Field("msg[receiver_type]") receiverType: Int = 1,
        @Field("msg[msg_type]") msgType: Int = 1,
        @Field("msg[content]") content: String,
        @Field("msg[dev_id]") devId: String,
        @Field("msg[timestamp]") timestamp: Long
    ): ApiResponse<SendMsgResult>

    /**
     * 更新已读标记
     */
    @API(Domains.VC_API_URL)
    @POST("/session_svr/v1/session_svr/update_ack")
    @FormUrlEncoded
    @Csrf
    suspend fun updateAck(
        @Field("talker_id") talkerId: Long,
        @Field("session_type") sessionType: Int = 1,
        @Field("ack_seqno") ackSeqno: Long = 0,
        @Field("build") build: Int = 0,
        @Field("mobi_app") mobiApp: String = "web"
    ): ApiResponse<Unit>
}