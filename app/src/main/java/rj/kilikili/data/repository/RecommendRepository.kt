package rj.kilikili.data.repository

import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IRecommendApi
import com.huanli233.biliwebapi.bean.video.VideoInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendRepository @Inject constructor() {
    
    suspend fun getRelatedVideos(aid: Long, bvid: String = ""): Result<List<VideoInfo>> {
        return bilibiliApi.api(IRecommendApi::class) {
            getRelated(aid, bvid)
        }.apiResultNonNull()
    }
    
    suspend fun getHomeRecommend(
        freshType: Int = 3,
        uniqId: String = "",
        pageSize: Int = 15
    ): Result<com.huanli233.biliwebapi.bean.recommend.home.HomeRecommend> {
        return bilibiliApi.api(IRecommendApi::class) {
            getRecommend(freshType, uniqId, pageSize)
        }.apiResultNonNull()
    }
    
    suspend fun getPopular(page: Int, pageSize: Int = 10) = bilibiliApi.api(IRecommendApi::class) {
        getPopular(page, pageSize)
    }.apiResultNonNull()
    
    suspend fun getPrecious(page: Int, pageSize: Int = 10) = bilibiliApi.api(IRecommendApi::class) {
        getPrecious(page, pageSize)
    }.apiResultNonNull()
}
