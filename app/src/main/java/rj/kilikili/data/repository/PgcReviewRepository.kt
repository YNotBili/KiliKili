package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IPgcReviewApi
import com.huanli233.biliwebapi.api.interfaces.IPgcReviewApi.PgcReviewItem
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PgcReviewRepository @Inject constructor() {

    suspend fun getLongReviews(mediaId: Long, page: Int = 1, pageSize: Int = 10): Result<List<PgcReviewItem>> {
        return bilibiliApi.api(IPgcReviewApi::class) { getLongReviews(mediaId, pageSize, page) }
            .apiResultNonNull().map { it.list }
    }

    suspend fun getShortReviews(mediaId: Long, sort: Int = 0, page: Int = 1, pageSize: Int = 10): Result<List<PgcReviewItem>> {
        return bilibiliApi.api(IPgcReviewApi::class) { getShortReviews(mediaId, pageSize, page, sort) }
            .apiResultNonNull().map { it.list }
    }

    suspend fun likeReview(reviewId: Long, action: Int = 1): Result<Unit> {
        return bilibiliApi.api(IPgcReviewApi::class) { likeReview(reviewId, action) }.apiResultNonNull()
    }

    suspend fun postShortReview(mediaId: Long, content: String, score: Int): Result<Long> {
        return bilibiliApi.api(IPgcReviewApi::class) { postShortReview(mediaId, content, score) }
            .apiResultNonNull().map { it.review_id }
    }
}