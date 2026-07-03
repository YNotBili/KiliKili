package rj.kilikili.ui.components.freshwear

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.huanli233.biliwebapi.bean.reply.Reply
import kotlinx.coroutines.launch
import rj.kilikili.ui.components.RichText
import rj.kilikili.ui.screens.comment.CommentUiState
import rj.kilikili.ui.theme.BiliPink
import rj.kilikili.utils.ArticleRedirectUtil
import rj.kilikili.utils.extensions.formatNumber
import rj.kilikili.utils.extensions.formatToRelativeTime

/**
 * Freshwear 优化版评论卡片（Wear OS 专用）。
 *
 * 参考 Orbit ReplyCard 设计，针对手表小屏优化：
 * - 紧凑头部（24dp 头像）
 * - 富文本内容（复用 RichText）
 * - 图片 LazyRow 水平滚动
 * - 子评论预览区（最多 3 条）
 * - 点赞激活态使用 BiliPink
 */
@Composable
fun CommentCard(
    reply: Reply,
    onLikeClick: (Reply, Boolean) -> Unit,
    onCommentClick: (Reply) -> Unit = {},
    onReplyClick: (Reply) -> Unit = {},
    onUserClick: (Long) -> Unit = {},
    onBvidClick: (String) -> Unit = {},
    onAvidClick: (Long) -> Unit = {},
    onCvidClick: (Long) -> Unit = {},
    onUrlClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isLiked = reply.actionState == 1
    val pictures = reply.content.pictures.orEmpty()
    val childReplies = reply.replies.orEmpty()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onCommentClick(reply) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            // ── 头部：头像 + 用户名 + 时间/楼层 ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = reply.member.face?.takeIf { it.isNotBlank() },
                    contentDescription = reply.member.name,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onUserClick(reply.member.mid) },
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reply.member.name ?: "未知用户",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = (reply.createTime * 1000).formatToRelativeTime(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        if ((reply.floor ?: 0) > 0) {
                            Text(
                                text = "#${reply.floor}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── 评论内容：复用 RichText 渲染表情、@用户、链接 ──
            RichText(
                text = reply.content.message ?: "",
                emotes = reply.content.emote,
                atList = reply.content.atNameToMid.toList(),
                style = MaterialTheme.typography.bodySmall,
                onUserClick = onUserClick,
                onBvidClick = onBvidClick,
                onAvidClick = onAvidClick,
                onCvidClick = onCvidClick,
                onUrlClick = onUrlClick
            )

            // ── 图片：水平滚动 LazyRow ──
            if (pictures.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    items(pictures) { pic ->
                        val imgUrl = if (pic.src.startsWith("http")) {
                            "${pic.src}@480w_270h_1c.webp"
                        } else {
                            pic.src
                        }
                        AsyncImage(
                            model = imgUrl,
                            contentDescription = "评论图片",
                            modifier = Modifier
                                .height(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // ── 子评论预览：surfaceContainerHigh 背景，最多 3 条 ──
            if (childReplies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(6.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        childReplies.take(3).forEach { child ->
                            Text(
                                text = "${child.member.name ?: "未知用户"}: ${child.content.message ?: ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (reply.rcount > 0) {
                            Text(
                                text = "查看全部 ${reply.rcount} 条回复",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { onCommentClick(reply) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── 底部：点赞图标 + 数量 ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onLikeClick(reply, isLiked) }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "点赞",
                    modifier = Modifier.size(14.dp),
                    tint = if (isLiked) BiliPink else MaterialTheme.colorScheme.onSurface
                )
                if (reply.like > 0) {
                    Text(
                        text = reply.like.formatNumber("万", "亿"),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLiked) BiliPink else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Freshwear 评论卡片（带点赞状态管理）。
 *
 * 与 CommentScreen 中的 [CommentItemWithLikeState] 对应，
 * 负责计算并注入更新后的 like 数量，再交给 [CommentCard] 渲染。
 */
@Composable
fun FreshwearCommentCardWithLikeState(
    reply: Reply,
    isLiked: Boolean,
    onLikeClick: (Reply, Boolean) -> Unit,
    onCommentClick: (Reply) -> Unit,
    onReplyClick: (Reply) -> Unit,
    onUserClick: (Long) -> Unit = {},
    onOpusClick: (Long) -> Unit = {},
    uiState: CommentUiState
) {
    val originalLiked = reply.actionState == 1
    val likeCountDelta = when {
        isLiked && !originalLiked -> 1
        !isLiked && originalLiked -> -1
        else -> 0
    }

    val modifiedReply = reply.copy(
        actionState = if (isLiked) 1 else 0,
        like = reply.like + likeCountDelta
    )

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoadingArticle by remember { mutableStateOf(false) }

    CommentCard(
        reply = modifiedReply,
        onLikeClick = onLikeClick,
        onCommentClick = onCommentClick,
        onReplyClick = onReplyClick,
        onUserClick = onUserClick,
        onCvidClick = { cvid ->
            isLoadingArticle = true
            scope.launch {
                ArticleRedirectUtil.convertCvidToOpusId(cvid).fold(
                    onSuccess = { opusId ->
                        isLoadingArticle = false
                        onOpusClick(opusId)
                    },
                    onFailure = { error ->
                        isLoadingArticle = false
                        Toast.makeText(context, "无法打开文章: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    )

    if (isLoadingArticle) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}
