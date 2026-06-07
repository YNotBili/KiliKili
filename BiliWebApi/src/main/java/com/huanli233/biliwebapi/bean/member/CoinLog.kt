package com.huanli233.biliwebapi.bean.member

import com.google.gson.annotations.SerializedName

data class CoinLog(
    @SerializedName("time") val time: String,
    @SerializedName("delta") val delta: Int,
    @SerializedName("reason") val reason: String
)