package rj.kilikili.ui.components.freshwear

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.huanli233.biliwebapi.bean.video.VideoInfo
import com.valentinilk.shimmer.shimmer
import rj.kilikili.ui.components.auto.AppLazyListState
import rj.kilikili.ui.components.auto.shouldLoadItem
import rj.kilikili.ui.components.formatViews
import rj.kilikili.utils.extensions.toHttpsUrl

/**
 * Orbit 风格沉浸式视频卡片 — 仅用于 Wear 端。
 *
 * 设计：封面图全卡片覆盖 → 底部黑色半透明渐变 → 白色文字（标题 + UP主 + 播放量）。
 */
@Composable
fun ImmersiveVideoCard(
    videoInfo: VideoInfo,
    onClick: (VideoInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    ImmersiveVideoCardContent(
        videoInfo = videoInfo,
        onClick = onClick,
        modifier = modifier,
        loadCover = true
    )
}

@Composable
fun ImmersiveVideoCard(
    index: Int,
    listState: AppLazyListState?,
    videoInfo: VideoInfo,
    onClick: (VideoInfo) -> Unit,
    modifier: Modifier = Modifier,
    prefetch: Int = 3
) {
    val loadCover = listState?.shouldLoadItem(index, prefetch) ?: true
    ImmersiveVideoCardContent(
        videoInfo = videoInfo,
        onClick = onClick,
        modifier = modifier,
        loadCover = loadCover
    )
}

@Composable
private fun ImmersiveVideoCardContent(
    videoInfo: VideoInfo,
    onClick: (VideoInfo) -> Unit,
    modifier: Modifier = Modifier,
    loadCover: Boolean = true
) {
    var isLoading by remember { mutableStateOf(true) }

    // 图片 URL 优化：追加 B站裁剪参数，若已有 @ 后缀则跳过
    val rawUrl = videoInfo.pic.toHttpsUrl()
    val imageUrl = if (rawUrl.contains("@")) rawUrl else "$rawUrl@480w_270h_1c.webp"

    val titleShadow = remember {
        Shadow(color = Color.Black, offset = Offset.Zero, blurRadius = 4f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick(videoInfo) }
    ) {
        // 封面图
        if (loadCover) {
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
        }

        // Shimmer 占位
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmer()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            )
        }

        // 底部渐变遮罩
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // 文字信息叠加
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Text(
                text = videoInfo.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium.copy(
                    shadow = titleShadow
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                // UP主名
                Text(
                    text = videoInfo.owner.name.orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelSmall
                )

                // 播放图标 + 播放量
                if (videoInfo.viewCount > 0) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(12.dp),
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = formatViews(videoInfo.viewCount),
                        maxLines = 1,
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
