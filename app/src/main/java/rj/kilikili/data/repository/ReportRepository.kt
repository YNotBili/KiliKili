package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IReportApi
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor() {

    suspend fun reportDanmaku(oid: Long, dmid: Long, reason: String = "其他", content: String = ""): Result<Unit> {
        return bilibiliApi.api(IReportApi::class) { reportDanmaku(oid, dmid, reason, content) }
            .apiResultNonNull()
    }

    suspend fun reportReply(oid: Long, rpid: Long, reason: Int = 1, content: String = ""): Result<Unit> {
        return bilibiliApi.api(IReportApi::class) { reportReply(oid, rpid, reason, content) }
            .apiResultNonNull()
    }
}