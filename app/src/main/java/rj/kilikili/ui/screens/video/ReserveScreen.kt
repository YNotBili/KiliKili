package rj.kilikili.ui.screens.video

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.ui.components.auto.appTopBar
import rj.kilikili.ui.dialog.AdaptDialog
import rj.kilikili.ui.viewmodel.ReserveViewModel
import rj.kilikili.utils.MsgUtil

@Composable
fun ReserveScreen(
    reserveId: Long = 0,
    upMid: Long = 0,
    viewModel: ReserveViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var title by remember { mutableStateOf("") }
    var livePlan by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(reserveId) {
        if (reserveId > 0) viewModel.load(reserveId)
        else showCreateDialog = true
    }

    AppScreenScaffold(
        topBar = appTopBar(
            title = stringResource(R.string.reserve),
            showBackIcon = true,
            onBackClick = onNavigateBack
        )
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (reserveId > 0 && uiState.info != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = uiState.info!!.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "预约 ID ${uiState.info!!.reserve_id} · 预约人数 ${uiState.info!!.total}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("标题") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = livePlan,
                onValueChange = { livePlan = it.filter { c -> c.isDigit() } },
                label = { Text("开播时间 (Unix 时间戳 秒)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = {
                    val plan = livePlan.toLongOrNull()
                    if (title.isBlank() || plan == null) {
                        MsgUtil.showMsg("标题和时间不能为空"); return@Button
                    }
                    if (reserveId > 0) {
                        viewModel.update(reserveId, title.trim(), plan) { ok, err ->
                            if (ok) MsgUtil.showMsg("已更新") else MsgUtil.showMsg(err ?: "失败")
                        }
                    } else {
                        viewModel.create(title.trim(), plan, upMid) { ok, id, err ->
                            if (ok) MsgUtil.showMsg("已创建 ID=$id") else MsgUtil.showMsg(err ?: "失败")
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (reserveId > 0) "更新" else "创建") }
        }
    }
}