package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.video.VideoInfo
import com.huanli233.biliwebapi.httplib.annotation.WbiSign
import retrofit2.http.GET
import retrofit2.http.Query

interface IRankingApi {

    /**
     * 获取视频排行榜
     * @param rid 分区id 0=全站
     * @param type 类型 all=全站
     */
    @WbiSign
    @GET("/x/web-interface/ranking/v2")
    suspend fun getRanking(
        @Query("rid") rid: Int = 0,
        @Query("type") type: String = "all",
        @Query("web_location") webLocation: String = "333.934"
    ): ApiResponse<RankingData>

    data class RankingData(
        val list: List<VideoInfo>,
        val note: String = ""
    )
}