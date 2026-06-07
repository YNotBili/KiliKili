package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.timeline.TimelineDay
import retrofit2.http.GET
import retrofit2.http.Query

interface ITimelineApi {

    /**
     * 获取番剧时间线
     * @param types 类型，如 "1,2,3,4,5,6,7"
     * @param before 往前天数
     * @param after 往后天数
     */
    @GET("/pgc/web/timeline")
    suspend fun getTimeline(
        @Query("types") types: String = "1,2,3,4,5,6,7",
        @Query("before") before: Int = 7,
        @Query("after") after: Int = 7
    ): TimelineResponse
}

data class TimelineResponse(
    val code: Int,
    val message: String,
    val result: List<TimelineDay>
)