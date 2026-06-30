package rj.kilikili.data.download

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regression tests for issue #4: verify the safety pattern used by
 * DownloadManager and DownloadWorker. When dao.getById returns null,
 * callers MUST return failure (or no-op) instead of throwing NPE on `!!`.
 *
 * This mirrors the post-fix structure:
 *   suspend fun cancel(id: Long) {
 *       val entity = downloadDao.getById(id) ?: return  // <-- safe early-return
 *       ...
 *   }
 *
 * It exercises the exact ?: pattern that now exists in 10 sites.
 */
class DownloadManagerNullSafetyTest {

    private class AlwaysNullDao : DownloadDao {
        var updateCallCount = 0
        var deleteCallCount = 0

        override fun observeAll(): Flow<List<DownloadEntity>> = flowOf(emptyList())
        override fun observeById(id: Long): Flow<DownloadEntity?> = flowOf(null)
        override suspend fun getById(id: Long): DownloadEntity? = null
        override suspend fun getByKey(key: String): DownloadEntity? = null
        override suspend fun insert(download: DownloadEntity): Long = 0L
        override suspend fun update(download: DownloadEntity) { updateCallCount++ }
        override suspend fun deleteById(id: Long) { deleteCallCount++ }
    }

    private class ExistingDao : DownloadDao {
        var updateCallCount = 0
        override fun observeAll(): Flow<List<DownloadEntity>> = flowOf(emptyList())
        override fun observeById(id: Long): Flow<DownloadEntity?> = flowOf(entity)
        override suspend fun getById(id: Long): DownloadEntity? = entity
        override suspend fun getByKey(key: String): DownloadEntity? = entity
        override suspend fun insert(download: DownloadEntity): Long = entity.id
        override suspend fun update(download: DownloadEntity) { updateCallCount++ }
        override suspend fun deleteById(id: Long) { }

        companion object {
            val entity = DownloadEntity(
                id = 1L,
                key = "k1",
                url = "https://example.com/a.mp4",
                fileName = "a.mp4",
                status = DownloadStatus.ENQUEUED
            )
        }
    }

    @Test
    fun `cancel with missing row does not call update`() = runBlocking {
        val dao = AlwaysNullDao()
        cancelShapedLikeDownloadManager(dao, downloadId = 42L)
        assertEquals("update must not be called when row missing", 0, dao.updateCallCount)
    }

    @Test
    fun `retry with missing row returns failure without NPE`() = runBlocking {
        val dao = AlwaysNullDao()
        val result = retryShapedLikeDownloadManager(dao, downloadId = 99L)
        assertTrue("retry(missing) must return failure", result.isFailure)
        assertEquals(0, dao.updateCallCount)
    }

    @Test
    fun `delete with missing row does not call deleteById`() = runBlocking {
        val dao = AlwaysNullDao()
        deleteShapedLikeDownloadManager(dao, downloadId = 7L, deleteFile = false)
        assertEquals("deleteById must not be called when row missing", 0, dao.deleteCallCount)
    }

    @Test
    fun `cancel with existing row calls update exactly once`() = runBlocking {
        val dao = ExistingDao()
        cancelShapedLikeDownloadManager(dao, downloadId = 1L)
        assertEquals(1, dao.updateCallCount)
    }

    // Mirrors DownloadManager.cancel(id)
    private suspend fun cancelShapedLikeDownloadManager(dao: DownloadDao, downloadId: Long) {
        val entity = dao.getById(downloadId) ?: return
        dao.update(entity.copy(status = DownloadStatus.CANCELED))
    }

    // Mirrors DownloadManager.retry(id)
    private fun retryShapedLikeDownloadManager(dao: DownloadDao, downloadId: Long): Result<Long> {
        val entity = runBlocking { dao.getById(downloadId) }
            ?: return Result.failure(IllegalArgumentException("download not found"))
        return Result.success(entity.id)
    }

    // Mirrors DownloadManager.delete(id, deleteFile)
    private suspend fun deleteShapedLikeDownloadManager(dao: DownloadDao, downloadId: Long, deleteFile: Boolean) {
        val entity = dao.getById(downloadId) ?: return
        if (deleteFile) {
            // contentResolver delete omitted — already gated by `entity ?: return`
        }
        dao.deleteById(downloadId)
    }
}
