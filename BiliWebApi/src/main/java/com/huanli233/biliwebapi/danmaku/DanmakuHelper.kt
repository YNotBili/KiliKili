package com.huanli233.biliwebapi.danmaku

import bilibili.community.service.dm.v1.Dm
import com.huanli233.biliwebapi.api.interfaces.IDanmakuApi
import okhttp3.ResponseBody

/**
 * 解析 B 站 protobuf 弹幕分段的工具函数。
 * 网络请求 + protobuf 解码都封装在 BiliWebApi 模块内，调用方拿到的是结构化数据。
 */
suspend fun IDanmakuApi.fetchDanmakuSegmentElems(
    type: Int = 1,
    oid: Long,
    pid: Long = 0,
    segmentIndex: Int = 1,
    pullMode: Int = 1
): Result<List<Dm.DanmakuElem>> {
    return runCatching {
        val body: ResponseBody = getDanmakuSegment(
            type = type,
            oid = oid,
            pid = pid,
            segmentIndex = segmentIndex,
            pullMode = pullMode
        )
        val bytes = body.bytes()
        if (bytes.isEmpty()) emptyList()
        else Dm.DmSegMobileReply.parseFrom(bytes).elemsList
    }
}
