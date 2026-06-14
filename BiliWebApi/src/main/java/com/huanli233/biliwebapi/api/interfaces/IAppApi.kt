package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.reply.ReplySendResult
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import com.huanli233.biliwebapi.httplib.annotation.Fields
import com.huanli233.biliwebapi.httplib.annotation.WbiSign
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** 应用端 API（使用 mobi_app=android_hd 等 HD 参数） */
interface IAppApi {

    /** App风格评论（支持带图） */
    @POST("/x/v2/reply/add")
    @FormUrlEncoded @Csrf
    @Fields(keys = ["platform", "mobi_app"], values = ["android", "android_hd"])
    suspend fun replyWithImage(
        @Field("oid") oid: Long,
        @Field("type") type: Int = 1,
        @Field("message") message: String,
        @Field("images") images: String = "",
        @Field("root") root: Long = 0,
        @Field("parent") parent: Long = 0
    ): ApiResponse<ReplySendResult>

    /** App推荐流 */
    @GET("/x/v2/feed/index")
    suspend fun getAppRecommend(
        @Query("idx") idx: String = "",
        @Query("login_event") loginEvent: Int = 0,
        @Query("flush") flush: Int = 0,
        @Query("build") build: Int = 0,
        @Query("mobi_app") mobiApp: String = "android_hd"
    ): ApiResponse<AppFeedResult>

    /** App风格点赞 */
    @POST("/x/v2/view/like")
    @FormUrlEncoded @Csrf
    suspend fun appLike(
        @Field("aid") aid: Long,
        @Field("like") like: Int = 1,
        @Field("mobi_app") mobiApp: String = "android_hd"
    ): ApiResponse<Unit>

    /** App风格投币 */
    @POST("/x/v2/view/coin/add")
    @FormUrlEncoded @Csrf
    suspend fun appCoin(
        @Field("aid") aid: Long,
        @Field("multiply") multiply: Int = 1,
        @Field("select_like") selectLike: Int = 1,
        @Field("mobi_app") mobiApp: String = "android_hd"
    ): ApiResponse<Unit>

    /** 视频播放心跳上报 */
    @POST("/x/click-interface/web/heartbeat")
    @FormUrlEncoded @Csrf
    suspend fun heartbeat(
        @Field("aid") aid: Long,
        @Field("cid") cid: Long,
        @Field("played_time") playedTime: Long = 0,
        @Field("real_played_time") realPlayedTime: Long = 0
    ): ApiResponse<Unit>

    /** 当前观看人数 */
    @GET("/x/player/online/total")
    suspend fun getOnlineTotal(
        @Query("aid") aid: Long,
        @Query("cid") cid: Long
    ): ApiResponse<OnlineTotalResult>

    data class AppFeedResult(val items: List<AppFeedItem> = emptyList())
    data class AppFeedItem(val id: Long = 0, val card: String = "", val card_type: String = "")
    data class OnlineTotalResult(val total: String = "", val count: String = "")
}