package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.ITopicApi
import com.huanli233.biliwebapi.api.interfaces.ITopicApi.TopicFeedItem
import com.huanli233.biliwebapi.api.interfaces.ITopicApi.TopicRcmdItem
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicRepository @Inject constructor() {

    suspend fun getTopicFeed(topicId: Long, offset: String = ""): Result<List<TopicFeedItem>> {
        return bilibiliApi.api(ITopicApi::class) { getTopicFeed(topicId, offset) }
            .apiResultNonNull().map { it.items }
    }

    suspend fun getTopicRcmd(topicId: Long): Result<List<TopicRcmdItem>> {
        return bilibiliApi.api(ITopicApi::class) { getTopicRcmd(topicId) }
            .apiResultNonNull().map { it.items }
    }

    suspend fun likeTopic(topicId: Long, action: Int = 1): Result<Unit> {
        return bilibiliApi.api(ITopicApi::class) { likeTopic(topicId, action) }.apiResultNonNull()
    }
}