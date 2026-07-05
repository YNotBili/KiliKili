package rj.kilikili.ui.screens.video

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import rj.kilikili.R
import rj.kilikili.ui.dialog.AdaptDialog
import rj.kilikili.ui.dialog.CoinDialog
import rj.kilikili.ui.dialog.DownloadDialog
import rj.kilikili.ui.dialog.FavoriteDialog
import rj.kilikili.ui.dialog.ReportDialog
import rj.kilikili.ui.dialog.VideoPage
import rj.kilikili.ui.theme.BiliPink
import rj.kilikili.ui.objects.Loading
import rj.kilikili.ui.objects.LoadingState
import rj.kilikili.utils.MsgUtil
import rj.kilikili.utils.extensions.formatNumber
import rj.kilikili.utils.extensions.formatToDate
import rj.kilikili.utils.extensions.toHttpsUrl
import rj.kilikili.utils.extensions.toTime
import com.huanli233.biliwebapi.bean.user.UserInfo
import androidx.core.graphics.toColorInt
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.isRoundDevice
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.foundation.rotary.RotaryScrollableDefaults
import androidx.wear.compose.foundation.rotary.rotaryScrollable
import rj.kilikili.ui.components.auto.appVerticalOptContentPadding
import rj.kilikili.ui.components.auto.AppScreenScaffold
import androidx.wear.compose.material3.TimeText
import rj.kilikili.data.setting.LocalData
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.screens.comment.CommentScreen
import rj.kilikili.ui.objects.WearPager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoDetailScreen(
    navController: NavController,
    viewModel: VideoDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var showCoinDialog by remember { mutableStateOf(false) }
    var showFavoriteDialog by remember { mutableStateOf(false) }
    var showDownloadDialog by remember { mutableStateOf(false) }
    var showVideoMoreDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showDmReportDialog by remember { mutableStateOf(false) }
    val reportVm: rj.kilikili.ui.viewmodel.ReportViewModel = hiltViewModel()

    var pendingDownloadPages by remember { mutableStateOf<List<VideoPage>?>(null) }

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val pages = pendingDownloadPages
        pendingDownloadPages = null
        if (granted && pages != null) {
            viewModel.enqueueDownloads(pages)
        } else if (!granted) {
            MsgUtil.showMsg(context.getString(R.string.msg_storage_permission_denied))
        }
    }

    fun canWriteToPublicDownloads(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(uiState.videoInfo) {
        val videoInfo = uiState.videoInfo
        val redirectUrl = videoInfo?.redirectUrl
        if (videoInfo != null && redirectUrl != null && redirectUrl.contains("bangumi")) {
            val epid = redirectUrl.replace("https://www.bilibili.com/bangumi/play/ep", "").toLongOrNull()
            if (epid != null) {
                navController.navigate("bangumi_from_ep/$epid") {
                    popUpTo("video_detail/${videoInfo.aid}/${videoInfo.bvid}") { inclusive = true }
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is VideoDetailEvent.LikeSuccess -> {
                    val message = if (event.action == 1) 
                        context.getString(R.string.msg_like_success)
                    else 
                        context.getString(R.string.msg_cancel_success)
                    MsgUtil.showMsg(message)
                }
                is VideoDetailEvent.LikeFailed -> {
                    MsgUtil.showMsg(event.message ?: context.getString(R.string.msg_operation_failed))
                }
                is VideoDetailEvent.NotLoggedIn -> {
                    MsgUtil.showMsg(context.getString(R.string.msg_not_logged_in))
                }
                is VideoDetailEvent.CoinSuccess -> {
                    MsgUtil.showMsg(context.getString(R.string.msg_coin_success))
                }
                is VideoDetailEvent.FavoriteSuccess -> {
                    MsgUtil.showMsg(context.getString(R.string.msg_favorite_success))
                }
                is VideoDetailEvent.WatchLaterSuccess -> {
                    MsgUtil.showMsg(context.getString(R.string.msg_watch_later_success))
                }
                is VideoDetailEvent.OperationFailed -> {
                    MsgUtil.showMsg(event.message ?: context.getString(R.string.msg_operation_failed))
                }
                is VideoDetailEvent.DownloadEnqueued -> {
                    MsgUtil.showMsg(context.getString(R.string.msg_download_enqueued, event.count))
                }
            }
        }
    }

    val isRound = isRoundDevice() && LocalData.settings.uiSettings.roundMode
    val pagerState = androidx.wear.compose.foundation.pager.rememberPagerState(pageCount = { 3 })
    
    // Create scroll states for each page
    val videoDetailScrollState = rememberScrollState()
    val commentScrollState = rememberAppLazyListState()
    val relatedScrollState = rememberAppLazyListState()
    
    AppScreenScaffold(
        scrollState = commentScrollState,
        modifier = Modifier.fillMaxSize(),
        topBar = appTopBar(
            title = stringResource(R.string.video_detail),
            onBackClick = { navController.popBackStack() }
        ),
        timeText = if (isRound) { { TimeText() } } else null
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        Loading(
                            state = LoadingState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                    uiState.error != null -> {
                        Loading(
                            state = LoadingState.Error,
                            errorMessage = uiState.error,
                            onRetry = { viewModel.fetchData() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                    uiState.videoInfo != null -> {
                        WearPager(
                            pageCount = 3,
                            pagerState = pagerState,
                            modifier = Modifier.weight(1f)
                        ) { page ->
                            when (page) {
                                0 -> VideoDetailContent(
                                    uiState = uiState,
                                    scrollState = videoDetailScrollState,
                                    padding = paddingValues,
                                    onLikeClick = { viewModel.like() },
                                    onCoinClick = { showCoinDialog = true },
                                    onFavoriteClick = {
                                        viewModel.loadFavoriteFolders()
                                        showFavoriteDialog = true
                                    },
                                    onWatchLaterClick = { viewModel.addToWatchLater() },
                                    onDownloadClick = { showDownloadDialog = true },
                                    onShareClick = { },
                                    onMoreClick = { showVideoMoreDialog = true },
                                    onPlayClick = {
                                        uiState.videoInfo?.let { video ->
                                            navController.navigate("player/${video.aid}/${video.cid}")
                                        }
                                    },
                                    onCoverClick = {
                                        uiState.videoInfo?.let { video ->
                                            val encodedUrl = java.net.URLEncoder.encode(video.pic, "UTF-8")
                                            navController.navigate("image/$encodedUrl/0")
                                        }
                                    },
                                    onUploaderClick = { mid ->
                                        navController.navigate("user/$mid")
                                    },
                                    onCollectionClick = { seasonId ->
                                        val videoInfo = uiState.videoInfo
                                        if (videoInfo != null && videoInfo.ugcSeason != null) {
                                            val season = videoInfo.ugcSeason
                                            val encodedName = java.net.URLEncoder.encode(season?.title.toString(), "UTF-8")
                                            navController.navigate("collection/${videoInfo.owner.mid}/$seasonId/$encodedName")
                                        }
                                    },
                                    onTagClick = { }
                                )
                                1 -> CommentScreen(
                                    aid = uiState.videoInfo?.aid ?: 0L,
                                    scrollState = commentScrollState,
                                    paddingValues = paddingValues,
                                    onCommentDetailClick = { replyId ->
                                        val oid = uiState.videoInfo?.aid ?: 0L
                                        navController.navigate("comment_detail/$replyId?oid=$oid&type=1")
                                    },
                                    onWriteReplyClick = { oid, rpid, parent, parentSender ->
                                        navController.navigate("write_reply/$oid/$rpid/$parent?parentSender=${parentSender ?: ""}")
                                    },
                                    onUserClick = { userId ->
                                        navController.navigate("user/$userId")
                                    },
                                    onOpusClick = { opusId ->
                                        navController.navigate("opus_detail/$opusId")
                                    }
                                )
                                2 -> VideoRelatedScreen(
                                    aid = uiState.videoInfo?.aid ?: 0L,
                                    bvid = uiState.videoInfo?.bvid ?: "",
                                    scrollState = relatedScrollState,
                                    onVideoClick = { video ->
                                        navController.navigate("video_detail/${video.aid}/${video.bvid}")
                                    },
                                    paddingValues = paddingValues
                                )
                            }
                        }
                    }
                }
            }

            // TopBar is now handled by ScreenScaffold automatically
        }
    }
    
    // 投币对话框
    if (showCoinDialog) {
        CoinDialog(
            onDismiss = { showCoinDialog = false },
            onConfirm = { count, alsoLike ->
                viewModel.coin(count, alsoLike)
                showCoinDialog = false
            },
            maxCoins = 2
        )
    }
    
    if (showFavoriteDialog) {
        FavoriteDialog(
            folders = uiState.favoriteFolders,
            onDismiss = { showFavoriteDialog = false },
            onConfirm = { selectedFids, deselectedFids ->
                viewModel.updateFavorites(selectedFids, deselectedFids)
                showFavoriteDialog = false
            },
            isLoading = uiState.isLoadingFolders
        )
    }
    
    if (showDownloadDialog && uiState.videoInfo != null) {
        val pages = uiState.videoInfo!!.pages.map { page ->
            VideoPage(
                cid = page.cid,
                page = page.page,
                part = page.part,
                duration = page.duration.toLong()
            )
        }
        DownloadDialog(
            pages = pages,
            onDismiss = { showDownloadDialog = false },
            onConfirm = { selectedPages ->
                if (canWriteToPublicDownloads()) {
                    viewModel.enqueueDownloads(selectedPages)
                } else {
                    pendingDownloadPages = selectedPages
                    storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                showDownloadDialog = false
            }
        )
    }

    if (showVideoMoreDialog) {
        val aid = uiState.videoInfo?.aid ?: 0L
        AdaptDialog(
            onDismissRequest = { showVideoMoreDialog = false },
            title = { Text(stringResource(R.string.video_more)) },
            text = {
                Column {
                    listOf(
                        R.string.video_note to "note_list/$aid",
                        R.string.vote to "vote/0",
                        R.string.danmaku_filter to "dm_filter",
                        R.string.video_report to "report_video",
                        R.string.reserve to "reserve/0/${uiState.videoInfo?.owner?.mid ?: 0L}"
                    ).forEach { (labelRes, _) ->
                        TextButton(
                            onClick = {
                                showVideoMoreDialog = false
                                when (labelRes) {
                                    R.string.video_note -> navController.navigate("note_list/$aid")
                                    R.string.vote -> navController.navigate("vote/0")
                                    R.string.danmaku_filter -> navController.navigate("dm_filter")
                                    R.string.video_report -> showReportDialog = true
                                    R.string.reserve -> navController.navigate("reserve/0/${uiState.videoInfo?.owner?.mid ?: 0L}")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(labelRes), modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showVideoMoreDialog = false }) { Text(stringResource(R.string.cancel)) } }
        )
    }

    if (showReportDialog && uiState.videoInfo != null) {
        ReportDialog(
            visible = true,
            title = stringResource(R.string.video_report),
            onDismiss = { showReportDialog = false },
            onConfirm = { reason, content ->
                reportVm.reportReply(uiState.videoInfo!!.aid, 0L, 1, "$reason $content")
                showReportDialog = false
            }
        )
        androidx.compose.runtime.LaunchedEffect(Unit) {
            reportVm.events.collect {
                when (it) {
                    is rj.kilikili.ui.viewmodel.ReportViewModel.Event.Success -> MsgUtil.showMsg("已举报")
                    is rj.kilikili.ui.viewmodel.ReportViewModel.Event.Failed -> MsgUtil.showMsg(it.msg)
                }
            }
        }
    }
}


@Composable
private fun CommentPlaceholder() {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = context.getString(R.string.placeholder_comments),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VideoDetailContent(
    uiState: VideoDetailUiState,
    scrollState: ScrollState,
    padding: PaddingValues,
    onLikeClick: () -> Unit,
    onCoinClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onWatchLaterClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onShareClick: () -> Unit,
    onMoreClick: () -> Unit,
    onPlayClick: () -> Unit,
    onCoverClick: () -> Unit,
    onUploaderClick: (Long) -> Unit,
    onCollectionClick: (Long) -> Unit,
    onTagClick: (String) -> Unit
) {
    val videoInfo = uiState.videoInfo ?: return
    val focusRequester = rememberActiveFocusRequester()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .rotaryScrollable(
                behavior = RotaryScrollableDefaults.behavior(scrollState),
                focusRequester = focusRequester
            )
            .verticalScroll(scrollState)
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp)
            .padding(padding)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onCoverClick),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box {
                AsyncImage(
                    model = videoInfo.pic.toHttpsUrl(),
                    contentDescription = videoInfo.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f),
                    contentScale = ContentScale.Crop
                )

                // 时长标签
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = toTime(videoInfo.duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Wear优化：紧凑的标题显示
        Column(modifier = Modifier.fillMaxWidth()) {
            // 特殊标签（如果有）
            val badgeText = when {
                videoInfo.isUpowerExclusive -> "充电专属"
                videoInfo.rights.isSteinGate == 1 -> "互动"
                videoInfo.rights.is360 == 1 -> "全景"
                !videoInfo.staff.isNullOrEmpty() -> "联合"
                else -> null
            }

            if (badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = videoInfo.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Argue Info (争议信息)
        if (videoInfo.argueInfo.argueMsg.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = videoInfo.argueInfo.argueMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        val uploaders = remember(videoInfo) {
            if (videoInfo.staff.isNullOrEmpty()) {
                listOf(videoInfo.owner)
            } else {
                videoInfo.staff.orEmpty()
            }
        }
        
        if (uploaders.isNotEmpty()) {
            ExpandableSection(
                title = if (uploaders.size == 1) {
                    stringResource(R.string.video_uploader)
                } else {
                    stringResource(R.string.video_uploaders, uploaders.size)
                },
                initiallyExpanded = uploaders.size == 1
            ) {
                UploaderList(
                    uploaders = uploaders,
                    onNavigateToProfile = onUploaderClick
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Collection Info (合集信息)
        if (videoInfo.ugcSeason != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { videoInfo.ugcSeason?.id?.let { onCollectionClick(it.toLong()) } },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "合集 · ${videoInfo.ugcSeason?.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        painter = painterResource(R.drawable.icon_arrow_forward),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Wear优化：紧凑的统计信息
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_play_16),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = videoInfo.stat.view.formatNumber("万", "亿"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_danmaku),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = videoInfo.stat.danmaku.formatNumber("万", "亿"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = videoInfo.ctime.formatToDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExpandableSection(
            title = "简介",
            initiallyExpanded = true
        ) {
            Text(
                text = videoInfo.desc ?: "暂无简介",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.tags.isNotEmpty()) {
            ExpandableSection(
                title = "标签",
                initiallyExpanded = false
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.tags.forEach { tag ->
                        AssistChip(
                            onClick = { onTagClick(tag.tagName) },
                            label = { Text(tag.tagName) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Wear优化：紧凑的播放按钮
        Button(
            onClick = onPlayClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.action_play),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Wear优化：紧凑的操作按钮行
        val isLiked = uiState.relation?.like == true
        val hasCoined = uiState.relation?.coin?.let { it > 0 } == true
        val isFavorited = uiState.relation?.favorite == true

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                icon = Icons.Outlined.ThumbUp,
                text = videoInfo.stat.like.formatNumber("万", "亿"),
                isActive = isLiked,
                enabled = uiState.relation != null && !uiState.isLiking,
                onClick = onLikeClick,
                modifier = Modifier.weight(1f)
            )

            ActionButton(
                icon = painterResource(R.drawable.icon_coin),
                text = videoInfo.stat.coin.formatNumber("万", "亿"),
                isActive = hasCoined,
                enabled = uiState.relation != null,
                onClick = onCoinClick,
                modifier = Modifier.weight(1f)
            )

            ActionButton(
                icon = Icons.Outlined.Star,
                text = videoInfo.stat.favorite.formatNumber("万", "亿"),
                isActive = isFavorited,
                enabled = uiState.relation != null,
                onClick = onFavoriteClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilledTonalButton(
            onClick = onWatchLaterClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.action_watch_later))
        }

        FilledTonalButton(
            onClick = onDownloadClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.action_download))
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilledTonalButton(
            onClick = onShareClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(R.string.action_share))
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilledTonalButton(
            onClick = onMoreClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.MoreVert, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.video_more))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.painter.Painter,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun InfoItem(
    icon: Any,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (icon) {
            is ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            is androidx.compose.ui.graphics.painter.Painter -> {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ExpandableSection(
    title: String,
    initiallyExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { isExpanded = !isExpanded })
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.rotate(rotationAngle),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActionButton(
    icon: Any,
    text: String,
    isActive: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isActive) {
        BiliPink.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isActive) {
        BiliPink
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(12.dp)),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (icon) {
                is ImageVector -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
                is androidx.compose.ui.graphics.painter.Painter -> {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
private fun UploaderList(
    uploaders: List<UserInfo>,
    onNavigateToProfile: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        uploaders.forEach { uploader ->
            UploaderItem(
                uploader = uploader,
                onNavigateToProfile = onNavigateToProfile
            )
        }
    }
}

@Composable
private fun UploaderItem(
    uploader: UserInfo,
    onNavigateToProfile: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onNavigateToProfile(uploader.mid) },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = uploader.face,
                contentDescription = uploader.name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                val nameColor = if (uploader.vip?.nicknameColor?.isNotEmpty() == true) {
                    try {
                        Color(uploader.vip?.nicknameColor!!.toColorInt())
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Text(
                    text = uploader.name.orEmpty(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = nameColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (uploader.sign?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = uploader.sign.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
