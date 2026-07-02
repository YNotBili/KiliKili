package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IPgcExApi
import com.huanli233.biliwebapi.bean.bangumi.BangumiDetail
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PgcExRepository @Inject constructor() {

    suspend fun getIndexCondition(seasonType: Int = 1, type: Int = 1, area: String = "", isFinish: Int = -1): Result<IPgcExApi.PgcIndexConditionResult> {
        return bilibiliApi.api(IPgcExApi::class) { getIndexCondition(seasonType, type, area, isFinish) }
            .apiResultNonNull()
    }

    suspend fun getIndexResult(type: Int = 1, area: String = "", seasonType: Int = 1, page: Int = 1, pageSize: Int = 20): Result<List<BangumiDetail>> {
        return bilibiliApi.api(IPgcExApi::class) { getIndexResult(type, area, seasonType, page, pageSize) }
            .apiResultNonNull().map { it.list }
    }

    suspend fun getPgcRank(seasonType: Int = 1, day: Int = 3): Result<List<BangumiDetail>> {
        return bilibiliApi.api(IPgcExApi::class) { getPgcRank(seasonType, day) }
            .apiResultNonNull().map { it.list }
    }

    suspend fun followPgc(seasonId: Long): Result<Unit> {
        return bilibiliApi.api(IPgcExApi::class) { followPgc(seasonId) }.apiResultNonNull()
    }

    suspend fun unfollowPgc(seasonId: Long): Result<Unit> {
        return bilibiliApi.api(IPgcExApi::class) { unfollowPgc(seasonId) }.apiResultNonNull()
    }
}