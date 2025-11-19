package com.phonefocusfarm.ui.screens.focus

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.activity.compose.BackHandler
import com.phonefocusfarm.common.models.TimerState
import com.phonefocusfarm.ui.components.FocusControlButton
import com.phonefocusfarm.ui.components.TimerDisplay
import com.phonefocusfarm.ui.components.FocusTips
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Schedule
import com.phonefocusfarm.ui.screens.farm.FarmViewModel
import com.phonefocusfarm.common.models.AnimalType
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun FocusScreen(
    viewModel: FocusViewModel = hiltViewModel(),
    onNavigateToFarm: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    
    val focusTips by viewModel.focusTips.collectAsState()
    val isTestMode by viewModel.isTestMode.collectAsState()
    val showResetDialog by viewModel.showResetDialog.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    Scaffold(
        topBar = {
            FocusTopBar(onNavigateToFarm = onNavigateToFarm)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .pointerInput(timerState) {
                    if (timerState is TimerState.Incubating) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitFirstDown()
                                viewModel.stopFocus()
                            }
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 周期概览
            CycleOverview()
            // 与动物概览保持合适间距（Material 3建议 16dp 间距）
            Spacer(modifier = Modifier.height(16.dp))

            HomeFarmOverview(
                timerState = timerState,
                isTestMode = isTestMode,
                currentTimeSec = currentTime
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 测试模式开关已移动到设置页
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 计时器显示
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatTime(currentTime),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = when (timerState) {
                            is TimerState.Idle -> "准备开始专注"
                            is TimerState.Incubating -> "专注中..."
                            is TimerState.Paused -> "暂停中"
                            is TimerState.Interrupted -> "已中断"
                            is TimerState.Completed -> "专注完成！"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 控制按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val settingsVm: com.phonefocusfarm.ui.screens.settings.SettingsViewModel = hiltViewModel()
                val allowPause by settingsVm.allowPause.collectAsState()
                when (timerState) {
                    is TimerState.Idle -> {
                        Button(
                            onClick = { viewModel.startTimer() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("开始专注")
                        }
                    }
                    is TimerState.Incubating -> {
                        if (allowPause) {
                            Button(
                                onClick = { viewModel.pauseTimer() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("暂停")
                            }
                        }
                    }
                    is TimerState.Paused -> {
                        if (allowPause) {
                            Button(
                                onClick = { viewModel.resumeTimer() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("继续")
                            }
                        }
                    }
                    is TimerState.Interrupted -> {
                        Button(
                            onClick = { viewModel.resumeTimer() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("继续")
                        }
                    }
                    is TimerState.Completed -> {
                        Button(
                            onClick = { viewModel.startTimer() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("再次专注")
                        }
                    }
                }
                
                if (timerState !is TimerState.Idle && timerState !is TimerState.Completed) {
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.showResetDialog() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("重置")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            FocusTips(timerState = timerState, isTestMode = isTestMode, modifier = Modifier.fillMaxWidth())
        }
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.setFocusVisible(true)
                Lifecycle.Event.ON_STOP -> viewModel.setFocusVisible(false)
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    if (timerState is TimerState.Incubating) {
        androidx.activity.compose.BackHandler(true) {
            viewModel.stopFocus()
        }
    }
    // 保持自然模式仅在应用切至后台或切换到其他应用时中断，由系统级监控处理
    
    // 重置确认对话框
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideResetDialog() },
            title = { Text("确认重置") },
            text = { 
                Text("确定要重置当前专注时间吗？这将清空当前周期的所有动物并保存周期记录。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetTimer()
                        viewModel.hideResetDialog()
                    }
                ) {
                    Text("确认")
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

@Composable
private fun HomeFarmOverview(
    vm: FarmViewModel = hiltViewModel(),
    timerState: TimerState? = null,
    isTestMode: Boolean = false,
    currentTimeSec: Int = 0
) {
    val animalCount by vm.animalCount.collectAsState()
    val settingsVm: com.phonefocusfarm.ui.screens.settings.SettingsViewModel = hiltViewModel()
    val stageDuration by settingsVm.stageDuration.collectAsState()
    val predicted = remember(timerState, isTestMode, currentTimeSec, stageDuration) {
        val base = if (isTestMode) 10_000L else stageDuration
        val thresholds = Triple(base, base * 2, base * 3)
        val elapsedMs = if (timerState is TimerState.Incubating) (currentTimeSec * 1000L) else 0L
        var remaining = elapsedMs
        val dog = (remaining / thresholds.third).toInt()
        remaining %= thresholds.third
        val cat = (remaining / thresholds.second).toInt()
        remaining %= thresholds.second
        val chicken = (remaining / thresholds.first).toInt()
        mapOf(
            AnimalType.DOG to dog,
            AnimalType.CAT to cat,
            AnimalType.CHICKEN to chicken
        )
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val chickenCount = (animalCount[AnimalType.CHICKEN] ?: 0) +
                (animalCount[AnimalType.CHICKEN_RED] ?: 0) +
                (animalCount[AnimalType.CHICKEN_FANCY] ?: 0)
            val catCount = (animalCount[AnimalType.CAT] ?: 0) +
                (animalCount[AnimalType.CAT_TABBY] ?: 0) +
                (animalCount[AnimalType.CAT_FAT] ?: 0)
            val dogCount = (animalCount[AnimalType.DOG] ?: 0) +
                (animalCount[AnimalType.DOG_BLACK] ?: 0) +
                (animalCount[AnimalType.DOG_HUSKY] ?: 0)
            OverviewItem("🐔", chickenCount + (predicted[AnimalType.CHICKEN] ?: 0), "鸡")
            OverviewItem("🐱", catCount + (predicted[AnimalType.CAT] ?: 0), "猫")
            OverviewItem("🐶", dogCount + (predicted[AnimalType.DOG] ?: 0), "狗")
        }
    }
}

@Composable
private fun CycleOverview(vm: CycleOverviewViewModel = hiltViewModel()) {
    val start by vm.cycleStart.collectAsState()
    val end by vm.cycleEnd.collectAsState()
    val duration by vm.cycleDuration.collectAsState()
    // 仅显示日期（不含时分）
    val fmt = remember { SimpleDateFormat("yyyy-MM-dd") }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题 + 简洁图标
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "农场周期",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "当前农场周期",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            // 日期范围（仅日期），更简洁的排版
            val startStr = start?.let { fmt.format(Date(it)) } ?: "--"
            val endStr = end?.let { fmt.format(Date(it)) } ?: "进行中"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = startStr,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = " 至 ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
                Text(
                    text = endStr,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // 累计专注时长胶囊
            AssistChip(
                onClick = {},
                label = {
                    Text(text = "累计专注 ${formatDurationMs(duration)}")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}

private fun formatDurationMs(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val secs = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}

@Composable
private fun OverviewItem(emoji: String, count: Int, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, style = MaterialTheme.typography.headlineMedium)
        Text(text = "$count", style = MaterialTheme.typography.bodyLarge)
        Text(text = name, style = MaterialTheme.typography.bodySmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FocusTopBar(onNavigateToFarm: () -> Unit) {
    TopAppBar(
        title = { Text("专注农场") },
        actions = {
            IconButton(onClick = onNavigateToFarmWrapper(onNavigateToFarm)) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = "农场"
                )
            }
        }
    )
}

@Composable
private fun onNavigateToFarmWrapper(onNavigateToFarm: () -> Unit): () -> Unit {
    val vm: FocusViewModel = hiltViewModel()
    return {
        vm.stopFocus()
        onNavigateToFarm()
    }
}

// 已移除模式选项组件

private fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}