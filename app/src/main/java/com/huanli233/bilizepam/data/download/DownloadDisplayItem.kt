package com.huanli233.bilizepam.data.download

enum class SourceType {
    DATABASE,
    SCANNED,
    BOTH
}

data class DownloadDisplayItem(
    val id: String,
    val fileName: String,
    val contentUri: String?,
    val coverUrl: String?,
    val status: DownloadStatus,
    val progress: Int,
    val fileExists: Boolean,
    val sourceType: SourceType,
    val dbId: Long? = null,
    val dbKey: String? = null
)