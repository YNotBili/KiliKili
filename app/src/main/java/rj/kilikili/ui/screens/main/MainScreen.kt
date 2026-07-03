package rj.kilikili.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs
import kotlinx.coroutines.launch
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
import rj.kilikili.UiType
import rj.kilikili.actualUiType
import rj.kilikili.uiType
import rj.kilikili.ui.components.phone.PhoneBottomNavBar
import rj.kilikili.ui.navigation.AppNavHostRoute
import rj.kilikili.ui.navigation.appComposable
import rj.kilikili.ui.navigation.rememberAppNavController
import rj.kilikili.R
import rj.kilikili.data.account.AccountManager
import rj.kilikili.data.menu.MenuConfigManager
import rj.kilikili.ui.components.auto.AppMenuPanel
import rj.kilikili.ui.navigation.NavGraph
import rj.kilikili.ui.navigation.Screen
import rj.kilikili.ui.navigation.loginGraph
import rj.kilikili.ui.navigation.settingsGraph
import rj.kilikili.ui.screens.collection.CollectionDetailScreen
import rj.kilikili.ui.screens.comment.CommentDetailScreen
import rj.kilikili.ui.screens.download.DownloadListScreen
import rj.kilikili.ui.screens.dynamic.DynamicDetailScreen
import rj.kilikili.ui.screens.dynamic.DynamicHomeScreen
import rj.kilikili.ui.screens.favorite.FavoriteScreen
import rj.kilikili.ui.screens.favorite.FavoriteVideosScreen
import rj.kilikili.ui.screens.favorite.OpusFavoriteScreen
import rj.kilikili.ui.screens.follow.FollowingScreen
import rj.kilikili.ui.screens.follow.SameFollowingScreen
import rj.kilikili.ui.screens.follow.SearchFollowingScreen
import rj.kilikili.ui.screens.history.HistoryScreen
import rj.kilikili.ui.screens.article.ArticleScreen
import rj.kilikili.ui.screens.bangumi.PgcIndexScreen
import rj.kilikili.ui.screens.bangumi.PgcRankScreen
import rj.kilikili.ui.screens.bangumi.PgcReviewScreen
import rj.kilikili.ui.screens.topic.TopicScreen
import rj.kilikili.ui.screens.video.DmFilterScreen
import rj.kilikili.ui.screens.video.NoteListScreen
import rj.kilikili.ui.screens.video.ReserveScreen
import rj.kilikili.ui.screens.video.VoteScreen
import rj.kilikili.ui.screens.history.SearchHistoryScreen
import rj.kilikili.ui.screens.image.ImageViewerScreen
import rj.kilikili.ui.screens.watchlater.WatchLaterScreen
import rj.kilikili.ui.screens.opus.OpusDetailScreen
import rj.kilikili.ui.screens.player.PlayerScreen
import rj.kilikili.ui.screens.recommend.RecommendScreen
import rj.kilikili.ui.screens.popular.PopularScreen
import rj.kilikili.ui.screens.precious.PreciousScreen
import rj.kilikili.ui.screens.bangumi.BangumiDetailScreen
import rj.kilikili.ui.screens.series.SeriesDetailScreen
import rj.kilikili.ui.screens.user.MySpaceScreen
import rj.kilikili.ui.screens.user.UserProfileScreen
import rj.kilikili.ui.screens.video.VideoDetailScreen
import rj.kilikili.ui.screens.comment.WriteReplyScreen
import rj.kilikili.ui.screens.search.SearchScreen
import rj.kilikili.ui.screens.search.SearchResultScreen
import rj.kilikili.ui.screens.ranking.RankingScreen
import rj.kilikili.ui.screens.timeline.TimelineScreen
import rj.kilikili.ui.screens.message.MessageCenterScreen
import rj.kilikili.ui.screens.message.PrivateMsgScreen
import rj.kilikili.ui.screens.vip.VipScreen
import rj.kilikili.ui.screens.member.CoinLogScreen
import rj.kilikili.ui.screens.member.ExpLogScreen
import rj.kilikili.ui.screens.member.LoginRecordScreen
import rj.kilikili.ui.screens.message.LikeMessagesScreen
import rj.kilikili.ui.screens.message.ReplyMessagesScreen
import rj.kilikili.ui.screens.message.SystemMessagesScreen
import rj.kilikili.ui.screens.message.ConversationScreen
import rj.kilikili.ui.screens.message.DanmakuSendScreen
import rj.kilikili.ui.screens.dynamic.SendDynamicScreen
import rj.kilikili.ui.screens.live.LiveMedalWallScreen
import rj.kilikili.ui.screens.live.FollowedLiveScreen
import rj.kilikili.ui.screens.live.SuperChatScreen
import rj.kilikili.ui.screens.live.DanmakuHistoryScreen
import rj.kilikili.ui.screens.user.RecentCoinVideosScreen
import rj.kilikili.ui.screens.user.RecentLikeVideosScreen
import rj.kilikili.ui.screens.follow.FollowTagScreen
import rj.kilikili.ui.screens.popular.PopularSeriesDetailScreen
import rj.kilikili.ui.screens.popular.PopularSeriesScreen
import rj.kilikili.ui.screens.search.HotSearchScreen
import rj.kilikili.ui.screens.follow.FansScreen
import android.net.Uri
import java.net.URLDecoder

