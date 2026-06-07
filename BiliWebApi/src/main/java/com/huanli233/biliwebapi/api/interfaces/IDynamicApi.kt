package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.ItemResult
import com.huanli233.biliwebapi.bean.dynamic.Dynamic
import com.huanli233.biliwebapi.bean.dynamic.DynamicFeedResponse
import com.huanli233.biliwebapi.bean.dynamic.DynamicPortal
import com.huanli233.biliwebapi.bean.dynamic.DynamicUpdateResult
import com.huanli233.biliwebapi.bean.dynamic.MentionResult
import com.huanli233.biliwebapi.httplib.Domains
import com.huanli233.biliwebapi.httplib.annotation.API
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import com.huanli233.biliwebapi.httplib.annotation.DmImg
import com.huanli233.biliwebapi.httplib.annotation.Queries
import com.huanli233.biliwebapi.httplib.annotation.WbiSign
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IDynamicApi {

    @WbiSign @DmImg
    @GET("/x/polymer/web-dynamic/v1/feed/all")
    suspend fun getDynamicFeed(
        @Query("timezone_offset") timezoneOffset: String = "-480",
        @Query("type") type: String = "all",
        @Query("platform") platform: String = "web",
        @Query("offset") offset: String? = null,
        @Query("page") page: Int = 1,
        @Query("features") features: String = "itemOpusStyle,listOnlyfans,opusBigCover,onlyfansVote,decorationCard,onlyfansAssetsV2,ugcDelete,onlyfansQaCard,forwardListHidden,commentsNewVersion,onlyfansV2,avatarAutoTheme,sunflowerStyle,eva3CardOpus,eva3CardVideo,eva3CardComment",
        @Query("web_location") webLocation: String = "333.1365"
    ): ApiResponse<DynamicFeedResponse>

    @WbiSign @DmImg
    @GET("/x/polymer/web-dynamic/v1/feed/space")
    suspend fun getUserSpaceDynamicFeed(
        @Query("host_mid") hostMid: Long,
        @Query("timezone_offset") timezoneOffset: String = "-480",
        @Query("platform") platform: String = "web",
        @Query("offset") offset: String? = null,
        @Query("features") features: String = "itemOpusStyle,listOnlyfans,opusBigCover,onlyfansVote,forwardListHidden,decorationCard,commentsNewVersion,onlyfansAssetsV2,ugcDelete,onlyfansQaCard,avatarAutoTheme,sunflowerStyle,eva3CardOpus,eva3CardVideo,eva3CardComment",
        @Query("web_location") webLocation: String = "333.999"
    ): ApiResponse<DynamicFeedResponse>

    @WbiSign
    @GET("/x/polymer/web-dynamic/v1/detail")
    @Queries(
        keys = ["features"],
        values = ["itemOpusStyle,opusBigCover,onlyfansVote,endFooterHidden,decorationCard,onlyfansAssetsV2,ugcDelete,onlyfansQaCard,editable,opusPrivateVisible,avatarAutoTheme"]
    )
    suspend fun getDynamic(@Query("id") id: String, @Query("rid") rid: String = "") : ApiResponse<ItemResult<Dynamic>>

    @API(Domains.VC_API_URL)
    @POST("/dynamic_like/v1/dynamic_like/thumb")
    @FormUrlEncoded @Csrf
    suspend fun like(
        @Field("dynamic_id") id: String,
        @Field("up") action: Int
    ) : ApiResponse<Unit>

    // ===== 新增端点 =====

    /**
     * 发布纯文本动态
     */
    @API(Domains.VC_API_URL)
    @POST("/dynamic_svr/v1/dynamic_svr/create")
    @FormUrlEncoded @Csrf
    suspend fun publishTextDynamic(
        @Field("dynamic_id") dynamicId: Long = 0,
        @Field("type") type: Int = 4,
        @Field("rid") rid: Long = 0,
        @Field("content") content: String
    ): ApiResponse<PublishDynamicResult>

    /**
     * 发布复杂动态
     */
    @POST("/x/dynamic/feed/create/dyn")
    @Csrf(forceQuery = true)
    suspend fun publishComplexDynamic(
        @Body body: com.huanli233.biliwebapi.bean.dynamic.DynamicPublishRequest
    ): ApiResponse<ComplexPublishResult>

    /**
     * 删除动态
     */
    @API(Domains.VC_API_URL)
    @POST("/dynamic_svr/v1/dynamic_svr/rm_dynamic")
    @FormUrlEncoded @Csrf
    suspend fun deleteDynamic(
        @Field("dynamic_id") dynamicId: Long
    ): ApiResponse<Unit>

    /**
     * 转发动态
     */
    @API(Domains.VC_API_URL)
    @POST("/dynamic_repost/v1/dynamic_repost/repost")
    @FormUrlEncoded @Csrf
    suspend fun repostDynamic(
        @Field("dynamic_id") dynamicId: Long,
        @Field("content") content: String,
        @Field("csrf_token") csrfToken: String = ""
    ): ApiResponse<PublishDynamicResult>

    /**
     * AT用户搜索
     */
    @GET("/x/polymer/web-dynamic/v1/mention/search")
    suspend fun mentionSearch(
        @Query("keyword") keyword: String
    ): ApiResponse<MentionResult>

    /**
     * 获取最近更新UP列表
     */
    @GET("/x/polymer/web-dynamic/v1/portal")
    suspend fun getPortal(): ApiResponse<DynamicPortal>

    /**
     * 检查动态更新数
     */
    @GET("/x/polymer/web-dynamic/v1/feed/all/update")
    suspend fun checkUpdate(
        @Query("type") type: String = "all",
        @Query("update_baseline") updateBaseline: Long,
        @Query("web_location") webLocation: String = "333.1365"
    ): ApiResponse<DynamicUpdateResult>

    data class PublishDynamicResult(
        val dynamic_id: Long
    )

    data class ComplexPublishResult(
        val dyn_id: Long
    )
}