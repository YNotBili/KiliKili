package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** 弹幕过滤 API */
interface IDmApi {

    @GET("/x/dm/filter/user")
    suspend fun getDmFilters(): ApiResponse<DmFilterResult>

    @POST("/x/dm/filter/user/add")
    @FormUrlEncoded @Csrf
    suspend fun addDmFilter(
        @Field("content") content: String,
        @Field("type") type: Int = 1
    ): ApiResponse<Unit>

    @POST("/x/dm/filter/user/del")
    @FormUrlEncoded @Csrf
    suspend fun delDmFilter(
        @Field("ids") ids: String
    ): ApiResponse<Unit>

    data class DmFilterResult(val list: List<DmFilterItem> = emptyList())
    data class DmFilterItem(val id: Long = 0, val content: String = "", val type: Int = 0)
}