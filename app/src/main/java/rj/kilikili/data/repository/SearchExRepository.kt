package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.ISearchExApi
import com.huanli233.biliwebapi.api.interfaces.ISearchExApi.HotWord
import com.huanli233.biliwebapi.api.interfaces.ISearchExApi.TrendingItem
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchExRepository @Inject constructor() {

    suspend fun getDefaultSearch(): Result<ISearchExApi.DefaultSearchResult> {
        return bilibiliApi.api(ISearchExApi::class) { getDefaultSearch() }.apiResultNonNull()
    }

    suspend fun getTrendingRanking(): Result<List<TrendingItem>> {
        return bilibiliApi.api(ISearchExApi::class) { getTrendingRanking() }
            .apiResultNonNull().map { it.list }
    }

    suspend fun getHotWords(): Result<List<HotWord>> {
        return bilibiliApi.api(ISearchExApi::class) { getHotWords() }
            .apiResultNonNull().map { it.list }
    }
}