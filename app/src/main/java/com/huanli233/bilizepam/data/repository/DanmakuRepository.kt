package com.huanli233.bilizepam.data.repository

import com.huanli233.bilizepam.api.apiResultNonNull
import com.huanli233.bilizepam.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IDanmakuApi
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
     * 获取弹幕分段（protobuf 原始数据）
     * 注意：这个接口返回的是 protobuf 二进制数据，不是 JSON
     */
    suspend fun getDanmakuSegment(
        oid: Long,
        pid: Long = 0,
        segmentIndex: Int = 1
    ): Result<okhttp3.ResponseBody> {
        return kotlin.runCatching {
            val api = bilibiliApi.getApi(IDanmakuApi::class.java)
            api.getDanmakuSegment(oid = oid, pid = pid, segmentIndex = segmentIndex)
        }
    }
}