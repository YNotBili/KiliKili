package rj.kilikili.ui.screens.openva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import rj.kilikili.R
import rj.kilikili.priv.OpenVAHelper
import rj.kilikili.priv.InstallResultWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenVAScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSyscallFilter: () -> Unit = {}
) {
    var packageName by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("0") }
    var installedApps by remember { mutableStateOf<List<String>>(emptyList()) }
    var message by remember { mutableStateOf<String?>(null) }
    
    // Load installed apps
    LaunchedEffect(Unit) {
        try {
            installedApps = OpenVAHelper.getInstalledPackages(userId.toIntOrNull() ?: 0)
        } catch (e: Exception) {
            message = "加载失败: ${e.message}"
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.openva)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Install section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.openva_install),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        label = { Text(stringResource(R.string.openva_package_name_hint)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = { Text(stringResource(R.string.openva_user_id)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            if (packageName.isNotBlank()) {
                                try {
                                    val result = OpenVAHelper.installPackage(
                                        packageName, 
                                        userId.toIntOrNull() ?: 0
                                    )
                                    if (result.success) {
                                        message = "安装成功"
                                        // Refresh list
                                        installedApps = OpenVAHelper.getInstalledPackages(
                                            userId.toIntOrNull() ?: 0
                                        )
                                    } else {
                                        message = "安装失败: ${result.message}"
                                    }
                                } catch (e: Exception) {
                                    message = "安装失败: ${e.message}"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.openva_install))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Installed apps list
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.openva_installed_apps),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (installedApps.isEmpty()) {
                        Text(
                            text = stringResource(R.string.openva_no_apps),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn {
                            items(installedApps) { app ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = app,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    IconButton(
                                        onClick = {
                                            try {
                                                val success = OpenVAHelper.launchApp(
                                                    app, 
                                                    userId.toIntOrNull() ?: 0
                                                )
                                                message = if (success) {
                                                    "启动成功"
                                                } else {
                                                    "启动失败"
                                                }
                                            } catch (e: Exception) {
                                                message = "启动失败: ${e.message}"
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.PlayArrow,
                                            contentDescription = stringResource(R.string.openva_launch)
                                        )
                                    }
                                    
                                    IconButton(
                                        onClick = {
                                            try {
                                                OpenVAHelper.uninstallApp(
                                                    app, 
                                                    userId.toIntOrNull() ?: 0
                                                )
                                                message = "卸载成功"
                                                // Refresh list
                                                installedApps = OpenVAHelper.getInstalledPackages(
                                                    userId.toIntOrNull() ?: 0
                                                )
                                            } catch (e: Exception) {
                                                message = "卸载失败: ${e.message}"
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.openva_uninstall)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Syscall Filter Button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToSyscallFilter,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.syscall_filter))
            }
            
            // Message
            message?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Snackbar {
                    Text(msg)
                }
            }
        }
    }
}