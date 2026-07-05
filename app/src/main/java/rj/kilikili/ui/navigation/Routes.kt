package rj.kilikili.ui.navigation

import androidx.annotation.StringRes
import rj.kilikili.R
import java.net.URLEncoder

object NavGraph {
    const val SETUP = "setup_graph"
    const val LOGIN = "login"
    const val MAIN = "main_graph"
}

sealed class Screen(val route: String, @StringRes val titleResId: Int) {
    data object Recommend : Screen("recommend", R.string.recommend)
    data object Dynamic : Screen("dynamic", R.string.dynamic)
    data object Settings : Screen("settings_root", R.string.settings)

    data object DownloadList : Screen("download_list", R.string.download_manager)

    data object UiSettings : Screen("settings_ui", R.string.settings_ui)
    data object PlayerSettings : Screen("settings_player", R.string.settings_player)
    data object About : Screen("settings_about", R.string.about)
    data object ThemeColor : Screen("settings_theme_color", R.string.theme_color)
    data object ViewPreview : Screen("view_preview", R.string.view_preview)
    data object DeveloperOptions : Screen("settings_developer", R.string.developer_options)
    
    data object VideoDetail : Screen("video_detail/{avid}/{bvid}", R.string.app_name) {
        fun createRoute(avid: Long, bvid: String) = "video_detail/$avid/$bvid"
    }
    
    data object DynamicDetail : Screen("dynamic_detail/{dynamicId}", R.string.dynamic_detail) {
        fun createRoute(dynamicId: String) = "dynamic_detail/$dynamicId"
    }
    
    data object OpusDetail : Screen("opus_detail/{opusId}", R.string.opus_detail) {
        fun createRoute(opusId: String) = "opus_detail/$opusId"
    }
    
    data object Search : Screen("search", R.string.search)
    data object SearchResult : Screen("search_result/{keyword}", R.string.search_result) {
        fun createRoute(keyword: String): String {
            val encodedKeyword = URLEncoder.encode(keyword, "UTF-8")
            return "search_result/$encodedKeyword"
        }
    }
    
    data object MySpace : Screen("my_space", R.string.my_space)
    data object PhoneMine : Screen("phone_mine", R.string.phone_mine)

    // ===== 新功能屏幕 =====
    data object Popular : Screen("popular", R.string.popular)
    data object Precious : Screen("precious", R.string.precious)
    data object Ranking : Screen("ranking", R.string.ranking)
    data object Timeline : Screen("timeline", R.string.timeline)
    data object MessageCenter : Screen("message_center", R.string.message_center)
    data object VipCenter : Screen("vip_center", R.string.vip_center)
    data object CoinLog : Screen("coin_log", R.string.coin_log)
    data object ExpLog : Screen("exp_log", R.string.exp_log)
    data object PrivateMessages : Screen("private_messages", R.string.private_messages)
    data object LikeMessages : Screen("like_messages", R.string.like_messages)
    data object ReplyMessages : Screen("reply_messages", R.string.reply_messages)
    data object AtMessages : Screen("at_messages", R.string.at_messages)
    data object SystemMessages : Screen("system_messages", R.string.system_messages)
    data object Conversation : Screen("conversation/{talkerUid}/{talkerName}", R.string.private_messages) {
        fun createRoute(talkerUid: Long, talkerName: String): String {
            return "conversation/$talkerUid/${URLEncoder.encode(talkerName, "UTF-8")}"
        }
    }
    data object DanmakuSend : Screen("danmaku_send/{cid}?aid={aid}&bvid={bvid}", R.string.send_danmaku) {
        fun createRoute(cid: Long, aid: Long = 0, bvid: String = ""): String {
            return "danmaku_send/$cid?aid=$aid&bvid=$bvid"
        }
    }
    data object FollowingBangumi : Screen("following_bangumi", R.string.following_bangumi)
    data object LoginRecords : Screen("login_records", R.string.login_records)
    data object SendDynamic : Screen("send_dynamic", R.string.send_dynamic)
    data object LiveMedalWall : Screen("live_medal_wall", R.string.live_medal_wall)
    data object FollowTags : Screen("follow_tags", R.string.follow_tags)
    data object PopularSeries : Screen("popular_series", R.string.popular_series)
    data object HotSearch : Screen("hot_search", R.string.hot_search)
    data object FansList : Screen("fans_list/{mid}", R.string.fans_list) {
        fun createRoute(mid: Long) = "fans_list/$mid"
    }
    data object Blacklist : Screen("blacklist", R.string.blacklist)

