package com.phonefocusfarm.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.phonefocusfarm.common.models.TimerState

@Composable
fun FocusTips(
    timerState: TimerState,
    isTestMode: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (timerState) {
                is TimerState.Idle -> {
                    IdleTips(isTestMode)
                }
                is TimerState.Incubating -> {
                    IncubatingTips(timerState, isTestMode)
                }
                is TimerState.Interrupted -> {
                    InterruptedTips(timerState)
                }
                is TimerState.Completed -> {
                    CompletedTips(timerState)
                }
                is TimerState.Paused -> {
                    PausedTips(timerState)
                }
            }
        }
    }
}

@Composable
private fun PausedTips(state: TimerState.Paused) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⏸️ 专注暂停",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "专注已暂停，点击继续按钮恢复专注",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun IdleTips(isTestMode: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "💡 专注提示",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (isTestMode) {
            Text(
                text = "测试模式已启用，孵化时间缩短为10/20/30秒",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            Text(
                text = "• 15分钟无操作 → 孵化小鸡\n" +
                       "• 30分钟无操作 → 升级小猫\n" +
                       "• 60分钟无操作 → 升级小狗",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun IncubatingTips(
    state: TimerState.Incubating,
    isTestMode: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎯 保持专注",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (isTestMode) {
            Text(
                text = "测试模式：保持手机静止，避免触摸屏幕",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "保持手机静止，避免触摸屏幕\n" +
                       "离开应用或设备移动会导致专注中断",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
        
        state.currentAnimal?.let { animal ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "即将孵化: ${animal.displayName} ${animal.emoji}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InterruptedTips(state: TimerState.Interrupted) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⏹️ 专注中断",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "专注时间: ${formatTime(state.duration)}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Text(
            text = "中断原因: ${state.reason.displayName}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (state.duration < 15 * 60 * 1000) { // 少于15分钟
            Text(
                text = "提示：下次专注时间达到15分钟可获得奖励",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun CompletedTips(state: TimerState.Completed) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎉 专注完成",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "专注时间: ${formatTime(state.duration)}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Text(
                text = "获得奖励: ${state.result.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        
        if (state.result == com.phonefocusfarm.common.models.IncubationResult.SUCCESS) {
            Text(
                text = "恭喜！新的动物已添加到您的农场",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}