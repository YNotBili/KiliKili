package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IArticleApi
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Deprecated("Move to opus api")
class ArticleRepository @Inject constructor() {

    suspend fun getArticle(cvid: Long) = bilibiliApi.api(IArticleApi::class) { getArticle(cvid) }.apiResultNonNull()
}