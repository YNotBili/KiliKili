package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IFollowTagApi
import com.huanli233.biliwebapi.api.interfaces.IFollowTagApi.TagItem
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FollowTagRepository @Inject constructor() {

    suspend fun getTags(): Result<List<TagItem>> {
        return bilibiliApi.api(IFollowTagApi::class) { getTags() }
            .apiResultNonNull().map { it.data }
    }

    suspend fun createTag(name: String): Result<Long> {
        return bilibiliApi.api(IFollowTagApi::class) { createTag(name) }
            .apiResultNonNull().map { it.tagid }
    }

    suspend fun deleteTag(tagId: Long): Result<Unit> {
        return bilibiliApi.api(IFollowTagApi::class) { deleteTag(tagId) }.apiResultNonNull()
    }

    suspend fun addUsersToTag(tagIds: String, fids: String): Result<Unit> {
        return bilibiliApi.api(IFollowTagApi::class) { addUsersToTag(tagIds, fids) }.apiResultNonNull()
    }
}