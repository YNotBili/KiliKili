package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** 动态扩展 API */
interface IDynamicExApi {

    @GET("/x/web-interface/dynamic/entrance")
    suspend fun getUnreadDynamic(): ApiResponse<UnreadDynamicResult>

    @POST("/x/dynamic/feed/operate/remove")
    @FormUrlEncoded @Csrf
    suspend fun removeDynamic(
        @Field("dynamic_id") dynamicId: Long
    ): ApiResponse<Unit>

    @POST("/x/dynamic/feed/edit/dyn")
    @FormUrlEncoded @Csrf
    suspend fun editDynamic(
        @Field("dynamic_id") dynamicId: Long,
        @Field("content") content: String = ""
    ): ApiResponse<Unit>

    @POST("/x/dynamic/feed/space/set_top")
    @FormUrlEncoded @Csrf
    suspend fun setTopDynamic(
        @Field("dynamic_id") dynamicId: Long
    ): ApiResponse<Unit>

    @POST("/x/dynamic/feed/space/rm_top")
    @FormUrlEncoded @Csrf
    suspend fun rmTopDynamic(
        @Field("dynamic_id") dynamicId: Long
    ): ApiResponse<Unit>

    @GET("/x/polymer/web-dynamic/v1/feed/space/search")
    suspend fun searchSpaceDynamics(
        @Query("host_mid") mid: Long,
        @Query("keyword") keyword: String,
        @Query("offset") offset: String = ""
    ): ApiResponse<DynamicSearchResult>

    data class UnreadDynamicResult(val count: Int = 0)
    data class DynamicSearchResult(val items: List<DynamicSearchItem> = emptyList())
    data class DynamicSearchItem(val id_str: String = "", val type: String = "")
}