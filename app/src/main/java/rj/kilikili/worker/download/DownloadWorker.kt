package rj.kilikili.worker.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import rj.kilikili.KiliKili
import rj.kilikili.api.setOkHttpSsl
import rj.kilikili.data.di.AppDependenciesEntryPoint
import rj.kilikili.data.download.DownloadDao
import rj.kilikili.data.download.DownloadEntity
import rj.kilikili.data.download.DownloadStatus
import dagger.hilt.EntryPoints
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class DownloadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val entryPoint = EntryPoints.get(
        KiliKili.application,
        AppDependenciesEntryPoint::class.java
    )

    private val downloadDao: DownloadDao = entryPoint.downloadDao()

    private suspend fun getOrFail(downloadId: Long, errorMsg: String): DownloadEntity? {
        val entity = downloadDao.getById(downloadId)
        if (entity == null) {
            android.util.Log.w(TAG, "download row missing: id=$downloadId, reason=$errorMsg")
        }
        return entity
    }

    override suspend fun doWork(): Result {
        val downloadId = inputData.getLong(KEY_DOWNLOAD_ID, -1L)
        if (downloadId <= 0L) return Result.failure()

        val entity = downloadDao.getById(downloadId) ?: return Result.failure()

        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            downloadDao.update(entity.copy(status = DownloadStatus.RUNNING, updatedAt = now, errorMessage = null))

            val notificationId = downloadId.toInt().coerceAtLeast(1)
            setForeground(createForegroundInfo(notificationId, entity.fileName, 0))

            try {
                val headers: Map<String, String> = parseHeaders(entity.headersJson)

                val client = setOkHttpSsl(OkHttpClient.Builder()).build()
                val reqBuilder = Request.Builder().url(entity.url)
                headers.forEach { (k, v) -> reqBuilder.addHeader(k, v) }
                val response = client.newCall(reqBuilder.build()).execute()

                if (!response.isSuccessful) {
                    val msg = "HTTP ${response.code}"
                    getOrFail(downloadId, msg)?.let { e ->
                        downloadDao.update(e.copy(
                            status = DownloadStatus.FAILED,
                            errorMessage = msg,
                            updatedAt = System.currentTimeMillis(),
                            finishedAt = System.currentTimeMillis()
                        ))
                    }
                    return@withContext Result.failure()
                }

                val body = response.body ?: run {
                    getOrFail(downloadId, "empty body")?.let { e ->
                        downloadDao.update(e.copy(
                            status = DownloadStatus.FAILED,
                            errorMessage = "empty body",
                            updatedAt = System.currentTimeMillis(),
                            finishedAt = System.currentTimeMillis()
                        ))
                    }
                    return@withContext Result.failure()
                }

                val totalBytes = body.contentLength().coerceAtLeast(0L)
                val mime = entity.mimeType ?: response.header("Content-Type")

                val uri = createDownloadItem(entity.fileName, mime)
                if (uri == null) {
                    getOrFail(downloadId, "media store insert failed")?.let { e ->
                        downloadDao.update(e.copy(
                            status = DownloadStatus.FAILED,
                            errorMessage = "failed to create MediaStore item",
                            updatedAt = System.currentTimeMillis(),
                            finishedAt = System.currentTimeMillis()
                        ))
                    }
                    return@withContext Result.failure()
                }

                val resolver = applicationContext.contentResolver
                resolver.openOutputStream(uri, "w")?.use { out ->
                    body.byteStream().use { input ->
                        val buf = ByteArray(DEFAULT_BUFFER_SIZE)
                        var downloaded = 0L
                        var lastUpdateTs = 0L

                        while (true) {
                            if (isStopped) {
                                getOrFail(downloadId, "stopped")?.let { e ->
                                    downloadDao.update(e.copy(
                                        status = DownloadStatus.CANCELED,
                                        updatedAt = System.currentTimeMillis(),
                                        finishedAt = System.currentTimeMillis()
                                    ))
                                }
                                return@withContext Result.success()
                            }

                            val read = input.read(buf)
                            if (read <= 0) break
                            out.write(buf, 0, read)
                            downloaded += read

                            val nowTs = System.currentTimeMillis()
                            if (nowTs - lastUpdateTs >= 600) {
                                val progress = if (totalBytes > 0) {
                                    ((downloaded * 100L) / totalBytes).toInt().coerceIn(0, 99)
                                } else 0

                                lastUpdateTs = nowTs
                                getOrFail(downloadId, "progress update")?.let { e ->
                                    downloadDao.update(e.copy(
                                        downloadedBytes = downloaded,
                                        totalBytes = totalBytes,
                                        progress = progress,
                                        updatedAt = nowTs,
                                        contentUri = uri.toString(),
                                        status = DownloadStatus.RUNNING
                                    ))
                                }
                                setForeground(createForegroundInfo(notificationId, entity.fileName, progress))
                            }
                        }
                        out.flush()

                        finalizePending(uri)

                        getOrFail(downloadId, "finalize")?.let { e ->
                            downloadDao.update(e.copy(
                                downloadedBytes = downloaded,
                                totalBytes = totalBytes,
                                progress = 100,
                                status = DownloadStatus.SUCCEEDED,
                                updatedAt = System.currentTimeMillis(),
                                finishedAt = System.currentTimeMillis(),
                                contentUri = uri.toString()
                            ))
                        }
                        setForeground(createForegroundInfo(notificationId, entity.fileName, 100))
                    }
                } ?: run {
                    getOrFail(downloadId, "open output stream failed")?.let { e ->
                        downloadDao.update(e.copy(
                            status = DownloadStatus.FAILED,
                            errorMessage = "failed to open output stream",
                            updatedAt = System.currentTimeMillis(),
                            finishedAt = System.currentTimeMillis()
                        ))
                    }
                    return@withContext Result.failure()
                }

                Result.success()
            } catch (e: SecurityException) {
                getOrFail(downloadId, "security: ${e.message}")?.let { row ->
                    downloadDao.update(row.copy(
                        status = DownloadStatus.FAILED,
                        errorMessage = e.message ?: "permission denied",
                        updatedAt = System.currentTimeMillis(),
                        finishedAt = System.currentTimeMillis()
                    ))
                }
                Result.failure()
            } catch (e: IOException) {
                getOrFail(downloadId, "io: ${e.message}")?.let { row ->
                    downloadDao.update(row.copy(
                        status = DownloadStatus.FAILED,
                        errorMessage = e.message ?: "io error",
                        updatedAt = System.currentTimeMillis(),
                        finishedAt = System.currentTimeMillis()
                    ))
                }
                Result.retry()
            } catch (e: Exception) {
                getOrFail(downloadId, "exception: ${e.message}")?.let { row ->
                    downloadDao.update(row.copy(
                        status = DownloadStatus.FAILED,
                        errorMessage = e.message ?: "unknown error",
                        updatedAt = System.currentTimeMillis(),
                        finishedAt = System.currentTimeMillis()
                    ))
                }
                Result.failure()
            }
        }
    }

    private fun parseHeaders(headersJson: String?): Map<String, String> {
        if (headersJson.isNullOrBlank()) return emptyMap()
        val type = object : TypeToken<Map<String, String>>() {}.type
        return kotlin.runCatching { Gson().fromJson<Map<String, String>>(headersJson, type) }.getOrNull() ?: emptyMap()
    }

    private fun createDownloadItem(fileName: String, mimeType: String?): Uri? {
        val resolver = applicationContext.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            mimeType?.let { put(MediaStore.MediaColumns.MIME_TYPE, it) }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/BiliZepam")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            } else {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val target = java.io.File(dir, "BiliZepam/$fileName")
                target.parentFile?.mkdirs()
                put(MediaStore.MediaColumns.DATA, target.absolutePath)
            }
        }

        return resolver.insert(collection, values)
    }

    private fun finalizePending(uri: Uri) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.IS_PENDING, 0)
        }
        applicationContext.contentResolver.update(uri, values, null, null)
    }

    private fun createForegroundInfo(notificationId: Int, title: String, progress: Int): ForegroundInfo {
        ensureChannel()

        val notification: Notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(title)
            .setContentText(if (progress >= 100) "下载完成" else "下载中 $progress%")
            .setOngoing(progress in 0..99)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress.coerceIn(0, 100), false)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val mgr = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val existing = mgr.getNotificationChannel(CHANNEL_ID)
        if (existing != null) return

        mgr.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_LOW)
        )
    }

    companion object {
        const val TAG_DOWNLOAD = "download"
        const val KEY_DOWNLOAD_ID = "download_id"
        private const val CHANNEL_ID = "downloads"
        private const val TAG = "DownloadWorker"
    }
}
