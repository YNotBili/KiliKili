package rj.kilikili.ui.components.freshwear

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.huanli233.biliwebapi.bean.dynamic.Dynamic
import com.huanli233.biliwebapi.bean.opus.OpusPicture
import com.huanli233.biliwebapi.bean.user.UserInfo
import com.valentinilk.shimmer.shimmer
import rj.kilikili.ui.theme.BiliPink
import rj.kilikili.ui.theme.WearDarkSurfaceContainer
import rj.kilikili.ui.theme.WearDarkSurfaceContainerHigh
import rj.kilikili.utils.extensions.formatNumber
import rj.kilikili.utils.extensions.toHttpsUrl

/**
 * Freshwear 风格动态卡片（Wear OS 专用）。
 *
 * 设计要点：
 * - 紧凑头部：28dp 圆形头像 + 用户名 + 类型标签
 * - 文字内容最多 3 行，超出截断
 * - 图片网格：单图 100dp 高 / 多图方形 2 列
 * - 转发内容使用深色半透明背景嵌套
 * - 嵌套视频复用 ImmersiveVideoCard
 * - 底部操作栏：点赞/转发/评论，点赞激活用 BiliPink
 */
@Composable
fun FreshwearDynamicCard(
    dynamic: Dynamic,
    onClick: () -> Unit,
    onUserClick: (Long) -> Unit = {},
    onVideoClick: (String) -> Unit = {},
    onImageClick: (List<String>, Int) -> Unit = { _, _ -> },
    onLikeClick: ((String, Boolean) -> Unit)? = null,
    onDynamicClick: (Dynamic) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = WearDarkSurfaceContainer
        ),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            // ── 头部：头像 + 用户名 + 类型标签 ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = dynamic.modules.authorModule.face?.takeIf { it.isNotBlank() },
                    contentDescription = dynamic.modules.authorModule.name,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onUserClick(dynamic.modules.authorModule.mid) },
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dynamic.modules.authorModule.name.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val pubAction = dynamic.modules.authorModule.pubAction.orEmpty()
                    if (pubAction.isNotBlank()) {
                        Text(
                            text = pubAction,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── 文字内容：最多 3 行 ──
            dynamic.modules.contentModule.desc?.let { desc ->
                if (desc.text.isNotBlank()) {
                    Text(
                        text = desc.text,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // ── 主体内容：视频 / 图片 / Opus ──
            dynamic.modules.contentModule.major?.let { major ->
                when (major.type) {
                    "MAJOR_TYPE_ARCHIVE" -> {
                        major.archive?.let { archive ->
                            val author = buildUserInfo(dynamic)
                            ImmersiveVideoCard(
                                videoInfo = archive.toVideoInfo(author),
                                onClick = { onVideoClick(archive.bvid) }
                            )
                        }
                    }
                    "MAJOR_TYPE_DRAW" -> {
                        major.opus?.let { opus ->
                            if (opus.pics.isNotEmpty()) {
                                FreshwearImageGrid(
                                    pics = opus.pics,
                                    onImageClick = { index ->
                                        onImageClick(opus.pics.map { it.url }, index)
                                    }
                                )
                            }
                        }
                    }
                    "MAJOR_TYPE_OPUS" -> {
                        major.opus?.let { opus ->
                            opus.summary?.let { summary ->
                                if (summary.text.isNotBlank()) {
                                    Text(
                                        text = summary.text,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                            if (opus.pics.isNotEmpty()) {
                                FreshwearImageGrid(
                                    pics = opus.pics,
                                    onImageClick = { index ->
                                        onImageClick(opus.pics.map { it.url }, index)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ── 转发内容：深色半透明嵌套 ──
            dynamic.origin?.let { origin ->
                Spacer(modifier = Modifier.height(6.dp))
                FreshwearOriginBlock(
                    origin = origin,
                    onClick = { onDynamicClick(origin) },
                    onVideoClick = onVideoClick,
                    onImageClick = { urls, index -> onImageClick(urls, index) }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── 底部操作栏 ──
            val isLiked = dynamic.modules.statsModule.like.status
            val likeCount = dynamic.modules.statsModule.like.count
            val forwardCount = dynamic.modules.statsModule.forward.count ?: 0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 点赞
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (onLikeClick != null) Modifier.clickable {
                                onLikeClick(dynamic.id, isLiked)
                            } else Modifier
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "点赞",
                        modifier = Modifier.size(14.dp),
                        tint = if (isLiked) BiliPink else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatCountNumber(likeCount.toLong()),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLiked) BiliPink else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // 转发
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "转发",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatCountNumber(forwardCount.toLong()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // 评论
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "评论",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "评论",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

// ─── 图片网格 ─────────────────────────────────────────────────────────────────

@Composable
private fun FreshwearImageGrid(
    pics: List<OpusPicture>,
    onImageClick: (Int) -> Unit
) {
    val count = pics.size
    when {
        count == 1 -> {
            val rawUrl = pics[0].url.toHttpsUrl()
            val imageUrl = if (rawUrl.contains("@")) rawUrl else "$rawUrl@480w_270h_1c.webp"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onImageClick(0) }
            ) {
                var isLoading by remember { mutableStateOf(true) }
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(200)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onSuccess = { isLoading = false },
                    onError = { isLoading = false }
                )
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmer()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    )
                }
            }
        }
        else -> {
            // 多图：方形 2 列网格
            val rows = (count + 1) / 2
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (row in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (col in 0..1) {
                            val idx = row * 2 + col
                            if (idx < count) {
                                val rawUrl = pics[idx].url.toHttpsUrl()
                                val imageUrl = if (rawUrl.contains("@")) rawUrl else "$rawUrl@480w_270h_1c.webp"
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onImageClick(idx) }
                                ) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── 转发嵌套块 ────────────────────────────────────────────────────────────────

@Composable
private fun FreshwearOriginBlock(
    origin: Dynamic,
    onClick: () -> Unit,
    onVideoClick: (String) -> Unit,
    onImageClick: (List<String>, Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = WearDarkSurfaceContainerHigh,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // 原作者名
            Text(
                text = "@${origin.modules.authorModule.name}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 文字内容
            origin.modules.contentModule.desc?.let { desc ->
                if (desc.text.isNotBlank()) {
                    Text(
                        text = desc.text,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 嵌套主体
            origin.modules.contentModule.major?.let { major ->
                when (major.type) {
                    "MAJOR_TYPE_ARCHIVE" -> {
                        major.archive?.let { archive ->
                            val author = buildUserInfoFromOrigin(origin)
                            ImmersiveVideoCard(
                                videoInfo = archive.toVideoInfo(author),
                                onClick = { onVideoClick(archive.bvid) }
                            )
                        }
                    }
                    "MAJOR_TYPE_DRAW", "MAJOR_TYPE_OPUS" -> {
                        major.opus?.let { opus ->
                            if (opus.pics.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    opus.pics.take(3).forEachIndexed { idx, pic ->
                                        val rawUrl = pic.url.toHttpsUrl()
                                        val imageUrl = if (rawUrl.contains("@")) rawUrl else "$rawUrl@480w_270h_1c.webp"
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(6.dp))
                                        ) {
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                    if (opus.pics.size < 3) {
                                        repeat(3 - opus.pics.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── 工具函数 ─────────────────────────────────────────────────────────────────

private fun formatCountNumber(count: Long): String {
    return count.formatNumber("万", "亿")
}

/**
 * 构建 UserInfo 用于 archive.toVideoInfo()。
 * 与 DynamicCard.kt 中的构造逻辑保持一致，仅复制必要字段。
 */
private fun buildUserInfo(dynamic: Dynamic): UserInfo = buildUserInfoFromAuthor(
    mid = dynamic.modules.authorModule.mid,
    name = dynamic.modules.authorModule.name,
    face = dynamic.modules.authorModule.face
)

private fun buildUserInfoFromOrigin(origin: Dynamic): UserInfo = buildUserInfoFromAuthor(
    mid = origin.modules.authorModule.mid,
    name = origin.modules.authorModule.name,
    face = origin.modules.authorModule.face
)

private fun buildUserInfoFromAuthor(mid: Long, name: String?, face: String?): UserInfo =
    UserInfo(
        mid = mid,
        title = null,
        name = name,
        face = face,
        vip = null,
        official = com.huanli233.biliwebapi.bean.user.Official(
            role = 0, title = "", desc = "", type = -1
        ),
        follower = 0,
        sex = "",
        sign = null,
        rank = 0,
        level = 0,
        silence = 0,
        coins = 0,
        fansBadge = false,
        fansMedal = null,
        pendant = com.huanli233.biliwebapi.bean.user.Pendant(
            pid = 0, name = "", image = "", expire = 0
        ),
        nameplate = com.huanli233.biliwebapi.bean.user.Nameplate(
            nid = 0, name = "", image = "", imageSmall = "", level = "", condition = ""
        ),
        isFollowed = false,
        topPhoto = "",
        systemNotice = null,
        liveRoom = null,
        series = com.huanli233.biliwebapi.bean.user.SeriesStatus(
            userUpgradeStatus = 0, showUpgradeWindow = false
        ),
        isSeniorMember = 0,
        contract = com.huanli233.biliwebapi.bean.user.ContractStatus(
            isDisplay = false, isFollowDisplay = false
        ),
        school = com.huanli233.biliwebapi.bean.user.School(name = "")
    )
