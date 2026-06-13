package com.huanli233.biliwebapi.bean.live

import com.google.gson.annotations.SerializedName

/**
 * 直播推荐列表
 */
data class RecommendLiveData(
    @SerializedName("room_list") val roomList: List<LiveRoom> = emptyList()
)