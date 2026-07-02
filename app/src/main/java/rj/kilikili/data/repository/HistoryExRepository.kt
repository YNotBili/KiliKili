package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IHistoryExApi
import com.huanli233.biliwebapi.api.interfaces.IHistoryExApi.HistorySearchResult
import com.huanli233.biliwebapi.api.interfaces.IHistoryExApi.HistoryStatusResult
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryExRepository @Inject constructor() {

    suspend fun clearHistory(): Result<Unit> {
        return bilibiliApi.api(IHistoryExApi::class) { clearHistory() }.apiResultNonNull()
    }

    suspend fun deleteHistoryEntry(kid: Long): Result<Unit> {
        return bilibiliApi.api(IHistoryExApi::class) { deleteHistoryEntry(kid) }.apiResultNonNull()
    }

    suspend fun getHistoryStatus(): Result<HistoryStatusResult> {
        return bilibiliApi.api(IHistoryExApi::class) { getHistoryStatus() }.apiResultNonNull()
    }

    suspend fun setPauseHistory(isShadow: Int = 1): Result<Unit> {
        return bilibiliApi.api(IHistoryExApi::class) { setPauseHistory(isShadow) }.apiResultNonNull()
    }

    suspend fun searchHistory(mid: Long, keyword: String, page: Int = 1, pageSize: Int = 20): Result<HistorySearchResult> {
        return bilibiliApi.api(IHistoryExApi::class) { searchHistory(mid, keyword, page, pageSize) }
            .apiResultNonNull()
    }
}