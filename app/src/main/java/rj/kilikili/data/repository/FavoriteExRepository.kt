package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IFavoriteExApi
import com.huanli233.biliwebapi.api.interfaces.IFavoriteExApi.FavResourceItem
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteExRepository @Inject constructor() {

    suspend fun getResourceList(mediaId: Long, page: Int = 1, pageSize: Int = 20, keyword: String = ""): Result<IFavoriteExApi.FavResourceListResult> {
        return bilibiliApi.api(IFavoriteExApi::class) { getFavResourceList(mediaId, page, pageSize, keyword) }
            .apiResultNonNull()
    }

    suspend fun addFolder(title: String, intro: String = "", privacy: Int = 0, cover: String = ""): Result<Long> {
        return bilibiliApi.api(IFavoriteExApi::class) { addFolder(title, intro, privacy, cover) }
            .apiResultNonNull().map { it.media_id }
    }

    suspend fun editFolder(mediaId: Long, title: String, intro: String = "", cover: String = ""): Result<Long> {
        return bilibiliApi.api(IFavoriteExApi::class) { editFolder(mediaId, title, intro, cover) }
            .apiResultNonNull().map { it.media_id }
    }

    suspend fun deleteFolder(mediaId: Long): Result<Long> {
        return bilibiliApi.api(IFavoriteExApi::class) { deleteFolder(mediaId) }
            .apiResultNonNull().map { it.media_id }
    }

    suspend fun batchDeal(resources: String, addMediaIds: String = "", delMediaIds: String = "", type: Int = 2): Result<Unit> {
        return bilibiliApi.api(IFavoriteExApi::class) { batchDeal(resources, type, addMediaIds, delMediaIds) }
            .apiResultNonNull()
    }
}