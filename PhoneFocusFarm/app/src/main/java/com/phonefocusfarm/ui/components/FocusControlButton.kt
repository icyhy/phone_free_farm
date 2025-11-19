package com.phonefocusfarm.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
 
import com.phonefocusfarm.common.models.TimerState

@Composable
fun FocusControlButton(
    timerState: TimerState,
    onStartFocus: () -> Unit,
    onStopFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (timerState) {
        is TimerState.Idle -> {
            Button(
                onClick = onStartFocus,
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "开始专注",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        is TimerState.Incubating -> {
            Button(
                onClick = onStopFocus,
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(
                    text = "停止专注",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        is TimerState.Completed -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onStartFocus,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "再次专注",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                OutlinedButton(
                    onClick = { /* TODO: 分享成就 */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(
                        text = "分享成就",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        is TimerState.Interrupted -> {
            Button(
                onClick = onStartFocus,
                modifier = modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "重新开始",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        is TimerState.Paused -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onStartFocus,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "继续专注",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                OutlinedButton(
                    onClick = onStopFocus,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(
                        text = "结束专注",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// 模式选择已移除