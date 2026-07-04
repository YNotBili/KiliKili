package rj.kilikili.ui.screens.player

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import rj.kilikili.api.apiResultNonNull
import rj.kilikili.api.bilibiliApi
import rj.kilikili.data.download.DownloadManager
import rj.kilikili.data.download.DownloadRequest
import rj.kilikili.data.repository.DanmakuRepository
import rj.kilikili.data.repository.VideoRepository
import rj.kilikili.data.setting.LocalData
import com.huanli233.biliwebapi.api.interfaces.IVideoApi
import com.huanli233.biliwebapi.bean.video.PlayUrlData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser
import rj.kilikili.data.proto.DanmakuSource
import rj.kilikili.parser.BilibiliProtoDanmakuParser
import rj.kilikili.parser.DanmakuListDataSource
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private const val BILIBILI_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"
private const val BILIBILI_REFERER = "https://bilibili.com"

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val downloadManager: DownloadManager,
    private val danmakuRepository: DanmakuRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    /** 弹幕来源（设置项），供 UI 触发 parser 重建 */
    val danmakuSource: StateFlow<DanmakuSource> = LocalData.settingsStateFlow
        .map { it?.playerSettings?.danmakuSource ?: DanmakuSource.DANMAKU_SOURCE_PROTOBUF }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DanmakuSource.DANMAKU_SOURCE_PROTOBUF)
    
    private var progressReportJob: Job? = null
    private var lastReportedProgress: Long = 0

    private val currentPlayTag = AtomicInteger(0)
    private val preparationCancelled = AtomicBoolean(false)

    /** 每次 seek 完成时更新，供 UI 观察以触发刷新 */
    val seekCompletedPosition = mutableStateOf(0L)

    val ijkPlayer: IjkMediaPlayer = IjkMediaPlayer().apply {
        Log.d("PlayerViewModel", "Initializing IjkMediaPlayer")
        
        // 基础配置
        setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", BILIBILI_USER_AGENT)
        setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 30000000)
        setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1)
        setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http_persistent", 1)
        setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0)
        setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek")
        setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "file,http,https,tcp,tls,crypto")
        setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "safe", 0)
        
        // 播放器配置
        setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 0)
        setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 3000)
        setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1)
        setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0)
        setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1)
        setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1)
        
        // 添加渲染相关选项解决绿屏问题
        setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "vout", "android")
        setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)
        
        // 根据设置配置解码器
        val settings = LocalData.settingsStateFlow.value?.playerSettings
        if (settings?.useSoftwareDecoder == true) {
            Log.d("PlayerViewModel", "Using software decoder")
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0)
        } else {
            Log.d("PlayerViewModel", "Using hardware decoder")
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1)
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-sync", 0)
        }
        
        // 根据 Surface 类型设置渲染格式
        if (settings?.useTextureView == true) {
            Log.d("PlayerViewModel", "Using TextureView - letting system choose optimal format")
            // TextureView 通常与 RGB 格式兼容性更好，让系统自动选择
        } else {
            Log.d("PlayerViewModel", "Using SurfaceView - setting YV12 format")
            setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_YV12.toLong())
        }
        
        // 设置监听器
        setOnPreparedListener { player ->
            Log.d("PlayerViewModel", "Player prepared")
            val tagAtFire = currentPlayTag.get()
            if (preparationCancelled.get() || tagAtFire != currentPlayTag.get()) {
                Log.d("PlayerViewModel", "Stale onPrepared callback (tag $tagAtFire), ignoring")
                runCatching { player.stop() }
                return@setOnPreparedListener
            }
            val autoPlay = LocalData.settingsStateFlow.value?.playerSettings?.autoPlay ?: false
            if (autoPlay) {
                // 延迟一点时间确保 Surface 已经设置
                viewModelScope.launch {
                    delay(200)
                    if (preparationCancelled.get() || tagAtFire != currentPlayTag.get()) {
                        return@launch
                    }
                    if (player.isPlayable) {
                        Log.d("PlayerViewModel", "Auto-starting playback")
                        player.start()
                    }
                }
            }
        }
        
        setOnVideoSizeChangedListener { _, width, height, _, _ ->
            Log.d("PlayerViewModel", "Video size changed: ${width}x${height}")
            if (width > 0 && height > 0) {
                val aspectRatio = width.toFloat() / height.toFloat()
                _uiState.value = _uiState.value.copy(videoAspectRatio = aspectRatio)
            }
        }
        
        setOnErrorListener { _, what, extra ->
            Log.e("PlayerViewModel", "Player error: what=$what, extra=$extra")
            if (preparationCancelled.get()) return@setOnErrorListener true
            false
        }
        
        setOnInfoListener { _, what, extra ->
            when (what) {
                IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                    Log.d("PlayerViewModel", "First frame rendered")
                }
                IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    Log.d("PlayerViewModel", "Buffering start")
                }
                IMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    Log.d("PlayerViewModel", "Buffering end")
                }
            }
            false
        }

        setOnSeekCompleteListener {
            seekCompletedPosition.value = ijkPlayer.currentPosition
            Log.d("PlayerViewModel", "Seek complete at ${ijkPlayer.currentPosition}ms")
        }
    }

     fun enqueueDownload(aid: Long, cid: Long, title: String) {
         if (aid <= 0L || cid <= 0L) return

         viewModelScope.launch {
             val quality = _uiState.value.currentQuality
             val playUrlResult = bilibiliApi.api(IVideoApi::class) {
                 getPlayUrl(aid = aid, cid = cid, qn = quality)
             }.apiResultNonNull()

             val url = playUrlResult.getOrNull()?.durl?.firstOrNull()?.url.orEmpty()
             if (url.isBlank()) return@launch

             val safeTitle = sanitizeFileName(title)
             val fileName = if (safeTitle.isBlank()) {
                 "video_${aid}_$cid.mp4"
             } else {
                 "$safeTitle-$cid.mp4"
             }

             downloadManager.enqueue(
                DownloadRequest(
                    key = "video_${aid}_${cid}_$quality",
                    url = url,
                    headersJson = "{\"Referer\":\"$BILIBILI_REFERER\",\"User-Agent\":\"$BILIBILI_USER_AGENT\"}",
                    coverUrl = uiState.value.coverUrl.takeIf { it.isNotBlank() },
                    fileName = fileName,
                    mimeType = "video/mp4"
                )
            )
         }
     }
    
    fun playVideo(videoUrl: String) {
        if (videoUrl.isEmpty()) return

        val tag = currentPlayTag.incrementAndGet()
        preparationCancelled.set(false)

        viewModelScope.launch {
            try {
                Log.d("PlayerViewModel", "Playing video: $videoUrl (tag=$tag)")

                if (tag != currentPlayTag.get()) return@launch

                // 停止当前播放
                if (ijkPlayer.isPlaying) {
                    ijkPlayer.stop()
                }

                // 设置数据源和请求头
                val headers = mapOf(
                    "Referer" to BILIBILI_REFERER
                )
                Log.d("PlayerViewModel", "Setting headers: $headers")
                ijkPlayer.setDataSource(videoUrl, headers)
                ijkPlayer.prepareAsync()

                Log.d("PlayerViewModel", "Video prepared for playback")
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Error playing video", e)
            }
        }
    }

    fun playLocalFile(path: String, title: String) {
        if (path.isEmpty()) return

        val tag = currentPlayTag.incrementAndGet()
        preparationCancelled.set(false)

        viewModelScope.launch {
            try {
                Log.d("PlayerViewModel", "Playing local file: $path (tag=$tag)")

                if (tag != currentPlayTag.get()) return@launch

                _uiState.value = PlayerUiState(
                    isLocalMode = true,
                    title = title,
                    videoUrl = path,
                    isLoading = false
                )

                if (ijkPlayer.isPlaying) {
                    ijkPlayer.stop()
                }

                ijkPlayer.setDataSource(path)
                ijkPlayer.prepareAsync()

                Log.d("PlayerViewModel", "Local file prepared for playback")
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Error playing local file", e)
                _uiState.value = _uiState.value.copy(
                    error = "无法播放本地文件: ${e.message}"
                )
            }
        }
    }

    fun cancelPreparation() {
        preparationCancelled.set(true)
        runCatching { ijkPlayer.stop() }
        Log.d("PlayerViewModel", "Preparation cancelled by UI")
    }
    
    fun loadVideoInfo(aid: Long) {
        viewModelScope.launch {
            try {
                val videoInfoResult = bilibiliApi.api(IVideoApi::class) {
                    getVideoInfo(aid = aid)
                }.apiResultNonNull()
                
                val videoInfo = videoInfoResult.getOrNull()
                if (videoInfo != null) {
                    val pages = videoInfo.pages.mapIndexed { index, page ->
                        VideoPage(
                            cid = page.cid,
                            page = page.page,
                            part = page.part
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        pages = pages,
                        title = videoInfo.title,
                        coverUrl = videoInfo.pic
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun loadVideo(aid: Long, cid: Long) {
        currentPlayTag.incrementAndGet()
        preparationCancelled.set(false)

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val defaultQuality = LocalData.settings.playerSettings?.defaultQuality ?: 64
                
                // 首先获取所有可用清晰度信息
                val qualityInfoResult = bilibiliApi.api(IVideoApi::class) {
                    getPlayUrl(aid = aid, cid = cid, qn = defaultQuality, fnval = 4048)
                }.apiResultNonNull()
                
                val qualityInfoData = qualityInfoResult.getOrNull()
                
                if (qualityInfoData != null) {
                    // 解析可用清晰度列表
                    val availableQualities = mutableListOf<VideoQuality>()
                    val acceptQuality = qualityInfoData.acceptQuality ?: emptyList()
                    val acceptDescription = qualityInfoData.acceptDescription ?: emptyList()
                    
                    for (i in acceptQuality.indices) {
                        if (i < acceptDescription.size) {
                            availableQualities.add(
                                VideoQuality(
                                    qn = acceptQuality[i],
                                    description = acceptDescription[i]
                                )
                            )
                        }
                    }
                    
                    // 如果没有获取到清晰度列表，使用默认列表
                    if (availableQualities.isEmpty()) {
                        availableQualities.addAll(getDefaultQualities())
                    }
                    
                    // 获取实际视频URL
                    val playUrlResult = bilibiliApi.api(IVideoApi::class) {
                        getPlayUrl(aid = aid, cid = cid, qn = defaultQuality)
                    }.apiResultNonNull()
                    
                    val playUrlData = playUrlResult.getOrNull()
                    
                    if (playUrlData != null) {
                        val videoUrl = playUrlData.durl?.firstOrNull()?.url ?: ""
                        val videoDurationMs = computeDurationMs(playUrlData)

                        val currentPageIndex = _uiState.value.pages.indexOfFirst { it.cid == cid }

                        val playerInfo = videoRepository.getPlayerInfo(aid, cid).getOrNull()
                        val historyProgress = if (playerInfo?.lastPlayCid == cid) {
                            playerInfo.lastPlayTime
                        } else {
                            0L
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            videoUrl = videoUrl,
                            videoDurationMs = videoDurationMs,
                            aid = aid,
                            cid = cid,
                            currentPage = currentPageIndex,
                            historyProgress = historyProgress,
                            availableQualities = availableQualities,
                            currentQuality = defaultQuality
                        )
                        
                        // 播放视频
                        playVideo(videoUrl)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to load video"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to get quality info"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun getDefaultQualities(): List<VideoQuality> {
        return listOf(
            VideoQuality(16, "360P 流畅"),
            VideoQuality(32, "480P 清晰"),
            VideoQuality(64, "720P 高清"),
            VideoQuality(74, "720P60 高帧率"),
            VideoQuality(80, "1080P 高清"),
            VideoQuality(112, "1080P+ 高码率"),
            VideoQuality(116, "1080P60 高帧率"),
            VideoQuality(120, "4K 超清")
        )
    }
    
    suspend fun createDanmakuParser(
        cid: Long,
        aid: Long,
        totalDurationMs: Long
    ): BaseDanmakuParser? {
        _uiState.value = _uiState.value.copy(isLoadingDanmaku = true, danmakuError = null)
        val source = LocalData.settings.playerSettings?.danmakuSource
            ?: DanmakuSource.DANMAKU_SOURCE_PROTOBUF
        return withContext(Dispatchers.IO) {
            try {
                if (source == DanmakuSource.DANMAKU_SOURCE_PROTOBUF) {
                    val protoParser = loadProtoDanmaku(cid, aid, totalDurationMs)
                    if (protoParser != null) {
                        Log.d("Danmaku", "Protobuf danmaku loaded for cid=$cid")
                        protoParser
                    } else {
                        Log.w("Danmaku", "Protobuf danmaku empty, fallback to XML for cid=$cid")
                        loadXmlDanmaku(cid)
                    }
                } else {
                    Log.d("Danmaku", "XML danmaku mode for cid=$cid")
                    loadXmlDanmaku(cid)
                }
            } catch (e: Exception) {
                val errorMsg = "Error loading danmaku: ${e.message}"
                Log.e("Danmaku", errorMsg, e)
                _uiState.value = _uiState.value.copy(danmakuError = errorMsg)
                null
            } finally {
                _uiState.value = _uiState.value.copy(isLoadingDanmaku = false)
            }
        }
    }

    private suspend fun loadProtoDanmaku(
        cid: Long,
        aid: Long,
        totalDurationMs: Long
    ): BilibiliProtoDanmakuParser? {
        val segments = max(1, ceil(totalDurationMs / 360_000.0).toInt())
        val maxSegment = min(segments, 2000) // 防御性上限：200h 视频
        Log.d("Danmaku", "Loading protobuf segments: $maxSegment for cid=$cid")

        val allElems = mutableListOf<bilibili.community.service.dm.v1.Dm.DanmakuElem>()
        var anySuccess = false
        coroutineScope {
            val concurrency = 4
            val semaphore = kotlinx.coroutines.sync.Semaphore(concurrency)
            val jobs = (1..maxSegment).map { idx ->
                async(Dispatchers.IO) {
                    semaphore.acquire()
                    try {
                        val elems = danmakuRepository.getDanmakuSegment(
                            oid = cid,
                            pid = aid,
                            segmentIndex = idx
                        ).getOrNull() ?: return@async
                        if (elems.isEmpty()) return@async
                        synchronized(allElems) {
                            allElems.addAll(elems)
                        }
                        anySuccess = true
                    } catch (e: Exception) {
                        Log.w("Danmaku", "segment $idx failed: ${e.message}")
                    } finally {
                        semaphore.release()
                    }
                }
            }
            jobs.awaitAll()
        }

        if (!anySuccess || allElems.isEmpty()) {
            return null
        }
        val parser = BilibiliProtoDanmakuParser()
        parser.load(DanmakuListDataSource(allElems))
        return parser
    }

    private suspend fun loadXmlDanmaku(cid: Long): BaseDanmakuParser? {
        val danmakuUrl = "https://comment.bilibili.com/$cid.xml"
        val parser = BiliDanmukuParser()
        val loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI)
            ?: run {
                Log.e("Danmaku", "Failed to create danmaku loader")
                return null
            }
        // B 站弹幕数据是 deflate 压缩的（不带 zlib header），需要先解压
        val rawInputStream = URL(danmakuUrl).openStream()
        val inflater = java.util.zip.Inflater(true)
        val inflaterInputStream = java.util.zip.InflaterInputStream(rawInputStream, inflater)
        loader.load(inflaterInputStream)
        val dataSource = loader.dataSource
            ?: run {
                Log.e("Danmaku", "Failed to get danmaku data source")
                return null
            }
        parser.load(dataSource)
        return parser
    }

    private fun computeDurationMs(playUrlData: PlayUrlData): Long {
        val total = playUrlData.durl?.sumOf { it.length } ?: 0L
        return if (total > 0) total else 0L
    }
    
    fun toggleDanmaku() {
        _uiState.value = _uiState.value.copy(
            isDanmakuVisible = !_uiState.value.isDanmakuVisible
        )
    }
    
    fun setDanmakuVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(
            isDanmakuVisible = visible
        )
    }
    
    fun changeQuality(quality: Int) {
        if (_uiState.value.isLocalMode) return
        val currentState = _uiState.value
        if (currentState.aid > 0 && currentState.cid > 0) {
            _uiState.value = currentState.copy(currentQuality = quality)
            // 重新加载视频，使用新的清晰度
            loadVideoWithQuality(currentState.aid, currentState.cid, quality)
        }
    }
    
    private fun loadVideoWithQuality(aid: Long, cid: Long, quality: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val playUrlResult = bilibiliApi.api(IVideoApi::class) {
                    getPlayUrl(aid = aid, cid = cid, qn = quality)
                }.apiResultNonNull()
                
                val playUrlData = playUrlResult.getOrNull()
                
                if (playUrlData != null) {
                    val videoUrl = playUrlData.durl?.firstOrNull()?.url ?: ""
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        videoUrl = videoUrl,
                        currentQuality = quality
                    )
                    
                    Log.d("PlayerViewModel", "Quality changed to $quality, new URL: $videoUrl")
                } else {
                    // 如果当前清晰度不可用，尝试降级
                    val fallbackQualities = listOf(64, 32, 16) // 720P -> 480P -> 360P
                    val nextQuality = fallbackQualities.find { it < quality }
                    
                    if (nextQuality != null) {
                        Log.w("PlayerViewModel", "Quality $quality not available, trying $nextQuality")
                        loadVideoWithQuality(aid, cid, nextQuality)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "No available quality found"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Failed to change quality", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to change quality: ${e.message}"
                )
            }
        }
    }
    
    fun startProgressReporting(getCurrentPosition: () -> Long) {
        progressReportJob?.cancel()
        progressReportJob = viewModelScope.launch {
            while (isActive) {
                delay(15000)
                val currentPosition = getCurrentPosition()
                if (currentPosition > 0) {
                    val progressInSeconds = currentPosition / 1000
                    if (progressInSeconds != lastReportedProgress) {
                        lastReportedProgress = progressInSeconds
                        reportProgress(progressInSeconds)
                    }
                }
            }
        }
    }
    
    fun stopProgressReporting() {
        progressReportJob?.cancel()
        progressReportJob = null
    }
    
    fun reportFinalProgress(currentPosition: Long) {
        if (_uiState.value.isLocalMode) return
        val aid = _uiState.value.aid
        val cid = _uiState.value.cid
        if (aid != 0L && cid != 0L && currentPosition > 0) {
            viewModelScope.launch {
                val progressInSeconds = currentPosition / 1000
                reportProgress(progressInSeconds)
            }
        }
    }
    
    private suspend fun reportProgress(progressInSeconds: Long) {
        if (_uiState.value.isLocalMode) return
        val aid = _uiState.value.aid
        val cid = _uiState.value.cid
        if (aid != 0L && cid != 0L) {
            try {
                videoRepository.reportHistory(aid, cid, progressInSeconds)
                Log.d("PlayerViewModel", "Progress reported: aid=$aid, cid=$cid, progress=$progressInSeconds")
            } catch (e: Exception) {
                Log.e("PlayerViewModel", "Failed to report progress", e)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        currentPlayTag.incrementAndGet()
        preparationCancelled.set(true)
        stopProgressReporting()
        ijkPlayer.release()
        Log.d("PlayerViewModel", "IjkMediaPlayer released")
    }
}

 private fun sanitizeFileName(value: String): String {
     return value
         .replace("\\\\", "_")
         .replace("/", "_")
         .replace(":", "_")
         .replace("*", "_")
         .replace("?", "_")
         .replace("\"", "_")
         .replace("<", "_")
         .replace(">", "_")
         .replace("|", "_")
         .trim()
         .take(80)
 }

data class PlayerUiState(
    val isLoading: Boolean = false,
    val isLoadingDanmaku: Boolean = false,
    val danmakuError: String? = null,
    val error: String? = null,
    val videoUrl: String = "",
    val title: String = "",
    val coverUrl: String = "",
    val aid: Long = 0,
    val cid: Long = 0,
    val pages: List<VideoPage> = emptyList(),
    val currentPage: Int = 0,
    val isDanmakuVisible: Boolean = true,
    val historyProgress: Long = 0,
    val availableQualities: List<VideoQuality> = emptyList(),
    val currentQuality: Int = 64,
    val videoAspectRatio: Float = 16f / 9f,
    val videoDurationMs: Long = 0L,
    val isLocalMode: Boolean = false
)

data class VideoPage(
    val cid: Long,
    val page: Int,
    val part: String
)

data class VideoQuality(
    val qn: Int,
    val description: String
)
