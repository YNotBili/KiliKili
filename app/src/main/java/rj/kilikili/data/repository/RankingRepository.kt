package rj.kilikili.data.repository

import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IRankingApi
import com.huanli233.biliwebapi.bean.video.VideoInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RankingRepository @Inject constructor() {

    suspend fun getRanking(rid: Int = 0, type: String = "all"): Result<List<VideoInfo>> {
        return bilibiliApi.api(IRankingApi::class) {
            getRanking(rid = rid, type = type)
        }.apiResultNonNull().map { it.list }
    }
}