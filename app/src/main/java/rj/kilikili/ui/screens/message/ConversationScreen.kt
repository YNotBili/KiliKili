package rj.kilikili.ui.screens.message

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.PaddingDefaults
import androidx.wear.compose.material3.ScreenScaffold
import rj.kilikili.R
import rj.kilikili.data.account.AccountManager
import rj.kilikili.ui.components.ScrollAwareTopBar
import rj.kilikili.ui.components.rememberEnterAlwaysScrollBehavior
import rj.kilikili.ui.screens.recommend.LoadingState
import rj.kilikili.ui.screens.recommend.LoadingView
import rj.kilikili.ui.viewmodel.ConversationViewModel
import rj.kilikili.utils.extensions.formatToDate
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage.Companion.TYPE_FACE
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage.Companion.TYPE_NOMAL_CARD
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage.Companion.TYPE_PIC
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage.Companion.TYPE_PIC_CARD
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage.Companion.TYPE_RETRACT
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage.Companion.TYPE_SYSTEM
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage.Companion.TYPE_TEXT
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage.Companion.TYPE_TEXT_WITH_VIDEO
import com.huanli233.biliwebapi.bean.privatemessage.PrivateMessage.Companion.TYPE_VIDEO

@Composable
fun ConversationScreen(
    talkerUid: Long,
    talkerName: String,
    onNavigateBack: () -> Unit,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScalingLazyListState()
    val scrollBehavior = rememberEnterAlwaysScrollBehavior()
    val currentUserUid = remember { AccountManager.currentAccount.accountId }
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(talkerUid) {
        viewModel.loadMessages(talkerUid)
        viewModel.updateAck(talkerUid)
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            scrollState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    ScreenScaffold(
        scrollState = scrollState,
        topBar = {
            ScrollAwareTopBar(
                title = talkerName,
                scrollBehavior = scrollBehavior,
                showBackIcon = true,
                showMenuIcon = false,
                onBackClick = onNavigateBack,
                onMenuClick = null
            )
        },
        topBarScrollBehavior = scrollBehavior
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.messages.isEmpty() -> {
                    LoadingView(state = LoadingState.LOADING, modifier = Modifier.fillMaxSize())
                }
                uiState.error != null && uiState.messages.isEmpty() -> {
                    LoadingView(
                        state = LoadingState.ERROR,
                        errorMessage = uiState.error,
                        onRetry = { viewModel.loadMessages(talkerUid) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    ScalingLazyColumn(
                        state = scrollState,
                        contentPadding = PaddingValues(
                            top = paddingValues.calculateTopPadding(),
                            bottom = PaddingDefaults.verticalContentPadding() + 56.dp
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = PaddingDefaults.horizontalContentPadding()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(uiState.messages, key = { it.msgId }) { message ->
                            ChatBubble(
                                message = message,
                                isMine = message.senderUid == currentUserUid
                            )
                        }
                    }

                    MessageInputBar(
                        text = inputText,
                        onTextChange = { inputText = it },
                        onSend = {
                            if (it.isNotBlank()) {
                                viewModel.sendMessage(
                                    content = it,
                                    senderUid = currentUserUid,
                                    talkerId = talkerUid
                                )
                                inputText = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: PrivateMessage,
    isMine: Boolean
) {
    val shape = if (isMine) {
        RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = 12.dp,
            bottomEnd = 4.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = 4.dp,
            bottomEnd = 12.dp
        )
    }

    val bgColor = if (isMine) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isMine) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(
                    max = if (LocalConfiguration.current.screenWidthDp < 200) {
                        160.dp
                    } else {
                        200.dp
                    }
                ),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = bgColor)
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text(
                    text = getMessageContent(message),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    color = contentColor
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getMessageTypeLabel(message.msgType),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (isMine) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        }
                    )

                    Text(
                        text = message.timestamp.let {
                            if (it.toString().length == 10) it * 1000 else it
                        }.formatToDate("HH:mm"),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (isMine) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                start = PaddingDefaults.horizontalContentPadding(),
                end = PaddingDefaults.horizontalContentPadding(),
                bottom = PaddingDefaults.verticalContentPadding()
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("输入消息", style = MaterialTheme.typography.bodySmall) },
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.bodySmall,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (text.isNotBlank()) {
                        onSend(text)
                    }
                }
            )
        )

        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSend(text)
                }
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_arrow_forward),
                contentDescription = "发送",
                tint = if (text.isNotBlank()) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                },
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun getMessageContent(message: PrivateMessage): String {
    return when (message.msgType) {
        TYPE_RETRACT -> "对方撤回了一条消息"
        TYPE_SYSTEM -> message.content
        else -> message.content
    }
}

private fun getMessageTypeLabel(msgType: Int): String {
    return when (msgType) {
        TYPE_TEXT -> "文本"
        TYPE_PIC -> "图片"
        TYPE_RETRACT -> "撤回"
        TYPE_FACE -> "表情"
        TYPE_VIDEO -> "视频"
        TYPE_NOMAL_CARD -> "卡片"
        TYPE_PIC_CARD -> "图文卡片"
        TYPE_TEXT_WITH_VIDEO -> "文本视频"
        TYPE_SYSTEM -> "系统消息"
        else -> "未知($msgType)"
    }
}