@Composable
fun MainScreen(mainNavController: androidx.navigation.NavController) {
    // 顶层 key(actualUiType) — 切换 uiType 时强制重建, 避免 wear/phone 状态污染。
    key(actualUiType) {
    val contentNavController = rememberAppNavController()
    val menuConfig by remember { mutableStateOf(MenuConfigManager.readMenuConfig()) }
    var isMenuExpanded by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 当前路由 — 用于高亮底部导航项
    val currentBackStackEntry by contentNavController.currentBackStackEntryFlow
        .collectAsState(initial = contentNavController.currentBackStackEntry)
    val currentRoute = currentBackStackEntry?.destination?.route

    val openMenu: () -> Unit = {
        when (uiType) {
            UiType.WEAR, UiType.FRESHWEAR -> isMenuExpanded = true
            UiType.PHONE -> scope.launch { drawerState.open() }
        }
    }

    // 一级页面跳转 — 清空整个返回栈只留新页面, 使每个一级页都是独立根 (按返回直接退出 app)。
    // 不能用 popUpTo(startDestinationRoute): 一旦该 destination 已被弹出, popBackStackInternal
    // 会静默 return false 不弹 (androidx.navigation 2.9.3 NavControllerImpl.kt:462), 导致
    // "一级页 → 另一一级页"时旧页面残留。手动循环 popBackStack 是唯一可靠方式。
    val navigateTopLevel: (String) -> Unit = { route ->
        contentNavController.navigate(route) {
            popUpTo(contentNavController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val menuGestureBlocker = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return if (abs(available.x) > abs(available.y)) {
                    Offset(available.x, 0f)
                } else {
                    Offset.Zero
                }
            }
            
            override suspend fun onPreFling(available: Velocity): Velocity {
                return if (abs(available.x) > abs(available.y)) {
                    Velocity(available.x, 0f)
                } else {
                    Velocity.Zero
                }
            }
        }
    }

    when (actualUiType) {
            UiType.WEAR, UiType.FRESHWEAR -> {
                MainNavHost(
                    contentNavController = contentNavController,
                    openMenu = openMenu,
                    menuConfig = menuConfig
                )
            }
            UiType.PHONE -> {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        AppMenuPanel(
                            menuItems = menuConfig.menuItems,
                            drawerState = drawerState,
                            scope = scope,
                            onSelect = { route ->
                                navigateTopLevel(route)
                            }
                        )
                    },
                    content = {
                        Scaffold(
                            bottomBar = {
                                PhoneBottomNavBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { route -> navigateTopLevel(route) }
                                )
                            }
                        ) { innerPadding ->
                            MainNavHost(
                                contentNavController = contentNavController,
                                openMenu = openMenu,
                                menuConfig = menuConfig,
                                contentPadding = innerPadding
                            )
                        }
                    }
                )
            }
        }

        if (actualUiType == UiType.WEAR || actualUiType == UiType.FRESHWEAR) {
            // 遮罩层 — 独立淡入淡出，不随菜单滑动
            AnimatedVisibility(
                visible = isMenuExpanded,
                enter = fadeIn(animationSpec = tween(durationMillis = 200)),
                exit = fadeOut(animationSpec = tween(durationMillis = 200)),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { isMenuExpanded = false }
                )
            }

            // 菜单面板 — 从上方滑入
            AnimatedVisibility(
                visible = isMenuExpanded,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (isMenuExpanded) {
                            Modifier
                                .systemGestureExclusion()
                                .nestedScroll(menuGestureBlocker)
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent(PointerEventPass.Initial)
                                            val change = event.changes.firstOrNull() ?: continue
                                            val dragX = change.position.x - change.previousPosition.x
                                            val dragY = change.position.y - change.previousPosition.y

                                            if (abs(dragX) > abs(dragY) && abs(dragX) > 0) {
                                                event.changes.forEach { it.consume() }
                                            }
                                        }
                                    }
                                }
                        } else {
                            Modifier
                        }
                    )
            ) {
                AppMenuPanel(
                    modifier = Modifier.fillMaxSize(),
                    menuItems = menuConfig.menuItems,
                    onSelect = { route ->
                        navigateTopLevel(route)
                        isMenuExpanded = false
                    },
                    onDismiss = { isMenuExpanded = false }
                )
            }
        }
    }
}

