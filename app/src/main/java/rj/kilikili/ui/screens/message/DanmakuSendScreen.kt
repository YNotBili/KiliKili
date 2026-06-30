package rj.kilikili.ui.screens.message

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import androidx.wear.compose.material3.PaddingDefaults
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.R
import rj.kilikili.ui.components.auto.AppTopBar
import rj.kilikili.ui.components.auto.rememberAppScrollBehavior
import rj.kilikili.ui.viewmodel.DanmakuSendViewModel

private val PRESET_COLORS = listOf(
    0xFFFFFF to "白色",
    0xFF0000 to "红色",
    0xFF8C00 to "橙色",
    0xFFFF00 to "黄色",
    0x00FF00 to "绿色",
    0x00FFFF to "青色",
    0x0000FF to "蓝色",
    0xAA00FF to "紫色",
    0xFF69B4 to "粉色"
)

private data class DanmakuMode(val value: Int, val label: String)

private val DANMAKU_MODES = listOf(
    DanmakuMode(1, "滚动"),
    DanmakuMode(5, "顶部"),
    DanmakuMode(4, "底部")
)

@Composable
fun DanmakuSendScreen(
    cid: Long,
    aid: Long = 0,
    bvid: String = "",
    onNavigateBack: () -> Unit,
    onSendSuccess: () -> Unit,
    viewModel: DanmakuSendViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberAppLazyListState()
    val scrollBehavior = rememberAppScrollBehavior()

    LaunchedEffect(uiState.result) {
        if (uiState.result == "发送成功喵~") {
            onSendSuccess()
        }
    }

    AppScreenScaffold(
        scrollState = scrollState,
        topBar = {
            AppTopBar(
                title = "发送弹幕",
                scrollBehavior = scrollBehavior,
                showBackIcon = true,
                showMenuIcon = false,
                onBackClick = onNavigateBack,
                onMenuClick = null
            )
        },
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = PaddingDefaults.horizontalContentPadding(),
                    end = PaddingDefaults.horizontalContentPadding(),
                    top = paddingValues.calculateTopPadding(),
                    bottom = PaddingDefaults.verticalContentPadding()
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Text Input
            Text(
                text = "弹幕内容",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = uiState.text,
                onValueChange = viewModel::updateText,
                placeholder = { Text("输入弹幕...", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodySmall,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.send(
                            oid = cid,
                            aid = aid,
                            bvid = bvid,
                            progress = 0
                        )
                    }
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Color Picker
            Text(
                text = "颜色",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PRESET_COLORS.forEach { (colorInt, colorName) ->
                    val isSelected = uiState.color == colorInt
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(colorInt))
                            .then(
                                if (isSelected) {
                                    Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                } else {
                                    Modifier.border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                }
                            )
                            .clickable { viewModel.updateColor(colorInt) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_check),
                                contentDescription = colorName,
                                tint = if (colorInt == 0xFFFFFF || colorInt == 0xFFFF00) {
                                    Color.Black
                                } else {
                                    Color.White
                                },
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Mode Selector
            Text(
                text = "弹幕类型",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DANMAKU_MODES.forEach { mode ->
                    val isSelected = uiState.mode == mode.value
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                        onClick = { viewModel.updateMode(mode.value) }
                    ) {
                        Text(
                            text = mode.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Send Button
            Button(
                onClick = {
                    viewModel.send(
                        oid = cid,
                        aid = aid,
                        bvid = bvid,
                        progress = 0
                    )
                },
                enabled = uiState.text.isNotBlank() && !uiState.isSending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                if (uiState.isSending) {
                    Text(
                        text = "正在发送~",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_arrow_forward),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "发送",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Result Message
            uiState.result?.let { result ->
                if (result != "发送成功喵~") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = result,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}