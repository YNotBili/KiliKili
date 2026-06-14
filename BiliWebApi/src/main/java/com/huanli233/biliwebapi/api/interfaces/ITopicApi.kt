package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ITopicApi {

    @GET("/x/polymer/web-dynamic/v1/feed/topic")
    suspend fun getTopicFeed(
        @Query("topic_id") topicId: Long,
        @Query("offset") offset: String = ""
    ): ApiResponse<TopicFeedResult>

    @GET("/x/topic/web/dynamic/rcmd")
    suspend fun getTopicRcmd(
        @Query("topic_id") topicId: Long
    ): ApiResponse<TopicRcmdResult>

    @POST("/x/topic/like")
    @FormUrlEncoded @Csrf
    suspend fun likeTopic(
        @Field("topic_id") topicId: Long,
        @Field("action") action: Int = 1
    ): ApiResponse<Unit>

    data class TopicFeedResult(val items: List<TopicFeedItem> = emptyList())
    data class TopicFeedItem(val id_str: String = "", val type: String = "")
    data class TopicRcmdResult(val items: List<TopicRcmdItem> = emptyList())
    data class TopicRcmdItem(val id_str: String = "", val type: String = "")
}