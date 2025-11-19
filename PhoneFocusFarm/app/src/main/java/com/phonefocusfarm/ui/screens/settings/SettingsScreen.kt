package com.phonefocusfarm.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import com.phonefocusfarm.common.models.CycleType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val stageDuration by viewModel.stageDuration.collectAsState()
    val cycleType by viewModel.cycleType.collectAsState()
    val showResetDialog by viewModel.showResetDialog.collectAsState()
    val allowPause by viewModel.allowPause.collectAsState()
    val isTestMode by viewModel.isTestMode.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "测试模式（10/20/30秒）", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = isTestMode, onCheckedChange = { viewModel.setTestMode(it) })
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "允许暂停", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = allowPause, onCheckedChange = { viewModel.setAllowPause(it) })
                    }
                }
            }

            // 动物升级设置
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "动物升级设置",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 升级阶段时长
                    Text(
                        text = "升级阶段时长: ${stageDuration / 60000} 分钟",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Slider(
                        value = (stageDuration / 60000).toFloat(),
                        onValueChange = { viewModel.setStageDuration((it * 60000).toLong()) },
                        valueRange = 5f..60f, // 5分钟到60分钟
                        steps = 10
                    )
                    
                    Text(
                        text = "每个动物阶段升级的间隔时间",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 周期设置
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "农场周期设置",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 周期类型选择
                    Text(
                        text = "重置周期",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CycleTypeOption(
                            type = CycleType.DAILY,
                            selected = cycleType == CycleType.DAILY,
                            onClick = { viewModel.setCycleType(CycleType.DAILY) }
                        )
                        CycleTypeOption(
                            type = CycleType.WEEK,
                            selected = cycleType == CycleType.WEEK,
                            onClick = { viewModel.setCycleType(CycleType.WEEK) }
                        )
                        CycleTypeOption(
                            type = CycleType.MONTH,
                            selected = cycleType == CycleType.MONTH,
                            onClick = { viewModel.setCycleType(CycleType.MONTH) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "周期结束后农场将自动重置，动物将被清零",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 重置按钮
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "手动重置",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "立即重置当前农场，保存周期记录并清空所有动物",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Button(
                        onClick = { viewModel.showResetDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("重置农场")
                    }
                }
            }
        }
        
        // 重置确认对话框
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideResetDialog() },
                title = { Text("确认重置农场") },
                text = { 
                    Text("确定要重置农场吗？这将保存当前周期的记录并清空所有动物，此操作不可撤销。")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetFarm()
                            viewModel.hideResetDialog()
                        }
                    ) {
                        Text("确认", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideResetDialog() }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
private fun CycleTypeOption(
    type: CycleType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        modifier = Modifier.width(80.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = type.displayName,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}