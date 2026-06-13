package rj.kilikili.data.repository

import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IEmoteApi
import com.huanli233.biliwebapi.bean.emote.EmotePackage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmoteRepository @Inject constructor() {

    suspend fun getEmotePanel(business: String = "reply"): Result<List<EmotePackage>> {
        return bilibiliApi.api(IEmoteApi::class) {
            getEmotePanel(business = business)
        }.apiResultNonNull().map { it.packages }
    }

    suspend fun getInUsePackages(business: String = "reply"): Result<List<EmotePackage>> {
        return bilibiliApi.api(IEmoteApi::class) {
            getInUsePackages(business = business)
        }.apiResultNonNull().map { it.packages }
    }

    suspend fun getMyPackages(
        business: String = "reply",
        page: Int = 1,
        type: Int = 0
    ): Result<List<EmotePackage>> {
        return bilibiliApi.api(IEmoteApi::class) {
            getMyPackages(business = business, page = page, type = type)
        }.apiResultNonNull().map { it.packages }
    }
}