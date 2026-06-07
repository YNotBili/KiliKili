package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.vip.VipInfo
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface IVipApi {

    /**
     * 获取VIP信息及特权
     */
    @GET("/x/vip/privilege/my")
    suspend fun getVipInfo(): ApiResponse<VipInfo>

    /**
     * 会员签到
     */
    @POST("/x/vip/experience/add")
    @FormUrlEncoded
    @Csrf
    suspend fun addExperience(
        @Field("csrf") csrf: String = ""
    ): ApiResponse<Unit>
}