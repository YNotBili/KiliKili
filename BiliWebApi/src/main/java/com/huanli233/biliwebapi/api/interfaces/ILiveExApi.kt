package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.Domains
import com.huanli233.biliwebapi.httplib.annotation.API
import com.huanli233.biliwebapi.httplib.annotation.AppSign
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@API(Domains.LIVE_API_URL)
interface ILiveExApi {

    @GET("/xlive/web-room/v1/dM/gethistory")
    suspend fun getDanmakuHistory(@Query("room_id") roomId: Long): ApiResponse<LiveDmHistoryResult>

    @GET("/xlive/web-room/v1/index/getDanmuInfo")
    suspend fun getDanmuInfo(@Query("id") roomId: Long): ApiResponse<DanmuInfoResult>

    @AppSign
    @FormUrlEncoded
    @POST("/xlive/web-ucenter/user/MedalWall")
    suspend fun getMedalWall(
        @Field("local_id") localId: String = "0"
    ): ApiResponse<LiveMedalWallResult>

    @GET("/av/v1/SuperChat/getMessageList")
    suspend fun getSuperChatMessages(
        @Query("room_id") roomId: Long,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 50
    ): ApiResponse<SuperChatResult>

    @GET("/xlive/web-room/v1/index/getInfoByUser")
    suspend fun getRoomInfoByUser(@Query("room_id") roomId: Long): ApiResponse<RoomUserInfoResult>

    data class LiveDmHistoryResult(
        val room: List<LiveDmItem> = emptyList()
    )
    data class LiveDmItem(val text: String = "", val uid: Long = 0, val uname: String = "")
    data class DanmuInfoResult(
        val host: List<DanmuHost> = emptyList(),
        val token: String = ""
    )
    data class DanmuHost(val host: String = "", val port: Int = 0, val wss_port: Int = 0)
    data class LiveMedalWallResult(val list: List<LiveMedalItem> = emptyList())
    data class LiveMedalItem(
        val medal_id: Long = 0, val medal_name: String = "", val level: Int = 0,
        val target_id: Long = 0, val target_name: String = "", val target_face: String = "",
        val today_feed: Int = 0, val day_limit: Int = 0, val score: Int = 0
    )
    data class SuperChatResult(val list: List<SuperChatItem> = emptyList())
    data class SuperChatItem(
        val uid: Long = 0, val uname: String = "", val avatar: String = "",
        val message: String = "", val price: Long = 0, val timestamp: Long = 0
    )
    data class RoomUserInfoResult(val follow: Int = 0, val attention: Int = 0)
}