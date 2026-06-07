package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import com.huanli233.biliwebapi.httplib.annotation.WbiSign
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IDanmakuApi {

    /**
     * 发送视频弹幕
     */
    @POST("/x/v2/dm/post")
    @FormUrlEncoded
    @Csrf
    suspend fun sendDanmaku(
        @Field("type") type: Int = 1,
        @Field("oid") oid: Long,
        @Field("msg") message: String,
        @Field("aid") aid: Long = 0,
        @Field("bvid") bvid: String = "",
        @Field("progress") progress: Long,
        @Field("color") color: Int = 0xFFFFFF,
        @Field("fontsize") fontSize: Int = 25,
        @Field("mode") mode: Int = 1,
        @Field("rnd") rnd: Long = System.currentTimeMillis() * 1000000,
        @Field("pool") pool: Int = 0,
        @Field("plat") plat: Int = 1
    ): ApiResponse<Unit>

    /**
     * 点赞弹幕
     * @param dmid 弹幕dmid
     * @param cid 视频cid
     * @param op 操作 1=点赞 2=取消点赞
     */
    @POST("/x/v2/dm/thumbup/add")
    @FormUrlEncoded
    @Csrf
    suspend fun likeDanmaku(
        @Field("oid") oid: Long,
        @Field("dmid") dmid: Long,
        @Field("op") op: Int,
        @Field("platform") platform: String = "web_player"
    ): ApiResponse<Unit>

    /**
     * 撤回弹幕
     */
    @POST("/x/dm/recall")
    @FormUrlEncoded
    @Csrf
    suspend fun recallDanmaku(
        @Field("cid") cid: Long,
        @Field("dmid") dmid: Long
    ): ApiResponse<Unit>

    /**
     * 获取弹幕（protobuf 分段）
     * 使用 WBI 签名
     */
    @WbiSign
    @GET("/x/v2/dm/wbi/web/seg.so")
    suspend fun getDanmakuSegment(
        @Query("type") type: Int = 1,
        @Query("oid") oid: Long,
        @Query("pid") pid: Long = 0,
        @Query("segment_index") segmentIndex: Int = 1
    ): okhttp3.ResponseBody
}