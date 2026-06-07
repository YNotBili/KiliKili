package com.huanli233.biliwebapi.bean.member

import com.google.gson.annotations.SerializedName

data class ExpLog(
    @SerializedName("delta") val delta: Int,
    @SerializedName("time") val time: String,
    @SerializedName("reason") val reason: String
)