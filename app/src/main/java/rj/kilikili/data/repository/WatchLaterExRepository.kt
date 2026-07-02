package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IWatchLaterExApi
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchLaterExRepository @Inject constructor() {

    suspend fun clearWatchLater(): Result<Unit> {
        return bilibiliApi.api(IWatchLaterExApi::class) { clearWatchLater() }.apiResultNonNull()
    }

    suspend fun copyToWatchLater(fromAid: Long, toAid: Long): Result<Unit> {
        return bilibiliApi.api(IWatchLaterExApi::class) { copyToWatchLater(fromAid, toAid) }
            .apiResultNonNull()
    }

    suspend fun batchDeleteWatchLater(aids: List<Long>): Result<Unit> {
        return bilibiliApi.api(IWatchLaterExApi::class) { batchDeleteWatchLater(aids.joinToString(",")) }
            .apiResultNonNull()
    }
}