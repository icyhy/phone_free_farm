package com.phonefocusfarm.ui.screens.farm

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import com.phonefocusfarm.common.models.AnimalType
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.core.view.drawToBitmap
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import kotlinx.coroutines.delay

@Composable
fun FarmScreen(
    viewModel: FarmViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToFocus: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val animalCount by viewModel.animalCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val rootView = LocalView.current
    
    Scaffold(
        topBar = {
            FarmTopBar(
                onNavigateBack = onNavigateBack,
                onNavigateToFocus = onNavigateToFocus,
                onNavigateToStats = onNavigateToStats,
                onNavigateToSettings = onNavigateToSettings
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp), // 只保留水平内边距，移除垂直内边距
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp)
                )
            } else {
                // 最大化农场画布高度，适配屏幕
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)  // 占用最大可用空间
                        .onGloballyPositioned { coords ->
                            val pos = coords.localToRoot(androidx.compose.ui.geometry.Offset.Zero)
                            val size = coords.size
                            viewModel.updateFarmAreaBounds(android.graphics.Rect(pos.x.toInt(), pos.y.toInt(), (pos.x + size.width).toInt(), (pos.y + size.height).toInt()))
                            val bmp = rootView.drawToBitmap()
                            val rect = viewModel.farmAreaBounds.value
                            if (rect != null) {
                                val safeRect = android.graphics.Rect(
                                    rect.left.coerceAtLeast(0),
                                    rect.top.coerceAtLeast(0),
                                    rect.right.coerceAtMost(bmp.width),
                                    rect.bottom.coerceAtMost(bmp.height)
                                )
                                val width = (safeRect.right - safeRect.left).coerceAtLeast(1)
                                val height = (safeRect.bottom - safeRect.top).coerceAtLeast(1)
                                if (width > 1 && height > 1) {
                                    val cropped = android.graphics.Bitmap.createBitmap(bmp, safeRect.left, safeRect.top, width, height)
                                    viewModel.updateFarmSnapshot(cropped)
                                }
                            }
                        }
                ) {
                    FarmCanvas(viewModel, expanded = true)
                }
                
                Spacer(modifier = Modifier.height(12.dp)) // 减少农场与概览之间的间距
                
                // 简化的农场概览（保留但简化）
                FarmOverviewCompact(animalCount)
                
                // 移除底部的"开始专注时间"按钮
            }
        }
    }
}

