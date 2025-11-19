package com.phonefocusfarm.ui.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import com.phonefocusfarm.ui.screens.farm.FarmViewModel
import com.phonefocusfarm.feature.share.ShareUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val totalFocusTime by viewModel.totalFocusTime.collectAsState()
    val successfulCount by viewModel.successfulCount.collectAsState()
    val longestFocus by viewModel.longestFocus.collectAsState()
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("统计") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "总专注时长: ${formatDuration(totalFocusTime)}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "成功次数: $successfulCount")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "最长单次: ${formatDuration(longestFocus)}")
                    Spacer(modifier = Modifier.height(16.dp))
                    val farmVm: FarmViewModel = hiltViewModel()
                    val animalCount by farmVm.animalCount.collectAsState()
                    val farmShot by farmVm.latestSnapshot.collectAsState()
                    Button(onClick = {
                        val summary = "总专注时长: ${formatDuration(totalFocusTime)}"
                        val bmp = farmShot?.let {
                            ShareUtil.buildFarmPosterWithImage(
                                context,
                                "我的农场",
                                summary,
                                it
                            )
                        } ?: ShareUtil.buildFarmPoster(
                            context,
                            "我的农场",
                            summary,
                            animalCount
                        )
                        val uri = ShareUtil.saveBitmap(context, bmp)
                        ShareUtil.shareToWeChatOrSystem(context, uri)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("分享农场海报")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            val sessions by viewModel.recentSessions.collectAsState()
            val grouped = remember(sessions) {
                sessions.groupBy { dayKey(it.startTime) }.toSortedMap(compareByDescending { it })
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                grouped.forEach { (day, list) ->
                    item {
                        Surface(color = MaterialTheme.colorScheme.surface) {
                            Text(text = formatDay(day), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                    items(list) { s ->
                        RecordCard(s)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                if (grouped.isEmpty()) {
                    item { Text(text = "暂无记录") }
                }
            }
        }
    }
}

@Composable
private fun RecordCard(s: com.phonefocusfarm.core.data.entity.IncubationSessionEntity) {
    var expanded by remember { mutableStateOf(false) }
    val vm: StatsViewModel = hiltViewModel()
    Card(modifier = Modifier.fillMaxWidth(), onClick = { expanded = !expanded }) {
        Column(modifier = Modifier.padding(12.dp)) {
            val timeStr = java.text.SimpleDateFormat("HH:mm").format(java.util.Date(s.startTime))
            Text(text = "开始: $timeStr  时长: ${formatDuration(s.duration)}")
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = {}, label = { Text(text = s.mode.displayName) })
                Spacer(modifier = Modifier.width(8.dp))
                val resultColor = when (s.result) {
                    com.phonefocusfarm.common.models.IncubationResult.SUCCESS -> MaterialTheme.colorScheme.primary
                    com.phonefocusfarm.common.models.IncubationResult.INTERRUPTED -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                }
                AssistChip(onClick = {}, label = { Text(text = s.result.displayName, color = resultColor) })
            }
            s.interruptionReason?.let { reason ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "中断: ${reason.displayName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            s.animalGenerated?.let { animal ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "奖励: ${animal.displayName}", style = MaterialTheme.typography.bodySmall)
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { /* todo: share single record */ }) { Text("分享") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { /* todo: add remark */ }) { Text("备注") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { vm.deleteSession(s.id) }) { Text("删除") }
                }
            }
        }
    }
}

private fun dayKey(ts: Long): String = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date(ts))
private fun formatDay(day: String): String = day

private fun formatDuration(ms: Long): String {
    val s = ms / 1000
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return String.format("%02d:%02d:%02d", h, m, sec)
}