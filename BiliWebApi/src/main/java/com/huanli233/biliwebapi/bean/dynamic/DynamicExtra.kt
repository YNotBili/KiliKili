package com.huanli233.biliwebapi.bean.dynamic

import com.google.gson.annotations.SerializedName

/**
 * 动态门户 - 最近更新的UP列表
 */
data class DynamicPortal(
    @SerializedName("up_list") val upList: List<UpInfo> = emptyList()
)

data class UpInfo(
    @SerializedName("mid") val mid: Long,
    @SerializedName("uname") val uname: String,
    @SerializedName("face") val face: String = "",
    @SerializedName("has_update") val hasUpdate: Boolean = false
)

/**
 * 动态更新检查结果
 */
data class DynamicUpdateResult(
    @SerializedName("update_num") val updateNum: Int = 0
)

/**
 * AT用户搜索结果
 */
data class MentionResult(
    @SerializedName("groups") val groups: List<MentionGroup> = emptyList()
)

data class MentionGroup(
    @SerializedName("group_type") val groupType: String = "",
    @SerializedName("items") val items: List<MentionItem> = emptyList()
)

data class MentionItem(
    @SerializedName("name") val name: String = "",
    @SerializedName("uid") val uid: String = "",
    @SerializedName("face") val face: String = "",
)

/**
 * 复杂动态发布请求体
 */
data class DynamicPublishRequest(
    val dyn_req: DynReq
)

data class DynReq(
    val content: DynContent,
    val scene: Int = 1,
    val meta: DynMeta = DynMeta(),
    val pics: List<String>? = null,
    val topic: DynTopic? = null,
    val option: DynOption? = null
)

data class DynContent(
    val contents: List<ContentItem>
)

data class ContentItem(
    val raw_text: String,
    val type: Int = 1,
    val biz_id: String = ""
)

data class DynMeta(
    val app_meta: AppMeta = AppMeta()
)

data class AppMeta(
    val from: String = "create.dynamic.web",
    val mobi_app: String = "web"
)

data class DynTopic(
    val id: Long? = null,
    val name: String? = null
)

data class DynOption(
    val screen_set: Int = 0
)