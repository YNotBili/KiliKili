package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.Domains
import com.huanli233.biliwebapi.httplib.annotation.API
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.*

interface IUploadApi {

    @API(Domains.BASE_API_URL)
    @POST("/x/dynamic/feed/draw/upload_bfs")
    @Csrf
    suspend fun uploadBfs(
        @Header("Content-Type") contentType: String = "image/jpeg",
        @Body body: okhttp3.RequestBody
    ): ApiResponse<BfsUploadResult>

    @API(Domains.BASE_API_URL)
    @POST("/x/upload/web/image")
    @Csrf
    suspend fun uploadImage(
        @Body body: okhttp3.RequestBody
    ): ApiResponse<ImageUploadResult>

    data class BfsUploadResult(
        val image_url: String = "",
        val image_width: Int = 0,
        val image_height: Int = 0,
        val file_size: Int = 0
    )

    data class ImageUploadResult(
        val url: String = "",
        val width: Int = 0,
        val height: Int = 0
    )
}