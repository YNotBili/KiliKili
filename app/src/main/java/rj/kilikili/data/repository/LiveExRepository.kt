package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.ILiveExApi
import com.huanli233.biliwebapi.api.interfaces.ILiveExApi.LiveDmHistoryResult
import com.huanli233.biliwebapi.api.interfaces.ILiveExApi.LiveMedalWallResult
import com.huanli233.biliwebapi.api.interfaces.ILiveExApi.SuperChatResult
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveExRepository @Inject constructor() {

    suspend fun getMedalWall(): Result<LiveMedalWallResult> {
        return bilibiliApi.api(ILiveExApi::class) { getMedalWall() }.apiResultNonNull()
    }

    suspend fun getDanmakuHistory(roomId: Long): Result<LiveDmHistoryResult> {
        return bilibiliApi.api(ILiveExApi::class) { getDanmakuHistory(roomId) }.apiResultNonNull()
    }

    suspend fun getSuperChatMessages(roomId: Long): Result<SuperChatResult> {
        return bilibiliApi.api(ILiveExApi::class) { getSuperChatMessages(roomId) }.apiResultNonNull()
    }
}