    // ===== 阶段 2 新增屏幕 (B 类已有 Repository 补 UI) =====
    data object FollowedLive : Screen("followed_live", R.string.followed_live)
    data object SuperChat : Screen("super_chat/{roomId}", R.string.super_chat) {
        fun createRoute(roomId: Long) = "super_chat/$roomId"
    }
    data object DanmakuHistory : Screen("danmaku_history/{roomId}", R.string.danmaku_history) {
        fun createRoute(roomId: Long) = "danmaku_history/$roomId"
    }
    data object SameFollowing : Screen("same_following/{mid}", R.string.same_following) {
        fun createRoute(mid: Long) = "same_following/$mid"
    }
    data object SearchFollowing : Screen("search_following/{mid}", R.string.search_following) {
        fun createRoute(mid: Long) = "search_following/$mid"
    }
    data object RecentCoinVideos : Screen("recent_coin_videos/{mid}", R.string.recent_coin_videos) {
        fun createRoute(mid: Long) = "recent_coin_videos/$mid"
    }
    data object RecentLikeVideos : Screen("recent_like_videos/{mid}", R.string.recent_like_videos) {
        fun createRoute(mid: Long) = "recent_like_videos/$mid"
    }
    data object SearchHistory : Screen("search_history/{mid}", R.string.search_history) {
        fun createRoute(mid: Long) = "search_history/$mid"
    }

    // ===== 阶段 3 新增屏幕 =====
    data object DmFilter : Screen("dm_filter", R.string.danmaku_filter)
    data object NoteList : Screen("note_list/{oid}", R.string.video_note) {
        fun createRoute(oid: Long) = "note_list/$oid"
    }
    data object Vote : Screen("vote/{voteId}", R.string.vote) {
        fun createRoute(voteId: Long) = "vote/$voteId"
    }
    data object Reserve : Screen("reserve/{reserveId}/{upMid}", R.string.reserve) {
        fun createRoute(reserveId: Long = 0, upMid: Long = 0) = "reserve/$reserveId/$upMid"
    }
    data object Topic : Screen("topic/{topicId}", R.string.topic) {
        fun createRoute(topicId: Long) = "topic/$topicId"
    }
    data object PgcIndex : Screen("pgc_index", R.string.pgc_index)
    data object PgcRank : Screen("pgc_rank", R.string.pgc_rank)
    data object PgcReview : Screen("pgc_review/{mediaId}", R.string.pgc_review) {
        fun createRoute(mediaId: Long) = "pgc_review/$mediaId"
    }
    data object Article : Screen("article/{cvid}", R.string.article) {
        fun createRoute(cvid: Long) = "article/$cvid"
    }
    
    // OpenVA
    data object OpenVA : Screen("openva", R.string.openva)
    data object SyscallFilter : Screen("syscall_filter", R.string.syscall_filter)
}

val allScreens = listOf(
    Screen.Recommend,
    Screen.Dynamic,
    Screen.Popular,
    Screen.Precious,
    Screen.Settings,
    Screen.UiSettings,
    Screen.PlayerSettings,
    Screen.About,
    Screen.ThemeColor,
    Screen.ViewPreview,
    Screen.Search,
    Screen.MySpace,
    Screen.Ranking,
    Screen.Timeline,
    Screen.MessageCenter,
    Screen.VipCenter,
    Screen.CoinLog,
    Screen.ExpLog,
    Screen.PrivateMessages,
    Screen.LiveMedalWall,
    Screen.FollowTags,
    Screen.PopularSeries,
    Screen.HotSearch,
    Screen.FansList,
    Screen.Blacklist,
    Screen.OpenVA,
    Screen.SyscallFilter
)