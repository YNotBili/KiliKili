package rj.kilikili.data.repository

import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.ITimelineApi
import com.huanli233.biliwebapi.bean.timeline.TimelineDay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimelineRepository @Inject constructor() {

    suspend fun getTimeline(
        types: String = "1,2,3,4,5,6,7",
        before: Int = 7,
        after: Int = 7
    ): kotlin.Result<List<TimelineDay>> {
        return kotlin.runCatching {
            bilibiliApi.getApi(ITimelineApi::class.java).getTimeline(
                types = types, before = before, after = after
            ).let { response ->
                if (response.code == 0) response.result
                else throw Exception("${response.code}: ${response.message}")
            }
        }
    }
}