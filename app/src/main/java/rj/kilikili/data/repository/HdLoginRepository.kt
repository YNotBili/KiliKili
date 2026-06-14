package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.ILoginApi
import com.huanli233.biliwebapi.bean.login.TvCookie
import com.huanli233.biliwebapi.bean.login.TvQrCodeAuth
import com.huanli233.biliwebapi.bean.login.TvQrCodePoll
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HdLoginRepository @Inject constructor() {

    suspend fun getAuthCode(): Result<TvQrCodeAuth> {
        return bilibiliApi.api(ILoginApi::class) {
            getTvAuthCode()
        }.apiResultNonNull()
    }

    suspend fun pollQrCode(authCode: String): Result<TvQrCodePoll> {
        return bilibiliApi.api(ILoginApi::class) {
            tvQrCodePoll(authCode)
        }.apiResultNonNull()
    }
}