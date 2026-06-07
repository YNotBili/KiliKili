package com.huanli233.biliwebapi.bean.emote

import com.google.gson.annotations.SerializedName

/**
 * 表情包
 */
data class EmotePackage(
    @SerializedName("id") val id: Int,
    @SerializedName("text") val text: String,
    @SerializedName("url") val url: String = "",
    @SerializedName("type") val type: Int = 0,
    @SerializedName("attr") val attr: Int = 0,
    @SerializedName("meta") val meta: EmotePackageMeta? = null,
    @SerializedName("flags") val flags: EmotePackageFlags? = null,
    @SerializedName("emote") val emotes: List<Emote> = emptyList()
) {
    val size: Int get() = meta?.size ?: 1
    val itemId: Int get() = meta?.itemId ?: -1
    val permanent: Boolean get() = flags?.permanent ?: false
}

data class EmotePackageMeta(
    @SerializedName("size") val size: Int = 1,
    @SerializedName("item_id") val itemId: Int = -1
)

data class EmotePackageFlags(
    @SerializedName("permanent") val permanent: Boolean = false
)

/**
 * 单个表情
 */
data class Emote(
    @SerializedName("id") val id: Int,
    @SerializedName("package_id") val packageId: Int,
    @SerializedName("text") val name: String,
    @SerializedName("url") val url: String = "",
    @SerializedName("meta") val meta: EmoteMeta? = null
) {
    val size: Int get() = meta?.size ?: 1
    val alias: String get() = meta?.alias ?: ""
}

data class EmoteMeta(
    @SerializedName("size") val size: Int = 1,
    @SerializedName("alias") val alias: String = ""
)