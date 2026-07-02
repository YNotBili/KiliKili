package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IReserveApi
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReserveRepository @Inject constructor() {

    suspend fun createReserve(title: String, livePlan: Long, upMid: Long, type: Int = 1): Result<Long> {
        return bilibiliApi.api(IReserveApi::class) { createReserve(title, livePlan, upMid, type) }
            .apiResultNonNull().map { it.reserve_id }
    }

    suspend fun updateReserve(reserveId: Long, title: String, livePlan: Long = 0): Result<Unit> {
        return bilibiliApi.api(IReserveApi::class) { updateReserve(reserveId, title, livePlan) }
            .apiResultNonNull()
    }

    suspend fun getReserveInfo(reserveId: Long): Result<IReserveApi.ReserveInfoResult> {
        return bilibiliApi.api(IReserveApi::class) { getReserveInfo(reserveId) }.apiResultNonNull()
    }
}