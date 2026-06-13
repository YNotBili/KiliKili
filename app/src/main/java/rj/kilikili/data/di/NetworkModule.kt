package rj.kilikili.data.di

import rj.kilikili.api.AppCookieManager
import com.huanli233.biliwebapi.httplib.CookieManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCookieJar(appCookieJar: AppCookieManager): CookieManager {
        return appCookieJar
    }

}