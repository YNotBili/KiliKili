package com.huanli233.biliwebapi.api.interfaces

import com.huanli233.biliwebapi.bean.ApiResponse
import com.huanli233.biliwebapi.httplib.Domains
import com.huanli233.biliwebapi.httplib.annotation.API
import com.huanli233.biliwebapi.httplib.annotation.Csrf
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IWatchLaterExApi {

    @POST("/x/v2/history/toview/clear")
    @FormUrlEncoded @Csrf
    suspend fun clearWatchLater(): ApiResponse<Unit>

    @POST("/x/v2/history/toview/copy")
    @FormUrlEncoded @Csrf
    suspend fun copyToWatchLater(
        @Field("from_aid") fromAid: Long,
        @Field("to_aid") toAid: Long
    ): ApiResponse<Unit>

    @POST("/x/v2/history/toview/v2/dels")
    @FormUrlEncoded @Csrf
    suspend fun batchDeleteWatchLater(
        @Field("aids") aids: String
    ): ApiResponse<Unit>
}

@API(Domains.VC_API_URL)
interface IIMApi {

    @POST("/account/v1/user/cards")
    @FormUrlEncoded
    suspend fun getUserCards(
        @Field("uids") uids: String
    ): ApiResponse<ImUserCardsResult>

    data class ImUserCardsResult(val data: List<ImUserCard> = emptyList())
    data class ImUserCard(val mid: Long = 0, val name: String = "", val face: String = "")
}