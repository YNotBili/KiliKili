package com.huanli233.biliwebapi.bean.electric

import com.google.gson.annotations.SerializedName

/**
 * 充电用户
 */
data class ElectricUser(
    @SerializedName("uname") val uname: String = "",
    @SerializedName("avatar") val avatar: String = "",
    @SerializedName("mid") val mid: Long = 0,
    @SerializedName("pay_mid") val payMid: Long = 0,
    @SerializedName("rank") val rank: Int = 0,
    @SerializedName("trend_type") val trendType: Int = 0,
    @SerializedName("message") val message: String = "",
    @SerializedName("msg_hidden") val msgHidden: Int = 0,
    @SerializedName("vip_info") val vipInfo: ElectricVipInfo? = null
)

data class ElectricVipInfo(
    @SerializedName("vipDueMsec") val vipDueMsec: Long = 0,
    @SerializedName("vipStatus") val vipStatus: Int = 0,
    @SerializedName("vipType") val vipType: Int = 0
)