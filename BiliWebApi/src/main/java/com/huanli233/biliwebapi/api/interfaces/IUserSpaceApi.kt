package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.video.VideoInfo
import com.huanli233.biliwebapi.httplib.annotation.DmImg
import com.huanli233.biliwebapi.httplib.annotation.WbiSign
import retrofit2.http.GET
import retrofit2.http.Query

interface IUserSpaceApi {

    @GET("/x/space/upstat")
    suspend fun getUpStat(@Query("mid") mid: Long): ApiResponse<UpStatResult>

    @GET("/x/space/top/arc")
    suspend fun getTopVideo(@Query("mid") mid: Long): ApiResponse<VideoInfo>

    @GET("/x/space/coin/video")
    suspend fun getRecentCoinVideos(@Query("mid") mid: Long): ApiResponse<CoinVideoResult>

    @WbiSign @DmImg
    @GET("/x/space/like/video")
    suspend fun getRecentLikeVideos(
        @Query("mid") mid: Long,
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 10
    ): ApiResponse<LikeVideoResult>

    @GET("/x/web-interface/popular/series/list")
    suspend fun getPopularSeriesList(): ApiResponse<PopularSeriesListResult>

    @GET("/x/web-interface/popular/series/one")
    suspend fun getPopularSeriesDetail(@Query("series_id") seriesId: Int): ApiResponse<PopularSeriesDetailResult>

    data class UpStatResult(
        val archive: UpStatArchive? = null,
        val article: UpStatArchive? = null,
        val likes: Long = 0
    )
    data class UpStatArchive(val view: Long = 0)
    data class CoinVideoResult(val list: List<VideoInfo> = emptyList())
    data class LikeVideoResult(val list: List<LikedVideo> = emptyList())
    data class LikedVideo(val aid: Long = 0, val title: String = "", val pic: String = "", val duration: Long = 0)
    data class PopularSeriesListResult(val list: List<PopularSeriesItem> = emptyList())
    data class PopularSeriesItem(
        val number: Int = 0,
        val name: String = "",
        val subject: String = "",
        val status: Int = 0
    )
    data class PopularSeriesDetailResult(val list: List<VideoInfo> = emptyList())
}