@Composable
private fun FarmCanvas(viewModel: FarmViewModel, expanded: Boolean = false) {
    val animals by viewModel.animals.collectAsState()
    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }
    val worldWidth = canvasSize.width.toFloat()
    val worldHeight = canvasSize.height.toFloat()
    val emojiSizePx = with(LocalDensity.current) { 48.sp.toPx() }
    val positions = remember { mutableStateMapOf<String, Offset>() }
    val velocities = remember { mutableStateMapOf<String, Offset>() }
    val fleeTargets = remember { mutableStateMapOf<String, Offset?>() }
    
    // 仅在画布尺寸可用时初始化位置，避免空范围coerceIn崩溃
    if (worldWidth > 0f && worldHeight > 0f) {
        animals.forEach { a ->
            if (!positions.containsKey(a.id)) {
                val safeMargin = kotlin.math.max(0f, kotlin.math.min(60f, kotlin.math.min(worldWidth, worldHeight) / 4f))
                val centerX = worldWidth / 2f
                val centerY = worldHeight / 2f
                val maxRadius = kotlin.math.max(0f, kotlin.math.min(worldWidth, worldHeight) / 2f - safeMargin)

                val angle = Math.random().toFloat() * 2f * Math.PI.toFloat()
                val radius = if (maxRadius > 0f) safeMargin + Math.random().toFloat() * (maxRadius - safeMargin) else 0f
                val x = centerX + radius * kotlin.math.cos(angle).toFloat()
                val y = centerY + radius * kotlin.math.sin(angle).toFloat()

                val minX = safeMargin
                val maxX = kotlin.math.max(minX, worldWidth - safeMargin)
                val minY = safeMargin
                val maxY = kotlin.math.max(minY, worldHeight - safeMargin)

                positions[a.id] = Offset(x.coerceIn(minX, maxX), y.coerceIn(minY, maxY))
                velocities[a.id] = Offset(
                    (Math.random().toFloat() - 0.5f) * 2f,
                    (Math.random().toFloat() - 0.5f) * 2f
                )
            }
        }
    }
    LaunchedEffect(animals.size, canvasSize) {
        while (true) {
            delay(16)
            animals.forEach { a ->
                val v = velocities[a.id] ?: Offset(0f, 0f)
                val noise = (Math.random().toFloat() - 0.5f) * 0.6f
                val speed = when (a.type) {
                    AnimalType.CHICKEN, AnimalType.CHICKEN_RED, AnimalType.CHICKEN_FANCY -> 1.2f
                    AnimalType.CAT, AnimalType.CAT_TABBY, AnimalType.CAT_FAT -> 1.6f
                    AnimalType.DOG, AnimalType.DOG_BLACK, AnimalType.DOG_HUSKY -> 2.0f
                    else -> 1.0f
                }
                val p = positions[a.id] ?: Offset(
                    (Math.random().toFloat() * worldWidth),
                    (Math.random().toFloat() * worldHeight)
                )

                var vx = (v.x + noise).coerceIn(-speed, speed)
                var vy = (v.y + noise).coerceIn(-speed, speed)
                
                // 改进的分散算法：邻居距离内的排斥力 + 边界避让
                animals.forEach { b ->
                    if (b.id != a.id) {
                        val bp = positions[b.id] ?: p
                        val dir = p - bp
                        val dist = dir.getDistance()
                        if (dist < 100f && dist > 1f) {
                            val factor = (100f - dist) / 100f * 0.8f // 降低排斥力强度
                            vx += (dir.x / dist) * factor * speed
                            vy += (dir.y / dist) * factor * speed
                        }
                    }
                }
                
                // 边界避让：当动物靠近边界时，施加向中心的力
                val boundaryDistance = 40f
                val centerX = worldWidth / 2f
                val centerY = worldHeight / 2f
                
                if (p.x < boundaryDistance) {
                    vx += (boundaryDistance - p.x) / boundaryDistance * speed * 0.3f
                } else if (p.x > worldWidth - boundaryDistance) {
                    vx -= (p.x - (worldWidth - boundaryDistance)) / boundaryDistance * speed * 0.3f
                }
                
                if (p.y < boundaryDistance) {
                    vy += (boundaryDistance - p.y) / boundaryDistance * speed * 0.3f
                } else if (p.y > worldHeight - boundaryDistance) {
                    vy -= (p.y - (worldHeight - boundaryDistance)) / boundaryDistance * speed * 0.3f
                }

                fleeTargets[a.id]?.let { ft ->
                    val dir = p - ft
                    val len = kotlin.math.max(0.001f, dir.getDistance())
                    vx += (dir.x / len) * speed
                    vy += (dir.y / len) * speed
                    fleeTargets[a.id] = null
                }

                val nv = Offset(vx.coerceIn(-speed, speed), vy.coerceIn(-speed, speed))
                velocities[a.id] = nv
                val maxX = kotlin.math.max(0f, worldWidth - emojiSizePx)
                val maxY = kotlin.math.max(0f, worldHeight - emojiSizePx)
                var nx = (p.x + nv.x)
                var ny = (p.y + nv.y)
                if (nx < 0f) { nx = 0f; velocities[a.id] = Offset(kotlin.math.abs(nv.x), nv.y) }
                if (ny < 0f) { ny = 0f; velocities[a.id] = Offset(nv.x, kotlin.math.abs(nv.y)) }
                if (nx > maxX) { nx = maxX; velocities[a.id] = Offset(-kotlin.math.abs(nv.x), nv.y) }
                if (ny > maxY) { ny = maxY; velocities[a.id] = Offset(nv.x, -kotlin.math.abs(nv.y)) }
                positions[a.id] = Offset(nx, ny)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .onSizeChanged { canvasSize = it }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4CAF50)) // 草地背景色
                .drawBehind {
                    // 绘制草地纹理
                    val grassColor1 = Color(0xFF66BB6A)
                    val grassColor2 = Color(0xFF4CAF50)
                    
                    // 简单的草地纹理
                    for (y in 0..size.height.toInt() step 20) {
                        for (x in 0..size.width.toInt() step 20) {
                            val color = if ((x + y) % 40 == 0) grassColor1 else grassColor2
                            drawRect(
                                color = color,
                                topLeft = Offset(x.toFloat(), y.toFloat()),
                                size = Size(20f, 20f)
                            )
                        }
                    }
                    
                    // 绘制栅栏
                    val fenceColor = Color(0xFF8D6E63)
                    val fenceHeight = 8.dp.toPx()
                    
                    // 顶部栅栏
                    drawRect(
                        color = fenceColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, fenceHeight)
                    )
                    
                    // 底部栅栏
                    drawRect(
                        color = fenceColor,
                        topLeft = Offset(0f, size.height - fenceHeight),
                        size = Size(size.width, fenceHeight)
                    )
                    
                    // 左侧栅栏
                    drawRect(
                        color = fenceColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(fenceHeight, size.height)
                    )
                    
                    // 右侧栅栏
                    drawRect(
                        color = fenceColor,
                        topLeft = Offset(size.width - fenceHeight, 0f),
                        size = Size(fenceHeight, size.height)
                    )
                    
                    // 绘制栅栏柱子
                    val postWidth = 12.dp.toPx()
                    val postInterval = 60.dp.toPx()
                    
                    // 顶部柱子
                    for (x in 0..size.width.toInt() step postInterval.toInt()) {
                        drawRect(
                            color = fenceColor,
                            topLeft = Offset(x.toFloat(), 0f),
                            size = Size(postWidth, fenceHeight * 2)
                        )
                    }
                    
                    // 底部柱子
                    for (x in 0..size.width.toInt() step postInterval.toInt()) {
                        drawRect(
                            color = fenceColor,
                            topLeft = Offset(x.toFloat(), size.height - fenceHeight * 2),
                            size = Size(postWidth, fenceHeight * 2)
                        )
                    }
                }
                .pointerInput(animals.size) {
                    awaitPointerEventScope {
                        while (true) {
                            val down = awaitFirstDown()
                            val pos = down.position
                            animals.forEach { a ->
                                val p = positions[a.id] ?: Offset(0f, 0f)
                                if ((p - pos).getDistance() < 24f) {
                                    fleeTargets[a.id] = pos
                                }
                            }
                        }
                    }
                }
        ) {
            animals.filter { 
                it.type in listOf(
                    AnimalType.CHICKEN,
                    AnimalType.CAT,
                    AnimalType.DOG
                )
            }.forEach { a ->
                if (!positions.containsKey(a.id) && worldWidth > 0f && worldHeight > 0f) {
                    val safeMargin = kotlin.math.max(0f, kotlin.math.min(60f, kotlin.math.min(worldWidth, worldHeight) / 4f))
                    val minX = safeMargin
                    val maxX = kotlin.math.max(minX, worldWidth - safeMargin)
                    val minY = safeMargin
                    val maxY = kotlin.math.max(minY, worldHeight - safeMargin)
                    positions[a.id] = Offset(
                        (minX + Math.random().toFloat() * (maxX - minX)),
                        (minY + Math.random().toFloat() * (maxY - minY))
                    )
                }
                val p = positions[a.id] ?: return@forEach
                val emoji = when (a.type) {
                    AnimalType.CHICKEN -> "🐥"
                    AnimalType.CHICKEN_RED -> "🐓"
                    AnimalType.CHICKEN_FANCY -> "🦃"
                    AnimalType.CAT -> "🐱"
                    AnimalType.CAT_TABBY -> "🐈"
                    AnimalType.CAT_FAT -> "🙀"
                    AnimalType.DOG -> "🐶"
                    AnimalType.DOG_BLACK -> "🐕"
                    AnimalType.DOG_HUSKY -> "🦮"
                    else -> "🐥"
                }
                Text(
                    text = emoji,
                    fontSize = 48.sp,
                    modifier = Modifier
                        .offset { IntOffset(p.x.toInt(), p.y.toInt()) }
                )
            }
        }
    }
}

