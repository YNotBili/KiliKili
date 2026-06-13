package rj.kilikili.data.di

import rj.kilikili.api.bilibiliApi
import com.huanli233.biliwebapi.api.interfaces.IReplyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideReplyApi(): IReplyApi {
        return bilibiliApi.api()
    }
}
