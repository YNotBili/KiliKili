package rj.kilikili.data.repository

import rj.kilikili.api.apiResult
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IBangumiApi
import com.huanli233.biliwebapi.api.interfaces.IBangumiApi.FollowedBangumiItem
import com.huanli233.biliwebapi.bean.bangumi.BangumiDetail
import com.huanli233.biliwebapi.bean.bangumi.BangumiSections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BangumiRepository @Inject constructor() {

    suspend fun getBangumiInfo(mediaId: Long): Result<BangumiDetail> {
        return bilibiliApi.api(IBangumiApi::class) {
            getBangumiInfo(mediaId)
        }.apiResultNonNull()
    }

    suspend fun getBangumiSections(seasonId: Long): Result<BangumiSections> {
        return bilibiliApi.api(IBangumiApi::class) {
            getBangumiSections(seasonId)
        }.apiResultNonNull()
    }

    suspend fun getMediaIdFromEpId(epId: Long): Result<Long> {
        return bilibiliApi.api(IBangumiApi::class) {
            getMediaIdFromEpId(epId)
        }.apiResultNonNull().map { it.media_id }
    }

    suspend fun getFollowedBangumi(page: Int = 1): Result<List<FollowedBangumiItem>> {
        return bilibiliApi.api(IBangumiApi::class) {
            getFollowedBangumi(page = page)
        }.apiResultNonNull().map { it.list }
    }
}
