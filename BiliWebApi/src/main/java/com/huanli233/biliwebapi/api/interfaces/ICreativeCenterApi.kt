package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import retrofit2.http.GET

interface ICreativeCenterApi {

    /**
     * 获取创作中心数据统计
     */
    @GET("/x/web/index/stat")
    suspend fun getVideoStat(): ApiResponse<CreatorStats>

    /**
     * 获取B站时长
     */
    @GET("/x/web/index/scrolls")
    suspend fun getBeUPTime(): ApiResponse<CreatorScrolls>

    data class CreatorStats(
        val view: Long = 0,
        val like: Long = 0,
        val follower: Long = 0,
        val aid: Long = 0,
        val `new`: NewStat? = null,
        val read: Long = 0,
        val play: Long = 0,
        val increased: Int = 0
    )

    data class NewStat(
        val play: Long = 0,
        val read: Long = 0
    )

    data class CreatorScrolls(
        val scrolls: List<CreatorScroll> = emptyList()
    )

    data class CreatorScroll(
        val name: String,
        val count: Int = 0,
        val unit: String = ""
    )
}