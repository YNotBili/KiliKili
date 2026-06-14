package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IVoteApi {

    @GET("/x/vote/vote_info")
    suspend fun getVoteInfo(@Query("vote_id") voteId: Long): ApiResponse<VoteInfoResult>

    @POST("/x/vote/do_vote")
    @FormUrlEncoded @Csrf
    suspend fun castVote(
        @Field("vote_id") voteId: Long,
        @Field("votes") votes: String,
        @Field("vote") vote: Int = 1
    ): ApiResponse<Unit>

    @POST("/x/vote/create")
    @FormUrlEncoded @Csrf
    suspend fun createVote(
        @Field("title") title: String,
        @Field("vote_type") voteType: Int = 0,
        @Field("options") options: String,
        @Field("deadline") deadline: Long = 0,
        @Field("max_num") maxNum: Int = 1
    ): ApiResponse<VoteCreateResult>

    data class VoteInfoResult(val vote_id: Long = 0, val title: String = "", val type: Int = 0)
    data class VoteCreateResult(val vote_id: Long = 0)
}