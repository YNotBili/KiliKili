package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import com.huanli233.biliwebapi.httplib.annotation.WbiSign
import retrofit2.http.*

interface IFavoriteExApi {

    /** 获取收藏夹资源列表 (分页) */
    @WbiSign
    @GET("/x/v3/fav/resource/list")
    suspend fun getFavResourceList(
        @Query("media_id") mediaId: Long,
        @Query("pn") page: Int = 1,
        @Query("ps") pageSize: Int = 20,
        @Query("keyword") keyword: String = "",
        @Query("order") order: String = "mtime",
        @Query("type") type: Int = 0,
        @Query("tid") tid: Int = 0,
        @Query("platform") platform: String = "web"
    ): ApiResponse<FavResourceListResult>

    /** 创建收藏夹 */
    @POST("/x/v3/fav/folder/add")
    @FormUrlEncoded @Csrf
    suspend fun addFolder(
        @Field("title") title: String,
        @Field("intro") intro: String = "",
        @Field("privacy") privacy: Int = 0,
        @Field("cover") cover: String = ""
    ): ApiResponse<FavFolderActionResult>

    /** 编辑收藏夹 */
    @POST("/x/v3/fav/folder/edit")
    @FormUrlEncoded @Csrf
    suspend fun editFolder(
        @Field("media_id") mediaId: Long,
        @Field("title") title: String,
        @Field("intro") intro: String = "",
        @Field("cover") cover: String = ""
    ): ApiResponse<FavFolderActionResult>

    /** 删除收藏夹 */
    @POST("/x/v3/fav/folder/del")
    @FormUrlEncoded @Csrf
    suspend fun deleteFolder(
        @Field("media_id") mediaId: Long
    ): ApiResponse<FavFolderActionResult>

    /** 批量取消收藏 */
    @POST("/x/v3/fav/resource/batch-deal")
    @FormUrlEncoded @Csrf
    suspend fun batchDeal(
        @Field("resources") resources: String,
        @Field("type") type: Int = 2,
        @Field("add_media_ids") addMediaIds: String = "",
        @Field("del_media_ids") delMediaIds: String = ""
    ): ApiResponse<Unit>

    data class FavResourceListResult(
        val medias: List<FavResourceItem> = emptyList(),
        val has_more: Boolean = false,
        val total: Int = 0
    )

    data class FavResourceItem(
        val id: Long = 0,
        val title: String = "",
        val cover: String = "",
        val intro: String = "",
        val page: Int = 0,
        val type: Int = 0,
        val duration: Long = 0,
        val upper: FavUpper? = null,
        val attr: Int = 0,
        val cnt_info: FavCntInfo? = null
    )

    data class FavUpper(
        val mid: Long = 0,
        val name: String = "",
        val face: String = ""
    )

    data class FavCntInfo(
        val collect: Int = 0,
        val play: Int = 0,
        val danmaku: Int = 0
    )

    data class FavFolderActionResult(
        val media_id: Long = 0,
        val failed: List<Long> = emptyList()
    )
}