@Composable
private fun MainNavHost(
    contentNavController: androidx.navigation.NavHostController,
    openMenu: () -> Unit,
    menuConfig: rj.kilikili.data.menu.MenuConfig,
    contentPadding: PaddingValues = PaddingValues()
) {
    AppNavHostRoute(
        navController = contentNavController,
        startDestination = Screen.Recommend.route,
        modifier = Modifier.padding(contentPadding)
    ) { nc ->
        appComposable(Screen.Recommend.route) {
            RecommendScreen(
                onVideoClick = { videoInfo ->
                    contentNavController.navigate(
                        Screen.VideoDetail.createRoute(videoInfo.aid, videoInfo.bvid)
                    )
                },
                onMenuClick = openMenu,
                onPopularClick = { contentNavController.navigate("popular") },
                onPreciousClick = { contentNavController.navigate("precious") }
            )
        }

            appComposable("popular") {
                PopularScreen(
                    onVideoClick = { videoInfo ->
                        contentNavController.navigate(
                            Screen.VideoDetail.createRoute(videoInfo.aid, videoInfo.bvid)
                        )
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable("precious") {
                PreciousScreen(
                    onVideoClick = { videoInfo ->
                        contentNavController.navigate(
                            Screen.VideoDetail.createRoute(videoInfo.aid, videoInfo.bvid)
                        )
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = "bangumi/{mediaId}",
                arguments = listOf(
                    navArgument("mediaId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getLong("mediaId") ?: 0L
                BangumiDetailScreen(
                    mediaId = mediaId,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onPlayEpisode = { aid, cid ->
                        contentNavController.navigate("video_detail/$aid/")
                    }
                )
            }

            appComposable(
                route = "bangumi_from_ep/{epId}",
                arguments = listOf(
                    navArgument("epId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val epId = backStackEntry.arguments?.getLong("epId") ?: 0L
                val bangumiRepository = remember { rj.kilikili.data.repository.BangumiRepository() }
                var mediaId by remember { mutableStateOf<Long?>(null) }
                var error by remember { mutableStateOf<String?>(null) }
                
                LaunchedEffect(epId) {
                    bangumiRepository.getMediaIdFromEpId(epId).fold(
                        onSuccess = { id -> mediaId = id },
                        onFailure = { e -> error = e.message }
                    )
                }
                
                when {
                    mediaId != null -> {
                        BangumiDetailScreen(
                            mediaId = mediaId!!,
                            onNavigateBack = { contentNavController.popBackStack() },
                            onPlayEpisode = { aid, cid ->
                                contentNavController.navigate("video_detail/$aid/")
                            }
                        )
                    }
                    error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("加载失败: $error")
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            appComposable(
                route = "series/{type}/{mid}/{id}/{name}",
                arguments = listOf(
                    navArgument("type") { type = NavType.StringType },
                    navArgument("mid") { type = NavType.LongType },
                    navArgument("id") { type = NavType.LongType },
                    navArgument("name") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val seriesType = backStackEntry.arguments?.getString("type") ?: "series"
                val mid = backStackEntry.arguments?.getLong("mid") ?: 0L
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                val name = backStackEntry.arguments?.getString("name") ?: ""
                
                SeriesDetailScreen(
                    type = seriesType,
                    mid = mid,
                    id = id,
                    name = URLDecoder.decode(name, "UTF-8"),
                    onNavigateBack = { contentNavController.popBackStack() },
                    onVideoClick = { videoInfo ->
                        contentNavController.navigate(
                            Screen.VideoDetail.createRoute(videoInfo.aid, videoInfo.bvid)
                        )
                    }
                )
            }

            appComposable(Screen.Dynamic.route) {
                DynamicHomeScreen(
                    onDynamicClick = { dynamic ->
                        val major = dynamic.modules.contentModule.major
                        when {
                            major?.type == "MAJOR_TYPE_OPUS" && major.opus != null -> {
                                val opusId = dynamic.id
                                contentNavController.navigate(
                                    Screen.OpusDetail.createRoute(opusId)
                                )
                            }
                            else -> {
                                contentNavController.navigate(
                                    Screen.DynamicDetail.createRoute(dynamic.id)
                                )
                            }
                        }
                    },
                    onUserClick = { mid ->
                        contentNavController.navigate("user/$mid")
                    },
                    onVideoClick = { bvid ->
                        contentNavController.navigate("video_detail/0/$bvid")
                    },
                    onImageClick = { imageUrls, initialPage ->
                        val encodedUrls = imageUrls.joinToString(",") { java.net.URLEncoder.encode(it, "UTF-8") }
                        contentNavController.navigate("imageViewer/$encodedUrls/$initialPage")
                    },
                    onMenuClick = openMenu
                )
            }

            appComposable(
                route = Screen.VideoDetail.route,
                arguments = listOf(
                    navArgument("avid") { type = NavType.LongType },
                    navArgument("bvid") { type = NavType.StringType }
                )
            ) {
                VideoDetailScreen(navController = contentNavController)
            }

            appComposable(
                route = Screen.DynamicDetail.route,
                arguments = listOf(
                    navArgument("dynamicId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val dynamicId = backStackEntry.arguments?.getString("dynamicId") ?: ""
                DynamicDetailScreen(
                    dynamicId = dynamicId,
                    navController = contentNavController,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onUserClick = { mid ->
                        contentNavController.navigate("user/$mid")
                    },
                    onVideoClick = { bvid ->
                        contentNavController.navigate("video_detail/0/$bvid")
                    },
                    onImageClick = { imageUrls, initialPage ->
                        val encodedUrls = imageUrls.joinToString(",") { java.net.URLEncoder.encode(it, "UTF-8") }
                        contentNavController.navigate("imageViewer/$encodedUrls/$initialPage")
                    },
                    onDynamicClick = { dynamic ->
                        val major = dynamic.modules.contentModule.major
                        when {
                            major?.type == "MAJOR_TYPE_OPUS" && major.opus != null -> {
                                val opusId = dynamic.id
                                contentNavController.navigate(
                                    Screen.OpusDetail.createRoute(opusId)
                                )
                            }
                            else -> {
                                contentNavController.navigate(
                                    Screen.DynamicDetail.createRoute(dynamic.id)
                                )
                            }
                        }
                    }
                )
            }

            appComposable(
                route = Screen.OpusDetail.route,
                arguments = listOf(
                    navArgument("opusId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val opusId = backStackEntry.arguments?.getString("opusId") ?: ""
                OpusDetailScreen(
                    opusId = opusId,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onUserClick = { mid ->
                        contentNavController.navigate("user/$mid")
                    },
                    onVideoClick = { bvid ->
                        contentNavController.navigate("video_detail/0/$bvid")
                    },
                    onImageClick = { imageUrls, initialPage ->
                        val encodedUrls = imageUrls.joinToString(",") { java.net.URLEncoder.encode(it, "UTF-8") }
                        contentNavController.navigate("imageViewer/$encodedUrls/$initialPage")
                    },
                    onCommentDetailClick = { replyId, oid ->
                        contentNavController.navigate("comment_detail/$replyId?oid=$oid&type=11")
                    },
                    onWriteReplyClick = { oid, rpid, parent, parentSender ->
                        contentNavController.navigate("write_reply/$oid/$rpid/$parent?parentSender=${parentSender ?: ""}")
                    }
                )
            }

            appComposable(
                route = "comment_detail/{replyId}?oid={oid}&type={type}",
                arguments = listOf(
                    navArgument("replyId") { type = NavType.LongType },
                    navArgument("oid") { 
                        type = NavType.LongType
                        defaultValue = -1
                    },
                    navArgument("type") { 
                        type = NavType.IntType
                        defaultValue = 1
                    }
                )
            ) { backStackEntry ->
                val replyId = backStackEntry.arguments?.getLong("replyId") ?: 0L
                val oidArg = backStackEntry.arguments?.getLong("oid") ?: -1L
                val oid = if (oidArg == -1L) null else oidArg
                val type = backStackEntry.arguments?.getInt("type") ?: 1
                CommentDetailScreen(
                    replyId = replyId,
                    oid = oid ?: 0L,
                    type = type,
                    onBackClick = { contentNavController.popBackStack() },
                    onWriteReplyClick = { oid, rpid, parent, parentSender ->
                        contentNavController.navigate("write_reply/$oid/$rpid/$parent?parentSender=${parentSender ?: ""}")
                    },
                    onUserClick = { userId ->
                        contentNavController.navigate("user/$userId")
                    },
                    onOpusClick = { opusId ->
                        contentNavController.navigate("opus_detail/$opusId")
                    },
                    onVideoClick = { aid, bvid ->
                        contentNavController.navigate("video_detail/$aid/$bvid")
                    }
                )
            }

            appComposable(
                route = "write_reply/{oid}/{rpid}/{parent}?parentSender={parentSender}",
                arguments = listOf(
                    navArgument("oid") { type = NavType.LongType },
                    navArgument("rpid") { type = NavType.LongType },
                    navArgument("parent") { type = NavType.LongType },
                    navArgument("parentSender") { 
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val oid = backStackEntry.arguments?.getLong("oid") ?: 0L
                val rpid = backStackEntry.arguments?.getLong("rpid") ?: 0L
                val parent = backStackEntry.arguments?.getLong("parent") ?: 0L
                val parentSender = backStackEntry.arguments?.getString("parentSender")
                WriteReplyScreen(
                    oid = oid,
                    rpid = rpid,
                    parent = parent,
                    parentSender = parentSender,
                    onBackClick = { contentNavController.popBackStack() },
                    onReplySuccess = { 
                        // 回复成功后返回上一页
                        contentNavController.popBackStack()
                    }
                )
            }

            appComposable(
                route = "image/{imageUrl}/{initialPage}",
                arguments = listOf(
                    navArgument("imageUrl") { type = NavType.StringType },
                    navArgument("initialPage") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->
                val encodedUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                val imageUrl = URLDecoder.decode(encodedUrl, "UTF-8")
                val initialPage = backStackEntry.arguments?.getInt("initialPage") ?: 0
                ImageViewerScreen(
                    imageUrls = listOf(imageUrl),
                    initialPage = initialPage,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = "imageViewer/{imageUrls}/{initialPage}",
                arguments = listOf(
                    navArgument("imageUrls") { type = NavType.StringType },
                    navArgument("initialPage") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->
                val encodedUrls = backStackEntry.arguments?.getString("imageUrls") ?: ""
                val imageUrls = encodedUrls.split(",").map { URLDecoder.decode(it, "UTF-8") }
                val initialPage = backStackEntry.arguments?.getInt("initialPage") ?: 0
                ImageViewerScreen(
                    imageUrls = imageUrls,
                    initialPage = initialPage,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = "user/{mid}",
                arguments = listOf(
                    navArgument("mid") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val mid = backStackEntry.arguments?.getLong("mid") ?: 0
                UserProfileScreen(
                    mid = mid,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onDynamicClick = { dynamicId ->
                        contentNavController.navigate("dynamic_detail/$dynamicId")
                    },
                    onVideoClick = { videoInfo ->
                        contentNavController.navigate(
                            Screen.VideoDetail.createRoute(videoInfo.aid, videoInfo.bvid)
                        )
                    },
                    onOpusClick = { opusId ->
                        contentNavController.navigate("opus_detail/$opusId")
                    },
                    onImageClick = { imageUrls, initialPage ->
                        val encodedUrls = imageUrls.joinToString(",") { java.net.URLEncoder.encode(it, "UTF-8") }
                        contentNavController.navigate("imageViewer/$encodedUrls/$initialPage")
                    },
                    onSeriesClick = { type, mid, id, name ->
                        val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
                        contentNavController.navigate("series/$type/$mid/$id/$encodedName")
                    }
                )
            }

            appComposable(
                route = "collection/{mid}/{seasonId}/{title}",
                arguments = listOf(
                    navArgument("mid") { type = NavType.LongType },
                    navArgument("seasonId") { type = NavType.LongType },
                    navArgument("title") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val mid = backStackEntry.arguments?.getLong("mid") ?: 0L
                val seasonId = backStackEntry.arguments?.getLong("seasonId") ?: 0L
                val title = backStackEntry.arguments?.getString("title")
                    ?.let { java.net.URLDecoder.decode(it, "UTF-8") }
                    ?: ""
                CollectionDetailScreen(
                    mid = mid,
                    seasonId = seasonId,
                    title = title,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onVideoClick = { contentNavController.navigate(Screen.VideoDetail.createRoute(it.aid, it.bvid)) }
                )
            }

            appComposable(
                route = "player/{aid}/{cid}",
                arguments = listOf(
                    navArgument("aid") { type = NavType.LongType },
                    navArgument("cid") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) { backStackEntry ->
                val aid = backStackEntry.arguments?.getLong("aid") ?: 0
                val cid = backStackEntry.arguments?.getLong("cid") ?: 0
                PlayerScreen(
                    aid = aid,
                    cid = cid,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = "player_local?path={path}&title={title}",
                arguments = listOf(
                    navArgument("path") { type = NavType.StringType },
                    navArgument("title") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val path = backStackEntry.arguments?.getString("path") ?: ""
                val title = backStackEntry.arguments?.getString("title") ?: ""
                PlayerScreen(
                    localVideoPath = path,
                    localVideoTitle = title,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.DownloadList.route) {
                DownloadListScreen(
                    onNavigateBack = { contentNavController.popBackStack() },
                    onPlayLocal = { path, title ->
                        contentNavController.navigate(
                            "player_local?path=${Uri.encode(path)}&title=${Uri.encode(title)}"
                        )
                    },
                    onMenuClick = openMenu
                )
            }

            appComposable(Screen.MySpace.route) {
                MySpaceScreen(
                    onNavigateToUserProfile = { userId ->
                        contentNavController.navigate("user/$userId")
                    },
                    onNavigateToHistory = {
                        contentNavController.navigate("history")
                    },
                    onNavigateToWatchLater = {
                        contentNavController.navigate("watch_later")
                    },
                    onNavigateToFavorite = {
                        contentNavController.navigate("favorite")
                    },
                    onNavigateToFollowing = {
                        contentNavController.navigate("following")
                    },
                    onNavigateToVipCenter = {
                        contentNavController.navigate(Screen.VipCenter.route)
                    },
                    onNavigateToCoinLog = {
                        contentNavController.navigate(Screen.CoinLog.route)
                    },
                    onNavigateToExpLog = {
                        contentNavController.navigate(Screen.ExpLog.route)
                    },
                    onNavigateToLiveMedal = {
                        contentNavController.navigate(Screen.LiveMedalWall.route)
                    },
                    onNavigateToFollowTags = {
                        contentNavController.navigate(Screen.FollowTags.route)
                    },
                    onMenuClick = openMenu
                )
            }

            appComposable("history") {
                HistoryScreen(
                    onVideoClick = { videoInfo ->
                        contentNavController.navigate(
                            Screen.VideoDetail.createRoute(videoInfo.aid, videoInfo.bvid)
                        )
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable("watch_later") {
                WatchLaterScreen(
                    onVideoClick = { videoInfo ->
                        contentNavController.navigate(
                            Screen.VideoDetail.createRoute(videoInfo.aid, videoInfo.bvid)
                        )
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable("favorite") {
                FavoriteScreen(
                    onFolderClick = { fid, name ->
                        val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
                        contentNavController.navigate("favorite_videos/$fid/$encodedName")
                    },
                    onOpusFavoriteClick = {
                        contentNavController.navigate("opus_favorite")
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = "favorite_videos/{fid}/{name}",
                arguments = listOf(
                    navArgument("fid") { type = NavType.LongType },
                    navArgument("name") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val fid = backStackEntry.arguments?.getLong("fid") ?: 0L
                val encodedName = backStackEntry.arguments?.getString("name") ?: ""
                val name = URLDecoder.decode(encodedName, "UTF-8")
                val mid = AccountManager.currentAccount.accountId
                
                FavoriteVideosScreen(
                    mid = mid,
                    fid = fid,
                    folderName = name,
                    onVideoClick = { videoInfo ->
                        contentNavController.navigate(
                            Screen.VideoDetail.createRoute(videoInfo.aid, videoInfo.bvid)
                        )
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable("opus_favorite") {
                OpusFavoriteScreen(
                    onOpusClick = { opusId ->
                        contentNavController.navigate("opus_detail/$opusId")
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable("following") {
                val mid = AccountManager.currentAccount.accountId
                FollowingScreen(
                    mid = mid,
                    onUserClick = { userId ->
                        contentNavController.navigate("user/$userId")
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.Search.route) {
                SearchScreen(
                    onMenuClick = openMenu,
                    onSearch = { query ->
                        contentNavController.navigate(Screen.SearchResult.createRoute(query))
                    },
                    onHotSearch = {
                        contentNavController.navigate(Screen.HotSearch.route)
                    }
                )
            }

            appComposable(
                route = Screen.SearchResult.route,
                arguments = listOf(
                    navArgument("keyword") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val encodedKeyword = backStackEntry.arguments?.getString("keyword") ?: ""
                val query = URLDecoder.decode(encodedKeyword, "UTF-8")
                SearchResultScreen(
                    query = query,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onVideoClick = { aid, bvid ->
                        contentNavController.navigate(Screen.VideoDetail.createRoute(aid, bvid))
                    },
                    onOpusClick = { opusId ->
                        contentNavController.navigate("opus_detail/$opusId")
                    },
                    onUserClick = { mid ->
                        contentNavController.navigate("user/$mid")
                    }
                )
            }

            // ===== 新功能路由 =====

            appComposable(Screen.Ranking.route) {
                RankingScreen(
                    onVideoClick = { videoInfo ->
                        contentNavController.navigate(
                            Screen.VideoDetail.createRoute(videoInfo.aid, videoInfo.bvid)
                        )
                    },
                    onNavigateBack = { contentNavController.popBackStack() },
                    onMenuClick = openMenu
                )
            }

            appComposable(Screen.Timeline.route) {
                TimelineScreen(
                    onBangumiClick = { seasonId ->
                        contentNavController.navigate("bangumi/$seasonId")
                    },
                    onNavigateBack = { contentNavController.popBackStack() },
                    onMenuClick = openMenu
                )
            }

            appComposable(Screen.MessageCenter.route) {
                MessageCenterScreen(
                    onLikeClick = {
                        contentNavController.navigate(Screen.LikeMessages.route)
                    },
                    onReplyClick = {
                        contentNavController.navigate(Screen.ReplyMessages.route)
                    },
                    onAtClick = {
                        contentNavController.navigate("at_messages")
                    },
                    onSystemClick = {
                        contentNavController.navigate(Screen.SystemMessages.route)
                    },
                    onPrivateMsgClick = {
                        contentNavController.navigate(Screen.PrivateMessages.route)
                    },
                    onNavigateBack = { contentNavController.popBackStack() },
                    onMenuClick = openMenu
                )
            }

            appComposable(Screen.LikeMessages.route) {
                LikeMessagesScreen(
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.ReplyMessages.route) {
                ReplyMessagesScreen(
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.SystemMessages.route) {
                SystemMessagesScreen(
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = Screen.Conversation.route,
                arguments = listOf(
                    navArgument("talkerUid") { type = NavType.LongType },
                    navArgument("talkerName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val talkerUid = backStackEntry.arguments?.getLong("talkerUid") ?: 0L
                val talkerName = URLDecoder.decode(
                    backStackEntry.arguments?.getString("talkerName") ?: "", "UTF-8"
                )
                ConversationScreen(
                    talkerUid = talkerUid,
                    talkerName = talkerName,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = Screen.DanmakuSend.route,
                arguments = listOf(
                    navArgument("cid") { type = NavType.LongType },
                    navArgument("aid") { type = NavType.LongType; defaultValue = 0L },
                    navArgument("bvid") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val cid = backStackEntry.arguments?.getLong("cid") ?: 0L
                val aid = backStackEntry.arguments?.getLong("aid") ?: 0L
                val bvid = backStackEntry.arguments?.getString("bvid") ?: ""
                DanmakuSendScreen(
                    cid = cid,
                    aid = aid,
                    bvid = bvid,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onSendSuccess = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.FollowingBangumi.route) {
                rj.kilikili.ui.screens.bangumi.FollowingBangumiScreen(
                    onBangumiClick = { mediaId ->
                        contentNavController.navigate("bangumi/$mediaId")
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.LoginRecords.route) {
                LoginRecordScreen(
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.SendDynamic.route) {
                SendDynamicScreen(
                    onNavigateBack = { contentNavController.popBackStack() },
                    onPublishSuccess = { dynamicId ->
                        contentNavController.navigate("dynamic_detail/$dynamicId")
                    }
                )
            }

            appComposable(Screen.PrivateMessages.route) {
                PrivateMsgScreen(
                    onSessionClick = { talkerUid ->
                        // TODO: 跳转到私信聊天详情页
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.VipCenter.route) {
                VipScreen(
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.CoinLog.route) {
                CoinLogScreen(
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.ExpLog.route) {
                ExpLogScreen(
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.LiveMedalWall.route) {
                LiveMedalWallScreen(
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.FollowTags.route) {
                FollowTagScreen(
                    onTagClick = { tagId, name ->
                        // TODO: 跳转到分组用户列表
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(Screen.PopularSeries.route) {
                PopularSeriesScreen(
                    onSeriesClick = { seriesId, name ->
                        contentNavController.navigate("popular_series/${seriesId}/${Uri.encode(name)}")
                    },
                    onNavigateBack = { contentNavController.popBackStack() },
                    onMenuClick = openMenu
                )
            }

            appComposable(
                route = "popular_series/{seriesId}/{name}",
                arguments = listOf(
                    navArgument("seriesId") { type = NavType.IntType },
                    navArgument("name") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val seriesId = backStackEntry.arguments?.getInt("seriesId") ?: 0
                val name = URLDecoder.decode(backStackEntry.arguments?.getString("name") ?: "", "UTF-8")
                PopularSeriesDetailScreen(
                    seriesId = seriesId,
                    seriesName = name,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onVideoClick = { aid, bvid ->
                        contentNavController.navigate(Screen.VideoDetail.createRoute(aid, bvid))
                    }
                )
            }

            appComposable(Screen.HotSearch.route) {
                HotSearchScreen(
                    onSearch = { query ->
                        contentNavController.navigate(Screen.SearchResult.createRoute(query))
                    },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = Screen.FansList.route,
                arguments = listOf(navArgument("mid") { type = NavType.LongType })
            ) { entry ->
                val mid = entry.arguments?.getLong("mid") ?: 0L
                FansScreen(
                    mid = mid,
                    onUserClick = { uid -> contentNavController.navigate("user/$uid") },
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(route = Screen.FollowedLive.route) {
                FollowedLiveScreen(
                    onNavigateBack = { contentNavController.popBackStack() },
                    onRoomClick = { roomId -> /* navigate to live room if exists */ }
                )
            }

            appComposable(
                route = Screen.SuperChat.route,
                arguments = listOf(navArgument("roomId") { type = NavType.LongType })
            ) { entry ->
                val roomId = entry.arguments?.getLong("roomId") ?: 0L
                SuperChatScreen(
                    roomId = roomId,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = Screen.DanmakuHistory.route,
                arguments = listOf(navArgument("roomId") { type = NavType.LongType })
            ) { entry ->
                val roomId = entry.arguments?.getLong("roomId") ?: 0L
                DanmakuHistoryScreen(
                    roomId = roomId,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = Screen.SameFollowing.route,
                arguments = listOf(navArgument("mid") { type = NavType.LongType })
            ) { entry ->
                val mid = entry.arguments?.getLong("mid") ?: 0L
                SameFollowingScreen(
                    mid = mid,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onUserClick = { uid -> contentNavController.navigate("user/$uid") }
                )
            }

            appComposable(
                route = Screen.SearchFollowing.route,
                arguments = listOf(navArgument("mid") { type = NavType.LongType })
            ) { entry ->
                val mid = entry.arguments?.getLong("mid") ?: 0L
                SearchFollowingScreen(
                    mid = mid,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onUserClick = { uid -> contentNavController.navigate("user/$uid") }
                )
            }

            appComposable(
                route = Screen.RecentCoinVideos.route,
                arguments = listOf(navArgument("mid") { type = NavType.LongType })
            ) { entry ->
                val mid = entry.arguments?.getLong("mid") ?: 0L
                RecentCoinVideosScreen(
                    mid = mid,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onVideoClick = { v -> contentNavController.navigate(Screen.VideoDetail.createRoute(v.aid, v.bvid)) }
                )
            }

            appComposable(
                route = Screen.RecentLikeVideos.route,
                arguments = listOf(navArgument("mid") { type = NavType.LongType })
            ) { entry ->
                val mid = entry.arguments?.getLong("mid") ?: 0L
                RecentLikeVideosScreen(
                    mid = mid,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = Screen.SearchHistory.route,
                arguments = listOf(navArgument("mid") { type = NavType.LongType })
            ) { entry ->
                val mid = entry.arguments?.getLong("mid") ?: 0L
                SearchHistoryScreen(
                    mid = mid,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(route = Screen.DmFilter.route) {
                DmFilterScreen(onNavigateBack = { contentNavController.popBackStack() })
            }

            appComposable(
                route = Screen.NoteList.route,
                arguments = listOf(navArgument("oid") { type = NavType.LongType })
            ) { entry ->
                val oid = entry.arguments?.getLong("oid") ?: 0L
                NoteListScreen(oid = oid, onNavigateBack = { contentNavController.popBackStack() })
            }

            appComposable(
                route = Screen.Vote.route,
                arguments = listOf(navArgument("voteId") { type = NavType.LongType })
            ) { entry ->
                val voteId = entry.arguments?.getLong("voteId") ?: 0L
                VoteScreen(voteId = voteId, onNavigateBack = { contentNavController.popBackStack() })
            }

            appComposable(
                route = Screen.Reserve.route,
                arguments = listOf(
                    navArgument("reserveId") { type = NavType.LongType; defaultValue = 0L },
                    navArgument("upMid") { type = NavType.LongType; defaultValue = 0L }
                )
            ) { entry ->
                val reserveId = entry.arguments?.getLong("reserveId") ?: 0L
                val upMid = entry.arguments?.getLong("upMid") ?: 0L
                ReserveScreen(
                    reserveId = reserveId,
                    upMid = upMid,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = Screen.Topic.route,
                arguments = listOf(navArgument("topicId") { type = NavType.LongType })
            ) { entry ->
                val topicId = entry.arguments?.getLong("topicId") ?: 0L
                TopicScreen(
                    topicId = topicId,
                    onNavigateBack = { contentNavController.popBackStack() },
                    onItemClick = { idStr -> contentNavController.navigate(Screen.DynamicDetail.createRoute(idStr)) }
                )
            }

            appComposable(route = Screen.PgcIndex.route) {
                PgcIndexScreen(
                    onNavigateBack = { contentNavController.popBackStack() },
                    onBangumiClick = { seasonId -> /* TODO: navigate to bangumi detail */ }
                )
            }

            appComposable(route = Screen.PgcRank.route) {
                PgcRankScreen(
                    onNavigateBack = { contentNavController.popBackStack() },
                    onBangumiClick = { seasonId -> /* TODO: navigate to bangumi detail */ }
                )
            }

            appComposable(
                route = Screen.PgcReview.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.LongType })
            ) { entry ->
                val mediaId = entry.arguments?.getLong("mediaId") ?: 0L
                PgcReviewScreen(
                    mediaId = mediaId,
                    onNavigateBack = { contentNavController.popBackStack() }
                )
            }

            appComposable(
                route = Screen.Article.route,
                arguments = listOf(navArgument("cvid") { type = NavType.LongType })
            ) { entry ->
                val cvid = entry.arguments?.getLong("cvid") ?: 0L
                ArticleScreen(cvid = cvid, onNavigateBack = { contentNavController.popBackStack() })
            }

            settingsGraph(
                contentNavController,
                onMenuClick = openMenu
            )

            loginGraph(
                contentNavController,
                onLoginSuccess = {
                    contentNavController.navigate(menuConfig.firstDestination) {
                        popUpTo(NavGraph.LOGIN) { inclusive = true }
                    }
                },
                onSkip = {
                    contentNavController.navigate(menuConfig.firstDestination) {
                        popUpTo(NavGraph.LOGIN) { inclusive = true }
                    }
                }
            )
        }
}