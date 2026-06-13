package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.live.DEFAULT_QN
import com.huanli233.biliwebapi.bean.live.LivePlayInfo
import com.huanli233.biliwebapi.bean.live.LiveRoom
import com.huanli233.biliwebapi.bean.live.RecommendLiveData
import com.huanli233.biliwebapi.httplib.Domains
import com.huanli233.biliwebapi.httplib.annotation.API
import retrofit2.http.GET
import retrofit2.http.Query

interface ILiveApi {

    @API(Domains.LIVE_API_URL)
    @GET("/room/v1/Room/get_info")
    suspend fun getRoomInfo(
        @Query("room_id") roomId: String
    ): ApiResponse<LiveRoom>

    @API(Domains.LIVE_API_URL)
    @GET("/xlive/web-room/v2/index/getRoomPlayInfo")
    suspend fun getPlayInfo(
        @Query("room_id") roomId: String,
        @Query("qn") qn: Int = DEFAULT_QN,
        @Query("protocol") protocol: String = "0,1",
        @Query("format") format: String = "0,1,2",
        @Query("codec") codec: String = "0,1,2",
        @Query("platform") platform: String = "web",
        @Query("ptype") ptype: Int = 8,
        @Query("dolby") dolby: Int = 5,
        @Query("panorama") panorama: Int = 1,
    ): ApiResponse<LivePlayInfo>

    /** 获取直播推荐 */
    @API(Domains.LIVE_API_URL)
    @GET("/xlive/web-interface/v1/second/getUserRecommend")
    suspend fun getRecommendLive(): ApiResponse<RecommendLiveData>

    /** 获取关注直播列表 */
    @API(Domains.LIVE_API_URL)
    @GET("/xlive/web-ucenter/v1/xfetter/GetWebList")
    suspend fun getFollowedLive(
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 10
    ): ApiResponse<FollowedLiveData>

    data class FollowedLiveData(
        val list: List<LiveRoom> = emptyList()
    )
}