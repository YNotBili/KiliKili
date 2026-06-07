package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.member.CoinLog
import com.huanli233.biliwebapi.bean.member.ExpLog
import com.huanli233.biliwebapi.bean.member.LoginRecord
import retrofit2.http.GET
import retrofit2.http.Query

interface IMemberApi {

    /**
     * 获取硬币记录
     */
    @GET("/x/member/web/coin/log")
    suspend fun getCoinLog(): ApiResponse<CoinLogData>

    /**
     * 获取经验记录
     */
    @GET("/x/member/web/exp/log")
    suspend fun getExpLog(
        @Query("jsonp") jsonp: String = "jsonp",
        @Query("web_location") webLocation: String = "333.33"
    ): ApiResponse<ExpLogData>

    /**
     * 获取登录记录
     */
    @GET("/x/safecenter/login_notice")
    suspend fun getLoginRecord(
        @Query("mid") mid: Long,
        @Query("buvid") buvid: String = ""
    ): ApiResponse<LoginRecord>

    data class CoinLogData(
        val list: List<CoinLog>
    )

    data class ExpLogData(
        val list: List<ExpLog>
    )
}