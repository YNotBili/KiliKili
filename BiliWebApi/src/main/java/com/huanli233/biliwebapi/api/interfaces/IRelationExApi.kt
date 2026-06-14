package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.follow.FollowUser
import retrofit2.http.GET
import retrofit2.http.Query

/** 关系扩展 API */
interface IRelationExApi {

    @GET("/x/relation/fans")
    suspend fun getFans(
        @Query("vmid") mid: Long,
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 20
    ): ApiResponse<FansResult>

    @GET("/x/relation/same/followings")
    suspend fun getSameFollowing(
        @Query("vmid") mid: Long
    ): ApiResponse<SameFollowingResult>

    @GET("/x/relation/followings/followed_upper")
    suspend fun getFollowedUpper(): ApiResponse<FollowedUpperResult>

    @GET("/x/relation/blacks")
    suspend fun getBlacklist(
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 20
    ): ApiResponse<BlacksResult>

    @GET("/x/relation/followings/search")
    suspend fun searchFollowing(
        @Query("mid") mid: Long,
        @Query("keyword") keyword: String
    ): ApiResponse<FollowSearchResult>

    data class FansResult(val list: List<FollowUser> = emptyList())
    data class SameFollowingResult(val list: List<FollowUser> = emptyList())
    data class FollowedUpperResult(val list: List<FollowUser> = emptyList())
    data class BlacksResult(val list: List<FollowUser> = emptyList())
    data class FollowSearchResult(val list: List<FollowUser> = emptyList())
}