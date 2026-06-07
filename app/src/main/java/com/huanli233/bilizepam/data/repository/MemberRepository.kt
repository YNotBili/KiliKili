package com.huanli233.bilizepam.data.repository

import com.huanli233.bilizepam.api.apiResultNonNull
import com.huanli233.bilizepam.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IMemberApi
import com.huanli233.biliwebapi.bean.member.CoinLog
import com.huanli233.biliwebapi.bean.member.ExpLog
import com.huanli233.biliwebapi.bean.member.LoginRecord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor() {

    suspend fun getCoinLog(): Result<List<CoinLog>> {
        return bilibiliApi.api(IMemberApi::class) {
            getCoinLog()
        }.apiResultNonNull().map { it.list }
    }

    suspend fun getExpLog(): Result<List<ExpLog>> {
        return bilibiliApi.api(IMemberApi::class) {
            getExpLog()
        }.apiResultNonNull().map { it.list }
    }

    suspend fun getLoginRecord(mid: Long, buvid: String = ""): Result<LoginRecord> {
        return bilibiliApi.api(IMemberApi::class) {
            getLoginRecord(mid = mid, buvid = buvid)
        }.apiResultNonNull()
    }
}