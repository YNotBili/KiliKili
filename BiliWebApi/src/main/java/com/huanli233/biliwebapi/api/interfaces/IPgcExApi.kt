package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.bangumi.BangumiDetail
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** PGC/番剧扩展 API */
interface IPgcExApi {

    @GET("/pgc/season/index/condition")
    suspend fun getIndexCondition(
        @Query("type") type: Int = 1,
        @Query("area") area: String = "",
        @Query("is_finish") isFinish: Int = -1
    ): ApiResponse<PgcIndexConditionResult>

    @GET("/pgc/season/index/result")
    suspend fun getIndexResult(
        @Query("type") type: Int = 1,
        @Query("area") area: String = "",
        @Query("season_type") seasonType: Int = 1,
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 20
    ): ApiResponse<PgcIndexResult>

    @GET("/pgc/web/rank/list")
    suspend fun getPgcRank(
        @Query("season_type") seasonType: Int = 1,
        @Query("day") day: Int = 3
    ): ApiResponse<PgcRankResult>

    @POST("/pgc/web/follow/add")
    @FormUrlEncoded @Csrf
    suspend fun followPgc(
        @Field("season_id") seasonId: Long
    ): ApiResponse<Unit>

    @POST("/pgc/web/follow/del")
    @FormUrlEncoded @Csrf
    suspend fun unfollowPgc(
        @Field("season_id") seasonId: Long
    ): ApiResponse<Unit>

    data class PgcIndexConditionResult(
        val areas: List<PgcFilterOption> = emptyList(),
        val seasons: List<PgcFilterOption> = emptyList()
    )
    data class PgcFilterOption(val id: Int = 0, val name: String = "")
    data class PgcIndexResult(val list: List<BangumiDetail> = emptyList())
    data class PgcRankResult(val list: List<BangumiDetail> = emptyList())
}