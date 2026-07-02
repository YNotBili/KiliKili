package rj.kilikili.data.repository

import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import bilibili.community.service.dm.v1.Dm
import com.huanli233.biliwebapi.api.interfaces.IDanmakuApi
import com.huanli233.biliwebapi.danmaku.fetchDanmakuSegmentElems
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DanmakuRepository @Inject constructor() {

    suspend fun sendDanmaku(
        oid: Long,
        message: String,
        progress: Long,
        aid: Long = 0,
        bvid: String = "",
        color: Int = 0xFFFFFF,
        fontSize: Int = 25,
        mode: Int = 1
    ): Result<Unit> {
        return bilibiliApi.api(IDanmakuApi::class) {
            sendDanmaku(
                oid = oid,
                message = message,
                progress = progress,
                aid = aid,
                bvid = bvid,
                color = color,
                fontSize = fontSize,
                mode = mode
            )
        }.apiResultNonNull()
    }

    suspend fun likeDanmaku(oid: Long, dmid: Long, op: Int = 1): Result<Unit> {
        return bilibiliApi.api(IDanmakuApi::class) {
            likeDanmaku(oid = oid, dmid = dmid, op = op)
        }.apiResultNonNull()
    }

    suspend fun recallDanmaku(cid: Long, dmid: Long): Result<Unit> {
        return bilibiliApi.api(IDanmakuApi::class) {
            recallDanmaku(cid = cid, dmid = dmid)
        }.apiResultNonNull()
    }

    /**
     * 获取弹幕分段。HTTP 请求与 protobuf 解码均由 BiliWebApi 模块完成，
     * 这里只做透传参数。
     */
    suspend fun getDanmakuSegment(
        oid: Long,
        pid: Long = 0,
        segmentIndex: Int = 1,
        pullMode: Int = 1
    ): Result<List<Dm.DanmakuElem>> {
        val api = bilibiliApi.getApi(IDanmakuApi::class.java)
        return api.fetchDanmakuSegmentElems(
            oid = oid,
            pid = pid,
            segmentIndex = segmentIndex,
            pullMode = pullMode
        )
    }
}