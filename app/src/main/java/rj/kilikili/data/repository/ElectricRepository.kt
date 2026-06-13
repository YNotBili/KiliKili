package rj.kilikili.data.repository

import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IElectricApi
import com.huanli233.biliwebapi.bean.electric.ElectricPanel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ElectricRepository @Inject constructor() {

    suspend fun getElectricPanel(upMid: Long): Result<ElectricPanel> {
        return bilibiliApi.api(IElectricApi::class) {
            getElectricPanel(upMid = upMid)
        }.apiResultNonNull()
    }
}