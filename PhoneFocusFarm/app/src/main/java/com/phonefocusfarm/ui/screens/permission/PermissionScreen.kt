package com.phonefocusfarm.ui.screens.permission

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phonefocusfarm.core.permission.PermissionType
import com.phonefocusfarm.core.permission.PermissionStatus
import com.phonefocusfarm.core.permission.HuaweiPermissionHelper
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun PermissionScreen(
    viewModel: PermissionViewModel = hiltViewModel(),
    onAllPermissionsGranted: () -> Unit,
    onRequestPermission: (PermissionType, (Boolean) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val permissionStatus by viewModel.permissionStatus.collectAsState()
    val missingPermissions by viewModel.missingPermissions.collectAsState()
    val isHuaweiDevice by viewModel.isHuaweiDevice.collectAsState()
    
    // 创建华为权限助手
    val huaweiPermissionHelper = remember { HuaweiPermissionHelper(context) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("权限设置") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 华为设备特殊提示
            if (isHuaweiDevice) {
                HuaweiBatteryCard {
                    // 显示华为设置指南
                    huaweiPermissionHelper.showHuaweiBatteryOptimizationGuide(context as android.app.Activity)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 权限说明
            PermissionExplanation()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 权限列表
            PermissionList(
                permissionStatus = permissionStatus,
                missingPermissions = missingPermissions,
                onPermissionClick = { permission ->
                    onRequestPermission(permission) { isGranted ->
                        if (isGranted) {
                            viewModel.refreshPermissionStatus()
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 继续按钮
            if (missingPermissions.isEmpty()) {
                Button(
                    onClick = onAllPermissionsGranted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("开始使用")
                }
            } else {
                OutlinedButton(
                    onClick = { viewModel.refreshPermissionStatus() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("刷新权限状态")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionExplanation() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "专注农场需要以下权限来正常工作",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "这些权限仅用于确保专注计时的准确性，不会收集您的个人信息",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionList(
    permissionStatus: PermissionStatus,
    missingPermissions: List<PermissionType>,
    onPermissionClick: (PermissionType) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(PermissionType.values().toList()) { permissionType ->
            val isGranted = when (permissionType) {
                PermissionType.USAGE_STATS -> permissionStatus.usageStats
                PermissionType.NOTIFICATIONS -> permissionStatus.notifications
                PermissionType.BATTERY_OPTIMIZATION -> permissionStatus.batteryOptimization
                PermissionType.FOREGROUND_SERVICE -> permissionStatus.foregroundService
                PermissionType.WAKE_LOCK -> permissionStatus.wakeLock
            }
            
            PermissionItem(
                permissionType = permissionType,
                isGranted = isGranted,
                onClick = { onPermissionClick(permissionType) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionItem(
    permissionType: PermissionType,
    isGranted: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (!isGranted) onClick else ({}),
        enabled = !isGranted
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 权限图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isGranted) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isGranted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 权限信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = getPermissionTitle(permissionType),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = getPermissionDescription(permissionType),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 状态图标
            if (!isGranted) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HuaweiBatteryCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "华为设备特殊设置",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "点击此处查看华为Mate 40 Pro的电池优化设置指南",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

private fun getPermissionTitle(permissionType: PermissionType): String {
    return when (permissionType) {
        PermissionType.USAGE_STATS -> "应用使用统计"
        PermissionType.NOTIFICATIONS -> "通知权限"
        PermissionType.BATTERY_OPTIMIZATION -> "电池优化"
        PermissionType.FOREGROUND_SERVICE -> "前台服务"
        PermissionType.WAKE_LOCK -> "唤醒锁"
    }
}

private fun getPermissionDescription(permissionType: PermissionType): String {
    return when (permissionType) {
        PermissionType.USAGE_STATS -> "用于检测您是否切换到其他应用"
        PermissionType.NOTIFICATIONS -> "用于显示专注计时通知"
        PermissionType.BATTERY_OPTIMIZATION -> "用于在后台保持计时器运行"
        PermissionType.FOREGROUND_SERVICE -> "用于在前台运行专注服务"
        PermissionType.WAKE_LOCK -> "用于保持设备唤醒状态"
    }
}