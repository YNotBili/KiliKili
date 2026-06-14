package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IReserveApi {

    @POST("/x/new-reserve/up/reserve/create")
    @FormUrlEncoded @Csrf
    suspend fun createReserve(
        @Field("title") title: String,
        @Field("live_plan") livePlan: Long = 0,
        @Field("up_mid") upMid: Long = 0,
        @Field("type") type: Int = 1
    ): ApiResponse<ReserveCreateResult>

    @POST("/x/new-reserve/up/reserve/update")
    @FormUrlEncoded @Csrf
    suspend fun updateReserve(
        @Field("reserve_id") reserveId: Long,
        @Field("title") title: String,
        @Field("live_plan") livePlan: Long = 0
    ): ApiResponse<Unit>

    @GET("/x/new-reserve/up/reserve/info")
    suspend fun getReserveInfo(
        @Query("reserve_id") reserveId: Long
    ): ApiResponse<ReserveInfoResult>

    data class ReserveCreateResult(val reserve_id: Long = 0)
    data class ReserveInfoResult(
        val reserve_id: Long = 0,
        val title: String = "",
        val live_plan: Long = 0,
        val total: Int = 0
    )
}