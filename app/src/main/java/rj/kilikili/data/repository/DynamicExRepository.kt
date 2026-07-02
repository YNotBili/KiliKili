package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IDynamicExApi
import com.huanli233.biliwebapi.api.interfaces.IDynamicExApi.DynamicSearchItem
import com.huanli233.biliwebapi.api.interfaces.IDynamicExApi.UnreadDynamicResult
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicExRepository @Inject constructor() {

    suspend fun getUnread(): Result<UnreadDynamicResult> {
        return bilibiliApi.api(IDynamicExApi::class) { getUnreadDynamic() }.apiResultNonNull()
    }

    suspend fun removeDynamic(dynamicId: Long): Result<Unit> {
        return bilibiliApi.api(IDynamicExApi::class) { removeDynamic(dynamicId) }.apiResultNonNull()
    }

    suspend fun editDynamic(dynamicId: Long, content: String): Result<Unit> {
        return bilibiliApi.api(IDynamicExApi::class) { editDynamic(dynamicId, content) }.apiResultNonNull()
    }

    suspend fun setTop(dynamicId: Long): Result<Unit> {
        return bilibiliApi.api(IDynamicExApi::class) { setTopDynamic(dynamicId) }.apiResultNonNull()
    }

    suspend fun removeTop(dynamicId: Long): Result<Unit> {
        return bilibiliApi.api(IDynamicExApi::class) { rmTopDynamic(dynamicId) }.apiResultNonNull()
    }

    suspend fun searchSpaceDynamics(hostMid: Long, keyword: String, offset: String = ""): Result<List<DynamicSearchItem>> {
        return bilibiliApi.api(IDynamicExApi::class) { searchSpaceDynamics(hostMid, keyword, offset) }
            .apiResultNonNull().map { it.items }
    }
}