package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.bangumi.BangumiDetail
import com.huanli233.biliwebapi.bean.bangumi.BangumiSections
import retrofit2.http.GET
import retrofit2.http.Query

interface IBangumiApi {
    
    @GET("/pgc/review/user")
    suspend fun getBangumiInfo(
        @Query("media_id") mediaId: Long
    ): ApiResponse<BangumiDetail>
    
    @GET("/pgc/web/season/section")
    suspend fun getBangumiSections(
        @Query("season_id") seasonId: Long
    ): ApiResponse<BangumiSections>
    
    @GET("/pgc/view/web/season")
    suspend fun getMediaIdFromEpId(
        @Query("ep_id") epId: Long
    ): ApiResponse<MediaIdResult>
    
    data class MediaIdResult(
        val media_id: Long
    )

    /**
     * 获取追番列表
     */
    @GET("/x/space/bangumi/follow/list")
    suspend fun getFollowedBangumi(
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 15,
        @Query("type") type: Int = 1
    ): ApiResponse<FollowedBangumiResult>

    data class FollowedBangumiResult(
        val list: List<FollowedBangumiItem> = emptyList(),
        val total: Int = 0
    )

    data class FollowedBangumiItem(
        val season_id: Long,
        val media_id: Long,
        val title: String = "",
        val cover: String = "",
        val evaluate: String = "",
        val total_count: Int = 0,
        val progress: String = "",
        val newest_ep_id: Long = 0,
        val newest_ep_index: String = "",
        val is_finish: Int = 0,
        val is_started: Int = 0,
        val attention: Int = 0
    )
}
