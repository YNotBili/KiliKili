package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.ILiveApi
import com.huanli233.biliwebapi.api.interfaces.ILiveApi.FollowedLiveData
import com.huanli233.biliwebapi.bean.live.LivePlayInfo
import com.huanli233.biliwebapi.bean.live.LiveRoom
import com.huanli233.biliwebapi.bean.live.RecommendLiveData
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveRepository @Inject constructor() {

    suspend fun getRoomInfo(roomId: Long): Result<LiveRoom> {
        return bilibiliApi.api(ILiveApi::class) { getRoomInfo(roomId.toString()) }
            .apiResultNonNull()
    }

    suspend fun getPlayInfo(roomId: Long, qn: Int = 10000): Result<LivePlayInfo> {
        return bilibiliApi.api(ILiveApi::class) { getPlayInfo(roomId = roomId.toString(), qn = qn) }
            .apiResultNonNull()
    }

    suspend fun getRecommendLive(): Result<RecommendLiveData> {
        return bilibiliApi.api(ILiveApi::class) { getRecommendLive() }.apiResultNonNull()
    }

    suspend fun getFollowedLive(page: Int = 1, pageSize: Int = 10): Result<FollowedLiveData> {
        return bilibiliApi.api(ILiveApi::class) { getFollowedLive(page, pageSize) }
            .apiResultNonNull()
    }
}