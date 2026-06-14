package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface IFollowTagApi {

    @GET("/x/relation/tags")
    suspend fun getTags(): ApiResponse<TagListResult>

    @POST("/x/relation/tag/create")
    @FormUrlEncoded @Csrf
    suspend fun createTag(@Field("tag") name: String): ApiResponse<TagActionResult>

    @POST("/x/relation/tag/update")
    @FormUrlEncoded @Csrf
    suspend fun updateTag(@Field("tagid") tagId: Long, @Field("name") name: String): ApiResponse<Unit>

    @POST("/x/relation/tag/del")
    @FormUrlEncoded @Csrf
    suspend fun deleteTag(@Field("tagid") tagId: Long): ApiResponse<Unit>

    @POST("/x/relation/tags/addUsers")
    @FormUrlEncoded @Csrf
    suspend fun addUsersToTag(
        @Field("tagids") tagIds: String,
        @Field("fids") fids: String
    ): ApiResponse<Unit>

    data class TagListResult(val data: List<TagItem> = emptyList())
    data class TagItem(val tagid: Long = 0, val name: String = "", val count: Int = 0)
    data class TagActionResult(val tagid: Long = 0)
}