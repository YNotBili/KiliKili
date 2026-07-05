package rj.kilikili.ui.screens.openva

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import rj.kilikili.R
import rj.kilikili.priv.OpenVAHelper
import rj.kilikili.priv.syscall.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyscallFilterScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("过滤器规则", "系统调用", "日志", "统计")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("系统调用过滤器") },
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
        ) {
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Content
            when (selectedTab) {
                0 -> FilterRulesTab()
                1 -> SyscallListTab()
                2 -> SyscallLogsTab()
                3 -> SyscallStatsTab()
            }
        }
    }
}

@Composable
private fun FilterRulesTab() {
    val filterManager = OpenVAHelper.getSyscallFilterManager()
    val filters by remember { mutableStateOf(filterManager.getFilters()) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Default action selector
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "默认动作",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterPreset.entries.forEach { preset ->
                        Button(
                            onClick = {
                                val filter = filterManager.createPresetFilter(preset)
                                filterManager.clearFilters()
                                filterManager.registerFilter(filter)
                            },
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(preset.displayName)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Filters list
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "过滤器列表",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Button(onClick = { showAddDialog = true }) {
                        Text("添加过滤器")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (filters.isEmpty()) {
                    Text(
                        text = "暂无过滤器",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn {
                        items(filters) { filter ->
                            FilterItem(
                                filter = filter,
                                onDelete = {
                                    filterManager.removeFilter(filter)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterItem(
    filter: SyscallFilter,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = filter.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = filter.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "优先级: ${filter.priority}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Row {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun SyscallListTab() {
    val syscallTypes = OpenVAHelper.getAvailableSyscallTypes()
    val categories = SyscallCategory.entries
    var selectedCategory by remember { mutableStateOf<SyscallCategory?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Category selector
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "分类筛选",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn {
                    items(categories.chunked(3)) { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowCategories.forEach { category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = {
                                        selectedCategory = if (selectedCategory == category) null else category
                                    },
                                    label = { Text(category.displayName) }
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Syscall list
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "系统调用列表",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val filteredSyscalls = if (selectedCategory != null) {
                    syscallTypes.filter { it.category == selectedCategory }
                } else {
                    syscallTypes
                }
                
                LazyColumn {
                    items(filteredSyscalls) { syscall ->
                        SyscallItem(syscall = syscall)
                    }
                }
            }
        }
    }
}

@Composable
private fun SyscallItem(syscall: SyscallType) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = syscall.displayName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = syscall.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        FilterChip(
            selected = false,
            onClick = { /* TODO: Add to filter */ },
            label = { Text("添加") }
        )
    }
}

@Composable
private fun SyscallLogsTab() {
    val logger = OpenVAHelper.getSyscallLogger()
    val logs by remember { mutableStateOf(logger.getLogs()) }
    var showExportDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Controls
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "日志数量: ${logs.size}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row {
                    Button(onClick = { showExportDialog = true }) {
                        Text("导出")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { logger.clearLogs() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("清空")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Logs list
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                items(logs.sortedByDescending { it.timestamp }) { log ->
                    LogItem(log = log)
                }
            }
        }
    }
    
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("导出日志") },
            text = {
                Text("日志将导出为文本格式")
            },
            confirmButton = {
                Button(onClick = {
                    // TODO: Implement export
                    showExportDialog = false
                }) {
                    Text("导出")
                }
            },
            dismissButton = {
                Button(onClick = { showExportDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun LogItem(log: SyscallLogEntry) {
    val resultColor = when (log.result) {
        is FilterResult.Allow -> MaterialTheme.colorScheme.primary
        is FilterResult.Deny -> MaterialTheme.colorScheme.error
        is FilterResult.Redirect -> MaterialTheme.colorScheme.tertiary
        is FilterResult.Mock -> MaterialTheme.colorScheme.secondary
    }
    
    val resultText = when (log.result) {
        is FilterResult.Allow -> "允许"
        is FilterResult.Deny -> "拒绝"
        is FilterResult.Redirect -> "重定向"
        is FilterResult.Mock -> "模拟"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = log.syscallType.displayName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${log.packageName} (用户${log.userId})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = log.getFormattedTime(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = resultText,
            color = resultColor,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun SyscallStatsTab() {
    val logger = OpenVAHelper.getSyscallLogger()
    val stats by remember { mutableStateOf(logger.getStatistics()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Overall stats
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "总体统计",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "总调用",
                        value = stats.totalLogs.toString()
                    )
                    StatItem(
                        label = "允许",
                        value = stats.allowedCount.toString()
                    )
                    StatItem(
                        label = "拒绝",
                        value = stats.deniedCount.toString()
                    )
                    StatItem(
                        label = "重定向",
                        value = stats.redirectedCount.toString()
                    )
                    StatItem(
                        label = "模拟",
                        value = stats.mockedCount.toString()
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Category stats
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "按分类统计",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn {
                    items(stats.byCategory.entries.toList()) { (category, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(category.displayName)
                            Text(count.toString())
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Package stats
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "按包名统计",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn {
                    items(stats.byPackage.entries.toList()) { (packageName, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(packageName)
                            Text(count.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}