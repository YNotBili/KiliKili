package rj.kilikili.data.repository

import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IVipApi
import com.huanli233.biliwebapi.bean.vip.VipInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VipRepository @Inject constructor() {

    suspend fun getVipInfo(): Result<VipInfo> {
        return bilibiliApi.api(IVipApi::class) {
            getVipInfo()
        }.apiResultNonNull()
    }

    suspend fun addExperience(): Result<Unit> {
        return bilibiliApi.api(IVipApi::class) {
            addExperience()
        }.apiResultNonNull()
    }
}