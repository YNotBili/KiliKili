package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IRelationExApi
import com.huanli233.biliwebapi.bean.follow.FollowUser
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelationExRepository @Inject constructor() {

    suspend fun getFans(mid: Long, page: Int = 1): Result<List<FollowUser>> {
        return bilibiliApi.api(IRelationExApi::class) { getFans(mid, page) }
            .apiResultNonNull().map { it.list }
    }

    suspend fun getBlacklist(page: Int = 1): Result<List<FollowUser>> {
        return bilibiliApi.api(IRelationExApi::class) { getBlacklist(page) }
            .apiResultNonNull().map { it.list }
    }

    suspend fun getFollowedUpper(): Result<List<FollowUser>> {
        return bilibiliApi.api(IRelationExApi::class) { getFollowedUpper() }
            .apiResultNonNull().map { it.list }
    }

    suspend fun getSameFollowing(mid: Long): Result<List<FollowUser>> {
        return bilibiliApi.api(IRelationExApi::class) { getSameFollowing(mid) }
            .apiResultNonNull().map { it.list }
    }

    suspend fun searchFollowing(mid: Long, keyword: String): Result<List<FollowUser>> {
        return bilibiliApi.api(IRelationExApi::class) { searchFollowing(mid, keyword) }
            .apiResultNonNull().map { it.list }
    }
}