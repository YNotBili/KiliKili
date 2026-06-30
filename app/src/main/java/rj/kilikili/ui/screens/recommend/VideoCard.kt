package rj.kilikili.ui.screens.recommend

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.huanli233.biliwebapi.bean.video.VideoInfo
import rj.kilikili.ui.components.auto.AppLazyListState

@Composable
fun VideoCard(
    videoInfo: VideoInfo,
    onClick: (VideoInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    rj.kilikili.ui.components.VideoCard(
        videoInfo = videoInfo,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun VideoCard(
    index: Int,
    listState: AppLazyListState?,
    videoInfo: VideoInfo,
    onClick: (VideoInfo) -> Unit,
    modifier: Modifier = Modifier,
    prefetch: Int = 3
) {
    rj.kilikili.ui.components.VideoCard(
        index = index,
        listState = listState,
        videoInfo = videoInfo,
        onClick = onClick,
        modifier = modifier,
        prefetch = prefetch
    )
}
