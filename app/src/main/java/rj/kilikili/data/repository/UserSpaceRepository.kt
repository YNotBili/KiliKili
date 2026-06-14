package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IUserSpaceApi
import com.huanli233.biliwebapi.api.interfaces.IUserSpaceApi.PopularSeriesItem
import com.huanli233.biliwebapi.api.interfaces.IUserSpaceApi.UpStatResult
import com.huanli233.biliwebapi.bean.video.VideoInfo
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSpaceRepository @Inject constructor() {

    suspend fun getUpStat(mid: Long): Result<UpStatResult> {
        return bilibiliApi.api(IUserSpaceApi::class) { getUpStat(mid) }.apiResultNonNull()
    }

    suspend fun getTopVideo(mid: Long): Result<VideoInfo> {
        return bilibiliApi.api(IUserSpaceApi::class) { getTopVideo(mid) }.apiResultNonNull()
    }

    suspend fun getPopularSeriesList(): Result<List<PopularSeriesItem>> {
        return bilibiliApi.api(IUserSpaceApi::class) { getPopularSeriesList() }
            .apiResultNonNull().map { it.list }
    }

    suspend fun getPopularSeriesDetail(seriesId: Int): Result<List<VideoInfo>> {
        return bilibiliApi.api(IUserSpaceApi::class) { getPopularSeriesDetail(seriesId) }
            .apiResultNonNull().map { it.list }
    }
}