package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.*

interface IPgcReviewApi {

    /** 获取长评列表 */
    @GET("/pgc/review/long/list")
    suspend fun getLongReviews(
        @Query("media_id") mediaId: Long,
        @Query("ps") pageSize: Int = 10,
        @Query("pn") page: Int = 1
    ): ApiResponse<PgcReviewListResult>

    /** 获取短评列表 */
    @GET("/pgc/review/short/list")
    suspend fun getShortReviews(
        @Query("media_id") mediaId: Long,
        @Query("ps") pageSize: Int = 10,
        @Query("pn") page: Int = 1,
        @Query("sort") sort: Int = 0
    ): ApiResponse<PgcReviewListResult>

    /** 点赞评价 */
    @POST("/pgc/review/action/like")
    @FormUrlEncoded @Csrf
    suspend fun likeReview(
        @Field("review_id") reviewId: Long,
        @Field("action") action: Int = 1
    ): ApiResponse<Unit>

    /** 发布短评 */
    @POST("/pgc/review/short/post")
    @FormUrlEncoded @Csrf
    suspend fun postShortReview(
        @Field("media_id") mediaId: Long,
        @Field("content") content: String,
        @Field("score") score: Int
    ): ApiResponse<PgcReviewActionResult>

    data class PgcReviewListResult(
        val list: List<PgcReviewItem> = emptyList(),
        val total: Int = 0
    )

    data class PgcReviewItem(
        val id: Long = 0,
        val mid: Long = 0,
        val content: String = "",
        val score: Int = 0,
        val ctime: Long = 0,
        val like_count: Int = 0,
        val is_up: Boolean = false,
        val user: PgcReviewUser? = null
    )

    data class PgcReviewUser(
        val mid: Long = 0,
        val uname: String = "",
        val avatar: String = ""
    )

    data class PgcReviewActionResult(
        val review_id: Long = 0
    )
}