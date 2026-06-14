package rj.kilikili.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.ScreenScaffold
import rj.kilikili.R
import rj.kilikili.ui.components.rememberEnterAlwaysScrollBehavior
import rj.kilikili.ui.components.scrollAwareTopBar
import rj.kilikili.ui.viewmodel.HdQrCodeLoginViewModel
import rj.kilikili.ui.viewmodel.HdQrStatus
import rj.kilikili.utils.QRCodeUtil

@Composable
fun HdQrCodeLoginHost(
    onLoginSuccess: () -> Unit,
    onSkip: () -> Unit
) {
    val viewModel: HdQrCodeLoginViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val scrollBehavior = rememberEnterAlwaysScrollBehavior()

    LaunchedEffect(state.status) {
        if (state.status == HdQrStatus.LOGIN_SUCCESS) onLoginSuccess()
    }

    ScreenScaffold(
        scrollState = scrollState,
        topBar = scrollAwareTopBar(
            title = "HD 扫码登录",
            showBackIcon = true,
            onBackClick = onSkip,
            scrollBehavior = scrollBehavior
        ),
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (state.status) {
                HdQrStatus.REQUESTING -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("获取二维码...")
                }

                HdQrStatus.WAITING -> {
                    val qrUrl = state.qrCodeUrl
                    if (qrUrl != null) {
                        val bitmap by produceState<android.graphics.Bitmap?>(initialValue = null, key1 = qrUrl) {
                            QRCodeUtil.createQRCodeBitmap(qrUrl, 512, 512)
                        }
                        bitmap?.let { bmp ->
                            Card(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "QR Code",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "请使用哔哩哔哩官方 App 扫码",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                HdQrStatus.EXPIRED -> {
                    Text("二维码已过期", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.requestQrCode() }) {
                        Text("重新获取")
                    }
                }

                HdQrStatus.LOGIN_SUCCESS -> {
                    Text("登录成功！", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                HdQrStatus.ERROR -> {
                    Text(state.error ?: "登录失败", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.requestQrCode() }) {
                        Text("重试")
                    }
                }
            }
        }
    }
}