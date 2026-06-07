package com.huanli233.biliwebapi.bean.electric

import com.google.gson.annotations.SerializedName

/**
 * 充电公示面板
 */
data class ElectricPanel(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("list") val list: List<ElectricUser> = emptyList(),
    @SerializedName("total_count") val totalCount: Int = 0,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("special_day") val specialDay: Int = 0
) {
    fun hasData(): Boolean = count > 0 && list.isNotEmpty()
}