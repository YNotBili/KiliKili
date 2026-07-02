package com.huanli233.biliwebapi.api.interfaces

import com.google.gson.annotations.SerializedName
import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.bangumi.BangumiDetail
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import com.huanli233.biliwebapi.httplib.annotation.WbiSign
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** PGC/番剧扩展 API */
interface IPgcExApi {

    @GET("/pgc/season/index/condition")
    suspend fun getIndexCondition(
        @Query("season_type") seasonType: Int = 1,
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

    @WbiSign @GET("/pgc/season/rank/web/list")
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
        val filter: List<PgcFilterGroup> = emptyList(),
        val order: List<PgcFilterOption> = emptyList()
    )
    data class PgcFilterGroup(
        val field: String = "",
        val name: String = "",
        val values: List<PgcFilterOption> = emptyList()
    )
    data class PgcFilterOption(
        @SerializedName("keyword") val id: String = "",
        val name: String = ""
    )
    data class PgcIndexResult(val list: List<BangumiDetail> = emptyList())
    data class PgcRankResult(val list: List<BangumiDetail> = emptyList())
}