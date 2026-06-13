package rj.kilikili.data.repository

import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IDynamicApi
import com.huanli233.biliwebapi.bean.dynamic.Dynamic
import com.huanli233.biliwebapi.bean.dynamic.DynamicFeedResponse
import com.huanli233.biliwebapi.bean.dynamic.DynamicPortal
import com.huanli233.biliwebapi.bean.dynamic.DynamicPublishRequest
import com.huanli233.biliwebapi.bean.dynamic.DynamicUpdateResult
import com.huanli233.biliwebapi.bean.dynamic.DynReq
import com.huanli233.biliwebapi.bean.dynamic.DynContent
import com.huanli233.biliwebapi.bean.dynamic.ContentItem
import com.huanli233.biliwebapi.bean.dynamic.MentionResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicRepository @Inject constructor() {

    suspend fun getDynamicFeed(
        offset: String? = null,
        type: String = "all"
    ): Result<DynamicFeedResponse> {
        return bilibiliApi.api(IDynamicApi::class) {
            getDynamicFeed(offset = offset, type = type)
        }.apiResultNonNull()
    }

    suspend fun getUserSpaceDynamicFeed(
        hostMid: Long,
        offset: String? = null
    ): Result<DynamicFeedResponse> {
        return bilibiliApi.api(IDynamicApi::class) {
            getUserSpaceDynamicFeed(hostMid = hostMid, offset = offset)
        }.apiResultNonNull()
    }

    suspend fun getDynamic(id: String, rid: String = ""): Result<Dynamic> {
        return bilibiliApi.api(IDynamicApi::class) {
            getDynamic(id, rid)
        }.apiResultNonNull().mapCatching { it.item }
    }

    suspend fun getDynamicDetail(id: String): Result<Dynamic> {
        return getDynamic(id)
    }

    suspend fun likeDynamic(dynamicId: String, action: Int): Result<Unit> {
        return bilibiliApi.api(IDynamicApi::class) {
            like(dynamicId, action)
        }.apiResultNonNull()
    }

    // ===== 新增 =====

    suspend fun publishTextDynamic(content: String): Result<Long> {
        return bilibiliApi.api(IDynamicApi::class) {
            publishTextDynamic(content = content)
        }.apiResultNonNull().map { it.dynamic_id }
    }

    suspend fun deleteDynamic(dynamicId: Long): Result<Unit> {
        return bilibiliApi.api(IDynamicApi::class) {
            deleteDynamic(dynamicId)
        }.apiResultNonNull()
    }

    suspend fun repostDynamic(dynamicId: Long, content: String): Result<Long> {
        return bilibiliApi.api(IDynamicApi::class) {
            repostDynamic(dynamicId, content)
        }.apiResultNonNull().map { it.dynamic_id }
    }

    suspend fun mentionSearch(keyword: String): Result<MentionResult> {
        return bilibiliApi.api(IDynamicApi::class) {
            mentionSearch(keyword)
        }.apiResultNonNull()
    }

    suspend fun getPortal(): Result<DynamicPortal> {
        return bilibiliApi.api(IDynamicApi::class) {
            getPortal()
        }.apiResultNonNull()
    }

    suspend fun checkUpdate(updateBaseline: Long): Result<DynamicUpdateResult> {
        return bilibiliApi.api(IDynamicApi::class) {
            checkUpdate(updateBaseline = updateBaseline)
        }.apiResultNonNull()
    }

    suspend fun publishComplexDynamic(
        contents: List<ContentItem>,
        scene: Int = 1,
        pics: List<String>? = null
    ): Result<Long> {
        val request = DynamicPublishRequest(
            dyn_req = DynReq(
                content = DynContent(contents = contents),
                scene = scene,
                pics = pics
            )
        )
        return bilibiliApi.api(IDynamicApi::class) {
            publishComplexDynamic(request)
        }.apiResultNonNull().map { it.dyn_id }
    }
}