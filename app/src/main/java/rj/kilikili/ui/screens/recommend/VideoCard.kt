package rj.kilikili.ui.screens.recommend

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.huanli233.biliwebapi.bean.video.VideoInfo

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
