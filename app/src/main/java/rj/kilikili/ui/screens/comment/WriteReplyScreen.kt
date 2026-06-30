package rj.kilikili.ui.screens.comment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.isRoundDevice
import rj.kilikili.ui.components.auto.rememberAppLazyListState
import rj.kilikili.ui.components.auto.AppScreenScaffold
import rj.kilikili.data.setting.LocalData
import rj.kilikili.ui.components.auto.appTopBar

@Composable
fun WriteReplyScreen(
    oid: Long,
    rpid: Long = 0L,
    parent: Long = 0L,
    parentSender: String? = null,
    onBackClick: () -> Unit = {},
    onReplySuccess: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WriteReplyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRound = isRoundDevice() && LocalData.settings.uiSettings.roundMode
    val scrollState = rememberAppLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var replyText by remember { mutableStateOf("") }

    LaunchedEffect(parentSender) {
        if (!parentSender.isNullOrEmpty()) {
            replyText = "回复 @$parentSender :"
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is WriteReplyEvent.Success -> {
                    onReplySuccess()
                    onBackClick()
                }
                is WriteReplyEvent.Error -> {
                    // Error handling is done in ViewModel with MsgUtil
                }
            }
        }
    }
    
    AppScreenScaffold(
        scrollState = scrollState,
        modifier = modifier,
        topBar = appTopBar(
            title = "写评论",
            onBackClick = onBackClick
        )
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "写评论",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        placeholder = {
                            Text(
                                text = "写一条友善的评论...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (replyText.isNotBlank()) {
                                    keyboardController?.hide()
                                    viewModel.sendReply(oid, rpid, parent, replyText.trim())
                                }
                            }
                        ),
                        enabled = !uiState.isSending,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    keyboardController?.hide()
                                    viewModel.sendReply(oid, rpid, parent, replyText.trim())
                                }
                            },
                            enabled = !uiState.isSending && replyText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            if (uiState.isSending) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Send,
                                    contentDescription = "发送",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uiState.isSending) "发送中..." else "发送",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            if (uiState.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.error!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
