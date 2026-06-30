package rj.kilikili.ui.screens.login

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.PaddingDefaults
import rj.kilikili.ui.components.auto.AppScreenScaffold
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.WormIndicatorType
import rj.kilikili.ui.components.auto.rememberAppScrollBehavior
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.viewmodel.HdQrCodeLoginViewModel
import rj.kilikili.ui.viewmodel.HdQrStatus
import rj.kilikili.utils.QRCodeUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HdQrCodeLoginHost(
    onLoginSuccess: () -> Unit,
    onSkip: () -> Unit,
    onNavigateToImport: () -> Unit = {}
) {
    val viewModel: HdQrCodeLoginViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.status) {
        if (state.status == HdQrStatus.LOGIN_SUCCESS) onLoginSuccess()
    }

    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    val qrCodeScrollState = rememberScrollState()
    val importScrollState = rememberScrollState()

    val currentScrollState = when (pagerState.currentPage) {
        0 -> qrCodeScrollState
        else -> importScrollState
    }

    val scrollBehavior = rememberAppScrollBehavior()

    AppScreenScaffold(
        scrollState = currentScrollState,
        topBar = appTopBar(
            title = "登录",
            showBackIcon = true,
            onBackClick = onSkip,
            scrollBehavior = scrollBehavior
        ),
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> HdQrCodePage(
                        scrollState = qrCodeScrollState,
                        paddingValues = paddingValues,
                        viewModel = viewModel,
                        state = state,
                        onNavigateToImport = {
                            scope.launch { pagerState.animateScrollToPage(1) }
                        },
                        onSkip = onSkip
                    )
                    1 -> ImportLoginScreen(
                        scrollState = importScrollState,
                        paddingValues = paddingValues,
                        onLoginSuccess = onLoginSuccess
                    )
                }
            }

            DotsIndicator(
                modifier = Modifier
                    .padding(bottom = PaddingDefaults.verticalOptContentPadding())
                    .align(Alignment.BottomCenter),
                dotCount = pagerState.pageCount,
                dotSpacing = 8.dp,
                type = WormIndicatorType(
                    dotsGraphic = DotGraphic(
                        16.dp,
                        borderWidth = 2.dp,
                        borderColor = MaterialTheme.colorScheme.primary,
                        color = Color.Transparent,
                    ),
                    wormDotGraphic = DotGraphic(
                        16.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                ),
                pagerState = pagerState
            )
        }
    }
}

@Composable
private fun HdQrCodePage(
    scrollState: androidx.compose.foundation.ScrollState,
    paddingValues: PaddingValues,
    viewModel: HdQrCodeLoginViewModel,
    state: rj.kilikili.ui.viewmodel.HdQrCodeState,
    onNavigateToImport: () -> Unit,
    onSkip: () -> Unit
) {
    val statusText = when (state.status) {
        HdQrStatus.REQUESTING -> "获取二维码..."
        HdQrStatus.WAITING -> "请使用哔哩哔哩官方 App 扫码"
        HdQrStatus.SCANNED -> "二维码已扫描，等待确认..."
        HdQrStatus.EXPIRED -> "二维码已过期"
        HdQrStatus.LOGIN_SUCCESS -> "登录成功！"
        HdQrStatus.ERROR -> state.error ?: "登录失败"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (state.status) {
                    HdQrStatus.REQUESTING -> {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    }
                    HdQrStatus.WAITING -> {
                        val qrUrl = state.qrCodeUrl
                        if (qrUrl != null) {
                            val bitmap by produceState<Bitmap?>(initialValue = null, key1 = qrUrl) {
                                value = QRCodeUtil.createQRCodeBitmap(qrUrl, 512, 512)
                            }
                            bitmap?.let { bmp ->
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                    HdQrStatus.SCANNED -> {}
                    HdQrStatus.EXPIRED -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("二维码已过期", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    HdQrStatus.ERROR -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("加载失败", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    HdQrStatus.LOGIN_SUCCESS -> {
                        Text("登录成功！", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = statusText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (state.status) {
            HdQrStatus.WAITING -> {
                Button(onClick = onNavigateToImport) {
                    Text("从其他设备导入")
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(onClick = onSkip) { Text("跳过") }
            }
            HdQrStatus.EXPIRED, HdQrStatus.ERROR -> {
                Button(onClick = { viewModel.requestQrCode() }) {
                    Text("重新获取")
                }
            }
            else -> {}
        }
    }
}
