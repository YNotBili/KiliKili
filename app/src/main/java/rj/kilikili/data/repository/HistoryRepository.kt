package rj.kilikili.data.repository

import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IHistoryApi
import com.huanli233.biliwebapi.bean.history.HistoryResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor() {
    
    suspend fun getHistory(
        viewAt: Long = 0,
        business: String = "",
        max: Long = 0
    ): Result<HistoryResponse> {
        return bilibiliApi.api(IHistoryApi::class) {
            getHistory(
                type = "archive",
                viewAt = viewAt,
                business = business,
                max = max
            )
        }.apiResultNonNull()
    }
}
