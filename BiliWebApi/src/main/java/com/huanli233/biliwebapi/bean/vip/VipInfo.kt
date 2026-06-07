package com.huanli233.biliwebapi.bean.vip

import com.google.gson.annotations.SerializedName

/**
 * VIP会员信息
 */
data class VipInfo(
    @SerializedName("is_short_vip") val isShortVip: Boolean = false,
    @SerializedName("is_freight_open") val isFreightOpen: Boolean = false,
    @SerializedName("level") val level: Int = 0,
    @SerializedName("cur_exp") val curExp: Long = 0,
    @SerializedName("next_exp") val nextExp: Long = 0,
    @SerializedName("is_vip") val isVip: Boolean = false,
    @SerializedName("is_senior_member") val isSeniorMember: Int = 0,
    @SerializedName("format060102") val format060102: Int = 0,
    @SerializedName("is_overdue_vip") val isOverdueVip: Boolean = false,
    @SerializedName("vip_status") val vipStatus: Int = 0,
    @SerializedName("vip_type") val vipType: Int = 0,
    @SerializedName("keeptime_end") val keeptimeEnd: Long = 0,
    @SerializedName("vip_due_date") val vipDueDate: Long = 0,
    @SerializedName("vip_is_annual") val vipIsAnnual: Boolean = false,
    @SerializedName("vip_is_month") val vipIsMonth: Boolean = false,
    @SerializedName("vip_is_new_user") val vipIsNewUser: Boolean = false,
    @SerializedName("bind_phone") val bindPhone: String = "",
    @SerializedName("list") val privilegeList: List<VipPrivilege> = emptyList()
)

data class VipPrivilege(
    @SerializedName("type") val type: Int = 0,
    @SerializedName("state") val state: Int = 0,
    @SerializedName("expire_time") val expireTime: Long = 0,
    @SerializedName("vip_type") val vipType: Int = 0,
    @SerializedName("next_receive_days") val nextReceiveDays: Int = 0,
    @SerializedName("period_end_unix") val periodEndUnix: Long = 0
)