package com.huanli233.bilizepam.data.repository

import com.huanli233.bilizepam.api.apiResultNonNull
import com.huanli233.bilizepam.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.ICreativeCenterApi
import com.huanli233.biliwebapi.api.interfaces.ICreativeCenterApi.CreatorScrolls
import com.huanli233.biliwebapi.api.interfaces.ICreativeCenterApi.CreatorStats
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreativeCenterRepository @Inject constructor() {

    suspend fun getVideoStat(): Result<CreatorStats> {
        return bilibiliApi.api(ICreativeCenterApi::class) {
            getVideoStat()
        }.apiResultNonNull()
    }

    suspend fun getBeUPTime(): Result<CreatorScrolls> {
        return bilibiliApi.api(ICreativeCenterApi::class) {
            getBeUPTime()
        }.apiResultNonNull()
    }
}