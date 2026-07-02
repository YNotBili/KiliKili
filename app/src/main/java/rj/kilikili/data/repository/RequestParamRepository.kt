package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IRequestParamApi
import com.huanli233.biliwebapi.bean.requestParam.BiliTicket
import com.huanli233.biliwebapi.bean.requestParam.Buvids
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestParamRepository @Inject constructor() {

    suspend fun requestBuvids(): Result<Buvids> {
        return bilibiliApi.api(IRequestParamApi::class) { requestBuvids() }.apiResultNonNull()
    }

    suspend fun genWebTicket(keyId: String? = null, hexsign: String? = null, ts: String? = null): Result<BiliTicket> {
        return bilibiliApi.api(IRequestParamApi::class) { genWebTicket(keyId, hexsign, ts) }.apiResultNonNull()
    }
}