package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.bean.electric.ElectricPanel
import retrofit2.http.GET
import retrofit2.http.Query

interface IElectricApi {

    /**
     * 获取充电公示面板
     * @param upMid UP主mid
     */
    @GET("/x/ugcpay-rank/elec/month/up")
    suspend fun getElectricPanel(
        @Query("up_mid") upMid: Long
    ): ApiResponse<ElectricPanel>
}