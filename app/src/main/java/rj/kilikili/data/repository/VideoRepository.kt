package rj.kilikili.data.repository

import rj.kilikili.api.apiResult
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IVideoApi
import com.huanli233.biliwebapi.bean.video.PlayerInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor() {
    
    suspend fun getPlayerInfo(aid: Long, cid: Long): Result<PlayerInfo> {
        return bilibiliApi.api(IVideoApi::class) {
            getPlayerInfo(aid, cid)
        }.apiResultNonNull()
    }
    
    suspend fun reportHistory(aid: Long, cid: Long, progress: Long): Result<Unit> {
        return bilibiliApi.api(IVideoApi::class) {
            reportHistory(aid, cid, progress)
        }.apiResult().map { }
    }
}
