package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IReportApi {

    @POST("/x/dm/report/add")
    @FormUrlEncoded @Csrf
    suspend fun reportDanmaku(
        @Field("oid") oid: Long,
        @Field("dmid") dmid: Long,
        @Field("reason") reason: String = "其他",
        @Field("content") content: String = ""
    ): ApiResponse<Unit>

    @POST("/x/v2/reply/report")
    @FormUrlEncoded @Csrf
    suspend fun reportReply(
        @Field("oid") oid: Long,
        @Field("rpid") rpid: Long,
        @Field("reason") reason: Int = 1,
        @Field("content") content: String = ""
    ): ApiResponse<Unit>
}