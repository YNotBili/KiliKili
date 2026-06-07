package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.emote.EmotePackage
import com.huanli233.biliwebapi.httplib.Domains
import com.huanli233.biliwebapi.httplib.annotation.API
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IEmoteApi {

    /**
     * 获取表情面板
     */
    @GET("/x/emote/user/panel/web")
    suspend fun getEmotePanel(
        @Query("business") business: String = "reply"
    ): ApiResponse<EmotePanelData>

    /**
     * 获取使用中的表情包
     */
    @API(Domains.MAIN_URL)
    @POST("/bapis/main.community.interface.emote.EmoteService/InUsePackages")
    @FormUrlEncoded
    @Csrf
    suspend fun getInUsePackages(
        @Field("business") business: String = "reply",
        @Field("csrf") csrf: String = ""
    ): ApiResponse<EmotePackagesData>

    /**
     * 获取我的表情包
     */
    @API(Domains.MAIN_URL)
    @POST("/bapis/main.community.interface.emote.EmoteService/MyPackages")
    @FormUrlEncoded
    @Csrf
    suspend fun getMyPackages(
        @Field("business") business: String = "reply",
        @Field("csrf") csrf: String = "",
        @Field("pn") page: Int = 1,
        @Field("ps") pageSize: Int = 12,
        @Field("type") type: Int = 0
    ): ApiResponse<EmotePackagesData>

    data class EmotePanelData(
        val packages: List<EmotePackage>
    )

    data class EmotePackagesData(
        val packages: List<EmotePackage>
    )
}