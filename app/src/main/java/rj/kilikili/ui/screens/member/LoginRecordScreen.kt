package rj.kilikili.ui.screens.member

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.AppTopBar
import rj.kilikili.ui.components.auto.rememberAppScrollBehavior
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.LoginRecordUiState
import rj.kilikili.ui.viewmodel.LoginRecordViewModel
import com.huanli233.biliwebapi.bean.member.LoginRecord

@Composable
fun LoginRecordScreen(
    onNavigateBack: () -> Unit,
    viewModel: LoginRecordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    val scrollBehavior = rememberAppScrollBehavior()

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = {
            AppTopBar(
                title = "登录记录",
                scrollBehavior = scrollBehavior,
                showBackIcon = true,
                showMenuIcon = false,
                onBackClick = onNavigateBack,
                onMenuClick = null
            )
        },
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        when (val state = uiState) {
            is LoginRecordUiState.Loading -> {
                LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
            }
            is LoginRecordUiState.Error -> {
                LoadingView(
                    state = LoadingState.ERROR,
                    errorMessage = state.message,
                    onRetry = { viewModel.loadLoginRecord() },
                    modifier = Modifier.fillMaxSize()
                )
            }
            is LoginRecordUiState.Success -> {
                LoginRecordContent(
                    record = state.record,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun LoginRecordContent(
    record: LoginRecord,
    modifier: Modifier = Modifier
) {
    ScalingLazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    RecordRow(label = "设备名称", value = record.deviceName)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    RecordRow(label = "登录方式", value = record.loginType)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    RecordRow(label = "登录时间", value = record.loginTime)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    RecordRow(label = "登录位置", value = record.location)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    RecordRow(label = "IP 地址", value = record.ip)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RecordRow(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}