package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IUploadApi
import com.huanli233.biliwebapi.api.interfaces.IUploadApi.ImageUploadResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadRepository @Inject constructor() {

    suspend fun uploadImage(byteArray: ByteArray, contentType: String = "image/jpeg"): Result<ImageUploadResult> {
        val body = byteArray.toRequestBody(contentType.toMediaTypeOrNull())
        return bilibiliApi.api(IUploadApi::class) { uploadImage(body) }.apiResultNonNull()
    }
}