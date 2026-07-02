package rj.kilikili.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import rj.kilikili.R

private val reportReasons = listOf("其他", "违法违禁", "色情低俗", "人身攻击", "侵犯隐私", "垃圾广告", "引战", "未成年", "不良内容", "错误分类")

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ReportDialog(
    visible: Boolean,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (reason: String, content: String) -> Unit
) {
    if (!visible) return
    var selectedReason by remember { mutableStateOf(reportReasons.first()) }
    var content by remember { mutableStateOf("") }
    var reasonExpanded by remember { mutableStateOf(false) }

    AdaptDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = reasonExpanded,
                    onExpandedChange = { reasonExpanded = !reasonExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedReason,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("举报原因") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reasonExpanded) }
                    )
                    ExposedDropdownMenu(expanded = reasonExpanded, onDismissRequest = { reasonExpanded = false }) {
                        reportReasons.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r) },
                                onClick = { selectedReason = r; reasonExpanded = false }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("补充说明（可选）") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { close ->
            TextButton(onClick = { onConfirm(selectedReason, content.trim()); close() }) {
                Text(stringResource(R.string.report_submit))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
    )
}
