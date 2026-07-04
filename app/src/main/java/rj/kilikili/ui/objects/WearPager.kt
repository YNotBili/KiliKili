package rj.kilikili.ui.objects

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.PagerState
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material3.HorizontalPageIndicator

/**
 * WearPager - 封装 HorizontalPager 和 HorizontalPageIndicator 的通用组件
 *
 * 该组件封装了 Wear OS 的分页器功能，提供统一的页面切换体验。
 * 参考 Orbit 项目的 Pager 实现模式。
 *
 * @param pageCount 页面总数
 * @param modifier Modifier
 * @param pagerState Pager 状态，如果不提供则自动创建
 * @param indicatorPosition 页面指示器位置（默认在底部）
 * @param content 页面内容
 */
@Composable
fun WearPager(
    pageCount: Int,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { pageCount },
    indicatorPosition: IndicatorPosition = IndicatorPosition.Bottom,
    content: @Composable (page: Int) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // HorizontalPager 内容
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            content(page)
        }

        // HorizontalPageIndicator 页面指示器
        HorizontalPageIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(when (indicatorPosition) {
                    IndicatorPosition.Bottom -> Alignment.BottomCenter
                    IndicatorPosition.Top -> Alignment.TopCenter
                })
                .padding(vertical = 8.dp)
        )
    }
}

/**
 * 页面指示器位置枚举
 */
enum class IndicatorPosition {
    Bottom,
    Top
}