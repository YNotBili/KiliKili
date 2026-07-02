package com.huanli233.biliwebapi.bean.article

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArticleInfo(
    val id: Long = 0L,
    @SerializedName("title") val title: String? = null,
    @SerializedName("author_name") val authorName: String? = null,
    @SerializedName("author_mid") val authorMid: Long = 0L,
    @SerializedName("author_face") val authorFace: String? = null,
    @SerializedName("banner") val banner: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("ctime") val ctime: Long = 0L,
    @SerializedName("stats") val stats: ArticleStats? = null,
    @SerializedName("words") val words: Int = 0,
    @SerializedName("dynamic_id_str") val dynIdStr: String? = null
) : Parcelable

@Parcelize
data class ArticleStats(
    val view: Int = 0,
    val like: Int = 0,
    val favorite: Int = 0,
    val reply: Int = 0,
    val share: Int = 0,
    val coin: Int = 0
) : Parcelable
