package com.huanli233.bilizepam.ui.viewmodel

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huanli233.bilizepam.data.download.DownloadDisplayItem
import com.huanli233.bilizepam.data.download.DownloadEntity
import com.huanli233.bilizepam.data.download.DownloadManager
import com.huanli233.bilizepam.data.download.DownloadStatus
import com.huanli233.bilizepam.data.download.SourceType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class ScannedFileInfo(
    val uri: String,
    val fileName: String
)

@HiltViewModel
class DownloadListViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val downloadManager: DownloadManager
) : ViewModel() {

    private val _displayItems = MutableStateFlow<List<DownloadDisplayItem>>(emptyList())
    val displayItems: StateFlow<List<DownloadDisplayItem>> = _displayItems.asStateFlow()

    init {
        viewModelScope.launch {
            downloadManager.observeAll().collect { dbEntities ->
                val merged = mergeWithScannedFiles(dbEntities)
                _displayItems.value = merged
            }
        }
    }

    private suspend fun mergeWithScannedFiles(dbEntities: List<DownloadEntity>): List<DownloadDisplayItem> {
        val scannedFiles = scanDownloadDirectory()
        val result = mutableListOf<DownloadDisplayItem>()

        // DB 记录：过滤掉文件不存在的 SUCCEEDED 项
        for (entity in dbEntities) {
            val isSucceeded = entity.status == DownloadStatus.SUCCEEDED
            val fileExists = if (isSucceeded && entity.contentUri != null) {
                checkFileExists(entity.contentUri)
            } else {
                true
            }

            if (isSucceeded && !fileExists) {
                // DB 标记完成但文件已不存在 → 跳过
                continue
            }

            val matchedScanned = if (isSucceeded && entity.contentUri != null) {
                scannedFiles.remove(entity.contentUri)
            } else null

            result.add(
                DownloadDisplayItem(
                    id = "db_${entity.id}",
                    fileName = entity.fileName,
                    contentUri = entity.contentUri,
                    coverUrl = entity.coverUrl,
                    status = entity.status,
                    progress = entity.progress,
                    fileExists = fileExists,
                    sourceType = if (matchedScanned != null) SourceType.BOTH else SourceType.DATABASE,
                    dbId = entity.id,
                    dbKey = entity.key
                )
            )
        }

        // 扫描到但 DB 没有的文件 → SCANNED
        for ((_, scanned) in scannedFiles) {
            result.add(
                DownloadDisplayItem(
                    id = "scan_${scanned.fileName}",
                    fileName = scanned.fileName,
                    contentUri = scanned.uri,
                    coverUrl = null,
                    status = DownloadStatus.SUCCEEDED,
                    progress = 100,
                    fileExists = true,
                    sourceType = SourceType.SCANNED,
                    dbId = null,
                    dbKey = null
                )
            )
        }

        return result.sortedByDescending { it.dbId ?: 0L }
    }

    private fun scanDownloadDirectory(): MutableMap<String, ScannedFileInfo> {
        val result = mutableMapOf<String, ScannedFileInfo>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME
            )
            val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ? AND ${MediaStore.MediaColumns.IS_PENDING} = 0"
            val selectionArgs = arrayOf("Download/BiliZepam/%")

            try {
                appContext.contentResolver.query(
                    collection, projection, selection, selectionArgs, null
                )?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val nameCol = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val name = cursor.getString(nameCol)
                        val uri = ContentUris.withAppendedId(collection, id).toString()
                        result[uri] = ScannedFileInfo(uri, name)
                    }
                }
            } catch (_: Exception) {
                // 权限不足等
            }
        } else {
            val dir = java.io.File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "BiliZepam"
            )
            if (dir.isDirectory) {
                dir.listFiles()?.forEach { file ->
                    if (file.isFile && file.name.endsWith(".mp4", ignoreCase = true)) {
                        val uri = file.toURI().toString()
                        result[uri] = ScannedFileInfo(
                            uri = uri,
                            fileName = file.name
                        )
                    }
                }
            }
        }
        return result
    }

    private fun checkFileExists(contentUri: String): Boolean {
        return try {
            val uri = android.net.Uri.parse(contentUri)
            appContext.contentResolver.openFileDescriptor(uri, "r")?.use { true } ?: false
        } catch (_: Exception) {
            false
        }
    }

    fun cancel(downloadId: Long) {
        viewModelScope.launch {
            downloadManager.cancel(downloadId)
        }
    }

    fun retry(downloadId: Long) {
        viewModelScope.launch {
            downloadManager.retry(downloadId)
        }
    }

    fun delete(displayItem: DownloadDisplayItem, deleteFile: Boolean) {
        viewModelScope.launch {
            when (displayItem.sourceType) {
                SourceType.DATABASE, SourceType.BOTH -> {
                    displayItem.dbId?.let { id ->
                        downloadManager.delete(id, deleteFile)
                    }
                }
                SourceType.SCANNED -> {
                    if (deleteFile && displayItem.contentUri != null) {
                        kotlin.runCatching {
                            appContext.contentResolver.delete(
                                android.net.Uri.parse(displayItem.contentUri), null, null
                            )
                        }
                    }
                }
            }
        }
    }
}