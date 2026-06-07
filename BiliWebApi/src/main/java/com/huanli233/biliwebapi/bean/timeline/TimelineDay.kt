package com.huanli233.biliwebapi.bean.timeline

import com.google.gson.annotations.SerializedName

/**
 * 番剧时间线 - 日期
 */
data class TimelineDay(
    @SerializedName("date") val date: String,
    @SerializedName("date_ts") val dateTs: Long,
    @SerializedName("day_of_week") val dayOfWeek: Int,
    @SerializedName("is_today") val isToday: Int,
    @SerializedName("episodes") val episodes: List<TimelineEpisode> = emptyList()
)

/**
 * 番剧时间线 - 剧集
 */
data class TimelineEpisode(
    @SerializedName("cover") val cover: String = "",
    @SerializedName("delay") val delay: Int = 0,
    @SerializedName("delay_id") val delayId: Long = 0,
    @SerializedName("delay_index") val delayIndex: String = "",
    @SerializedName("delay_reason") val delayReason: String = "",
    @SerializedName("ep_cover") val epCover: String = "",
    @SerializedName("episode_id") val episodeId: Long = 0,
    @SerializedName("pub_index") val pubIndex: String = "",
    @SerializedName("pub_time") val pubTime: String = "",
    @SerializedName("pub_ts") val pubTs: Long = 0,
    @SerializedName("published") val published: Int = 0,
    @SerializedName("follows") val follows: String = "",
    @SerializedName("plays") val plays: String = "",
    @SerializedName("season_id") val seasonId: Long = 0,
    @SerializedName("square_cover") val squareCover: String = "",
    @SerializedName("title") val title: String = ""
)