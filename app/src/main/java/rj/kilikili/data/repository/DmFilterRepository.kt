package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IDmApi
import com.huanli233.biliwebapi.api.interfaces.IDmApi.DmFilterItem
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DmFilterRepository @Inject constructor() {

    suspend fun getFilters(): Result<List<DmFilterItem>> {
        return bilibiliApi.api(IDmApi::class) { getDmFilters() }
            .apiResultNonNull().map { it.list }
    }

    suspend fun addFilter(content: String, type: Int = 1): Result<Unit> {
        return bilibiliApi.api(IDmApi::class) { addDmFilter(content, type) }.apiResultNonNull()
    }

    suspend fun deleteFilters(ids: List<Long>): Result<Unit> {
        val idsStr = ids.joinToString(",")
        return bilibiliApi.api(IDmApi::class) { delDmFilter(idsStr) }.apiResultNonNull()
    }
}