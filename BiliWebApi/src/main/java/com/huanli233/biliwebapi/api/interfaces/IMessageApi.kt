package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.message.AtMessagesResult
import com.huanli233.biliwebapi.bean.message.LikeMessagesResult
import com.huanli233.biliwebapi.bean.message.ReplyMessagesResult
import com.huanli233.biliwebapi.bean.message.SystemMessagesResult
import com.huanli233.biliwebapi.bean.message.UnreadCount
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import com.huanli233.biliwebapi.httplib.annotation.Fields
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IMessageApi {

    /**
     * 获取未读消息数
     */
    @GET("/x/msgfeed/unread")
    suspend fun getUnreadCount(): ApiResponse<UnreadCount>

    /**
     * 获取点赞消息列表
     */
    @GET("/x/msgfeed/like")
    suspend fun getLikeMessages(
        @Query("platform") platform: String = "web",
        @Query("build") build: Int = 0,
        @Query("mobi_app") mobiApp: String = "web",
        @Query("id") id: Long = 0,
        @Query("reply_time") replyTime: Long = 0
    ): ApiResponse<LikeMessagesResult>

    /**
     * 获取回复消息列表
     */
    @GET("/x/msgfeed/reply")
    suspend fun getReplyMessages(
        @Query("platform") platform: String = "web",
        @Query("build") build: Int = 0,
        @Query("mobi_app") mobiApp: String = "web",
        @Query("id") id: Long = 0,
        @Query("reply_time") replyTime: Long = 0
    ): ApiResponse<ReplyMessagesResult>

    /**
     * 获取@消息列表
     */
    @GET("/x/msgfeed/at")
    suspend fun getAtMessages(
        @Query("platform") platform: String = "web",
        @Query("build") build: Int = 0,
        @Query("mobi_app") mobiApp: String = "web",
        @Query("id") id: Long = 0,
        @Query("at_time") atTime: Long = 0
    ): ApiResponse<AtMessagesResult>

    /**
     * 获取系统通知
     */
    @GET("/x/sys-msg/query_user_notify")
    suspend fun getSystemMessages(
        @Query("page_size") pageSize: Int = 35,
        @Query("build") build: Int = 0,
        @Query("mobi_app") mobiApp: String = "web"
    ): ApiResponse<SystemMessagesResult>
}