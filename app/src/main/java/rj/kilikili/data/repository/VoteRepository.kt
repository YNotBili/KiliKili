package rj.kilikili.data.repository

import com.huanli233.biliwebapi.api.interfaces.IVoteApi
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoteRepository @Inject constructor() {

    suspend fun getVoteInfo(voteId: Long): Result<IVoteApi.VoteInfoResult> {
        return bilibiliApi.api(IVoteApi::class) { getVoteInfo(voteId) }.apiResultNonNull()
    }

    suspend fun castVote(voteId: Long, votes: String, vote: Int = 1): Result<Unit> {
        return bilibiliApi.api(IVoteApi::class) { castVote(voteId, votes, vote) }.apiResultNonNull()
    }

    suspend fun createVote(title: String, options: String, deadline: Long = 0, voteType: Int = 0, maxNum: Int = 1): Result<Long> {
        return bilibiliApi.api(IVoteApi::class) { createVote(title, voteType, options, deadline, maxNum) }
            .apiResultNonNull().map { it.vote_id }
    }
}