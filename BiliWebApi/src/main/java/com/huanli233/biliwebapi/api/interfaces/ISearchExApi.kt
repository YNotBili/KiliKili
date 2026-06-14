package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/** 搜索扩展 API */
interface ISearchExApi {

    @GET("/x/web-interface/wbi/search/default")
    suspend fun getDefaultSearch(): ApiResponse<DefaultSearchResult>

    @GET("/x/v2/search/trending/ranking")
    suspend fun getTrendingRanking(): ApiResponse<TrendingResult>

    /** 搜索热点词 */
    @GET("https://s.search.bilibili.com/main/hotword")
    suspend fun getHotWords(): ApiResponse<HotWordResult>

    data class DefaultSearchResult(val show_name: String = "", val name: String = "")
    data class TrendingResult(val list: List<TrendingItem> = emptyList())
    data class TrendingItem(val keyword: String = "", val status: String = "", val icon: String = "")
    data class HotWordResult(val list: List<HotWord> = emptyList())
    data class HotWord(val keyword: String = "", val show_name: String = "", val icon: String = "")
}