private fun Offset.getDistance(): Float {
    return kotlin.math.sqrt(x * x + y * y)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FarmTopBar(
    onNavigateBack: () -> Unit,
    onNavigateToFocus: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    TopAppBar(
        title = { 
            Text("我的农场")
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回"
                )
            }
        },
        actions = {
            // 设置按钮
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // 专注按钮
            IconButton(
                onClick = onNavigateToFocus,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "专注",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onNavigateToStats) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "统计",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
private fun FarmOverview(animalCount: Map<AnimalType, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "农场概览",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnimalCountItem("🐔", animalCount[AnimalType.CHICKEN] ?: 0, "鸡")
                AnimalCountItem("🐱", animalCount[AnimalType.CAT] ?: 0, "猫")
                AnimalCountItem("🐶", animalCount[AnimalType.DOG] ?: 0, "狗")
            }
        }
    }
}

@Composable
private fun FarmOverviewCompact(animalCount: Map<AnimalType, Int>) {
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
            // 统计所有鸡类动物
            val chickenCount = (animalCount[AnimalType.CHICKEN] ?: 0) + 
                             (animalCount[AnimalType.CHICKEN_RED] ?: 0) + 
                             (animalCount[AnimalType.CHICKEN_FANCY] ?: 0)
            
            // 统计所有猫类动物
            val catCount = (animalCount[AnimalType.CAT] ?: 0) + 
                         (animalCount[AnimalType.CAT_TABBY] ?: 0) + 
                         (animalCount[AnimalType.CAT_FAT] ?: 0)
            
            // 统计所有狗类动物
            val dogCount = (animalCount[AnimalType.DOG] ?: 0) + 
                         (animalCount[AnimalType.DOG_BLACK] ?: 0) + 
                         (animalCount[AnimalType.DOG_HUSKY] ?: 0)
            
            AnimalCountItemCompact("🐔", chickenCount, "鸡")
            AnimalCountItemCompact("🐱", catCount, "猫")
            AnimalCountItemCompact("🐶", dogCount, "狗")
        }
    }
}

@Composable
private fun AnimalCountItem(emoji: String, count: Int, name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 32.sp
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AnimalCountItemCompact(emoji: String, count: Int, name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineSmall  // 减小图标大小
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,  // 减小数字大小
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AnimalList(animalCount: Map<AnimalType, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "动物详情",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            animalCount.forEach { (animalType, count) ->
                AnimalItem(animalType, count)
            }
        }
    }
}

@Composable
private fun AnimalItem(animalType: AnimalType, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = animalType.emoji,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(end = 16.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = animalType.displayName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "数量: $count",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}