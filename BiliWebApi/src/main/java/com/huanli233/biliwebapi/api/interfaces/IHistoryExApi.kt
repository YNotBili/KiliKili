package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** 历史记录扩展 API */
interface IHistoryExApi {

    @POST("/x/v2/history/shadow/set")
    @FormUrlEncoded @Csrf
    suspend fun setPauseHistory(
        @Field("is_shadow") isShadow: Int = 1
    ): ApiResponse<Unit>

    @GET("/x/v2/history/shadow")
    suspend fun getHistoryStatus(): ApiResponse<HistoryStatusResult>

    @POST("/x/v2/history/clear")
    @FormUrlEncoded @Csrf
    suspend fun clearHistory(): ApiResponse<Unit>

    @POST("/x/v2/history/delete")
    @FormUrlEncoded @Csrf
    suspend fun deleteHistoryEntry(
        @Field("kid") historyKid: Long
    ): ApiResponse<Unit>

    @GET("/x/web-interface/history/search")
    suspend fun searchHistory(
        @Query("mid") mid: Long,
        @Query("keyword") keyword: String,
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 20
    ): ApiResponse<HistorySearchResult>

    data class HistoryStatusResult(val is_shadow: Boolean = false)
    data class HistorySearchResult(val list: List<HistoryEntry> = emptyList())
    data class HistoryEntry(val kid: Long = 0, val title: String = "", val cover: String = "")
}