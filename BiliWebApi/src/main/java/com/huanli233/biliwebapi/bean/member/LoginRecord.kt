package com.huanli233.biliwebapi.bean.member

import com.google.gson.annotations.SerializedName

data class LoginRecord(
    @SerializedName("mid") val mid: Long,
    @SerializedName("device_name") val deviceName: String = "未知设备",
    @SerializedName("login_type") val loginType: String = "未知方式",
    @SerializedName("login_time") val loginTime: String = "",
    @SerializedName("location") val location: String = "未知位置",
    @SerializedName("ip") val ip: String = ""
)