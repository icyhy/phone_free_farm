# 手机专注农场应用 - UI/UX设计规范

## 1. 设计原则

### 1.1 核心设计原则
- **简洁性**：界面简洁明了，减少视觉干扰
- **生动性**：色彩明快，动画流畅，增强趣味性
- **沉浸感**：3D农场场景提供沉浸式体验
- **一致性**：统一的视觉语言和交互模式
- **可访问性**：支持深色模式，适配不同视力需求

### 1.2 用户体验目标
- **减少认知负担**：直观的操作流程
- **即时反馈**：用户操作有明确的视觉和音效反馈
- **正向激励**：通过视觉和音效强化专注行为
- **个性化**：支持主题和偏好设置

## 2. 视觉设计规范

### 2.1 色彩系统

#### 主色调
```kotlin
// 主色彩定义
object AppColors {
    // 主品牌色
    val Primary = Color(0xFF4CAF50)        // 绿色 - 代表农场和自然
    val PrimaryVariant = Color(0xFF388E3C) // 深绿色
    
    // 辅助色
    val Secondary = Color(0xFFFFA726)      // 橙色 - 温暖和活力
    val SecondaryVariant = Color(0xFFFF9800) // 深橙色
    
    // 动物颜色
    val Chicken = Color(0xFFFFEB3B)         // 小鸡 - 明黄色
    val Cat = Color(0xFFFF6F00)             // 小猫 - 橙黄色
    val Dog = Color(0xFF8D6E63)             // 小狗 - 棕褐色
    
    // 状态色
    val Success = Color(0xFF4CAF50)         // 成功 - 绿色
    val Warning = Color(0xFFFF9800)         // 警告 - 橙色
    val Error = Color(0xFFF44336)           // 错误 - 红色
    val Info = Color(0xFF2196F3)            // 信息 - 蓝色
    
    // 背景色
    val Background = Color(0xFFF5F5F5)      // 浅灰色背景
    val Surface = Color(0xFFFFFFFF)       // 白色表面
    
    // 文字色
    val OnPrimary = Color(0xFFFFFFFF)       // 主色上的白色文字
    val OnSecondary = Color(0xFF000000)     // 辅助色上的黑色文字
    val OnBackground = Color(0xFF212121)    // 背景上的深灰文字
    val OnSurface = Color(0xFF424242)       // 表面上的中灰文字
}
```

#### 深色模式色彩
```kotlin
object DarkAppColors {
    val Primary = Color(0xFF81C784)         // 浅绿色
    val PrimaryVariant = Color(0xFF66BB6A)   // 中绿色
    val Secondary = Color(0xFFFFB74D)       // 浅橙色
    val SecondaryVariant = Color(0xFFFFA726) // 中橙色
    
    val Background = Color(0xFF121212)      // 深灰色背景
    val Surface = Color(0xFF1E1E1E)       // 稍浅的表面
    val OnBackground = Color(0xFFE0E0E0)     // 浅灰文字
    val OnSurface = Color(0xFFBDBDBD)      // 中灰文字
}
```

### 2.2 字体系统

#### 字体家族
```kotlin
object AppTypography {
    // 标题
    val H1 = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )
    
    val H2 = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )
    
    val H3 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp,
        letterSpacing = 0.15.sp
    )
    
    // 正文
    val BodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    
    val BodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    
    val BodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    
    // 按钮
    val Button = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 1.25.sp
    )
    
    // 标签
    val LabelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    
    val LabelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
}
```

### 2.3 间距系统

```kotlin
object AppSpacing {
    val XS = 4.dp    // 超小间距
    val SM = 8.dp    // 小间距
    val MD = 16.dp   // 中间距
    val LG = 24.dp   // 大间距
    val XL = 32.dp   // 超大间距
    val XXL = 48.dp  // 特大间距
}
```

### 2.4 圆角和阴影

```kotlin
object AppShapes {
    val Small = RoundedCornerShape(4.dp)
    val Medium = RoundedCornerShape(8.dp)
    val Large = RoundedCornerShape(16.dp)
    val ExtraLarge = RoundedCornerShape(24.dp)
    val Circle = CircleShape
}

object AppElevation {
    val Level0 = 0.dp
    val Level1 = 2.dp
    val Level2 = 4.dp
    val Level3 = 8.dp
    val Level4 = 12.dp
    val Level5 = 16.dp
}
```

## 3. 界面原型设计

### 3.1 主界面（专注页面）

```kotlin
@Composable
fun FocusScreen(
    timerState: TimerState,
    onStartFocus: () -> Unit,
    onStopFocus: () -> Unit,
    onNavigateToFarm: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(AppSpacing.MD),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 顶部导航栏
        FocusTopBar(
            onNavigateToFarm = onNavigateToFarm,
            onNavigateToStats = onNavigateToStats
        )
        
        // 主要内容区域
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            // 计时器显示
            TimerDisplay(timerState)
            
            Spacer(modifier = Modifier.height(AppSpacing.XL))
            
            // 控制按钮
            FocusControlButton(
                timerState = timerState,
                onStartFocus = onStartFocus,
                onStopFocus = onStopFocus
            )
            
            Spacer(modifier = Modifier.height(AppSpacing.LG))
            
            // 模式选择
            FocusModeSelector()
            
            Spacer(modifier = Modifier.height(AppSpacing.XL))
            
            // 提示信息
            FocusTips(timerState)
        }
        
        // 底部状态栏
        FocusStatusBar(timerState)
    }
}
```

### 3.2 农场界面

```kotlin
@Composable
fun FarmScreen(
    animals: List<Animal>,
    onAnimalClick: (Animal) -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 3D农场背景
        FarmBackground()
        
        // 动物渲染层
        AnimalLayer(
            animals = animals,
            onAnimalClick = onAnimalClick
        )
        
        // 顶部导航栏
        FarmTopBar(
            onNavigateBack = onNavigateBack,
            animalCount = animals.size
        )
        
        // 互动效果层
        InteractionEffectLayer()
        
        // 动物信息面板（可选）
        AnimalInfoPanel()
    }
}
```

### 3.3 统计界面

```kotlin
@Composable
fun StatisticsScreen(
    currentCycle: Cycle,
    historicalCycles: List<Cycle>,
    achievements: List<Achievement>,
    onNavigateBack: () -> Unit,
    onShareAchievement: (Achievement) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 顶部导航栏
        StatsTopBar(
            onNavigateBack = onNavigateBack,
            onShare = { /* 分享战绩 */ }
        )
        
        // 统计内容
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(AppSpacing.MD),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.MD)
        ) {
            // 当前周期概览
            item {
                CurrentCycleCard(currentCycle)
            }
            
            // 图表区域
            item {
                ChartsSection(historicalCycles)
            }
            
            // 成就区域
            item {
                AchievementsSection(
                    achievements = achievements,
                    onShareAchievement = onShareAchievement
                )
            }
            
            // 详细数据
            items(historicalCycles) { cycle ->
                CycleHistoryItem(cycle)
            }
        }
    }
}
```

## 4. 组件设计规范

### 4.1 按钮组件

```kotlin
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonType: ButtonType = ButtonType.Primary,
    size: ButtonSize = ButtonSize.Medium
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(size.height)
            .then(
                when (size) {
                    ButtonSize.Small -> Modifier.widthIn(min = 80.dp)
                    ButtonSize.Medium -> Modifier.widthIn(min = 120.dp)
                    ButtonSize.Large -> Modifier.fillMaxWidth()
                }
            ),
        enabled = enabled,
        colors = when (buttonType) {
            ButtonType.Primary -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
            ButtonType.Secondary -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
            ButtonType.Outlined -> ButtonDefaults.outlinedButtonColors()
        },
        shape = AppShapes.Medium,
        contentPadding = PaddingValues(horizontal = AppSpacing.MD)
    ) {
        Text(
            text = text,
            style = AppTypography.Button
        )
    }
}
```

### 4.2 卡片组件

```kotlin
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    elevation: Dp = AppElevation.Level1,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = AppShapes.Large,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Box(
            modifier = Modifier.padding(AppSpacing.MD)
        ) {
            content()
        }
    }
}
```

### 4.3 进度条组件

```kotlin
@Composable
fun FocusProgressBar(
    progress: Float,
    targetTime: Long,
    currentTime: Long,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 进度条
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        
        Spacer(modifier = Modifier.height(AppSpacing.XS))
        
        // 时间显示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentTime),
                style = AppTypography.BodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatTime(targetTime),
                style = AppTypography.BodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

## 5. 动画设计规范

### 5.1 过渡动画

```kotlin
object AppAnimations {
    // 默认过渡动画
    val DefaultTransition = tween<Float>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
    
    // 快速过渡
    val QuickTransition = tween<Float>(
        durationMillis = 150,
        easing = LinearOutSlowInEasing
    )
    
    // 缓慢过渡
    val SlowTransition = tween<Float>(
        durationMillis = 600,
        easing = FastOutSlowInEasing
    )
    
    // 弹性动画
    val BouncyAnimation = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
}
```

### 5.2 动物动画

```kotlin
@Composable
fun AnimalAnimation(
    animal: Animal,
    onAnimationEnd: () -> Unit
) {
    // 入场动画
    val enterTransition = scaleIn(
        animationSpec = AppAnimations.BouncyAnimation,
        initialScale = 0.5f
    ) + fadeIn(
        animationSpec = AppAnimations.DefaultTransition
    )
    
    // 点击反馈动画
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.2f else 1f,
        animationSpec = AppAnimations.QuickTransition,
        label = "animal_scale"
    )
    
    AnimatedVisibility(
        visible = animal.isVisible,
        enter = enterTransition,
        exit = fadeOut()
    ) {
        AnimalView(
            animal = animal,
            modifier = Modifier
                .scale(scale)
                .clickable {
                    isPressed = true
                    // 播放音效
                    playAnimalSound(animal.type)
                    // 延迟重置状态
                    Handler(Looper.getMainLooper()).postDelayed({
                        isPressed = false
                    }, 200)
                }
        )
    }
}
```

## 6. 响应式设计

### 6.1 屏幕适配

```kotlin
object ScreenBreakpoints {
    val Compact = 360.dp   // 手机竖屏
    val Medium = 600.dp   // 手机横屏/小平板
    val Expanded = 840.dp // 平板
}

@Composable
fun <T> rememberResponsiveLayout(
    compact: T,
    medium: T,
    expanded: T
): T {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    return remember(screenWidth) {
        when {
            screenWidth < ScreenBreakpoints.Medium -> compact
            screenWidth < ScreenBreakpoints.Expanded -> medium
            else -> expanded
        }
    }
}
```

### 6.2 自适应布局

```kotlin
@Composable
fun ResponsiveFocusScreen() {
    val isTablet = rememberResponsiveLayout(
        compact = false,
        medium = true,
        expanded = true
    )
    
    if (isTablet) {
        // 平板布局 - 并排显示
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimerDisplay()
                FocusControlButton()
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                FarmPreview()
                StatisticsPreview()
            }
        }
    } else {
        // 手机布局 - 垂直堆叠
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimerDisplay()
            FocusControlButton()
            FocusTips()
        }
    }
}
```

## 7. 交互设计规范

### 7.1 手势交互

```kotlin
@Composable
fun FarmInteractionLayer(
    onFarmTouch: (Offset) -> Unit,
    onAnimalDrag: (animalId: String, delta: Offset) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        onFarmTouch(offset)
                    },
                    onDoubleTap = { offset ->
                        // 双击放大
                        zoomToPosition(offset)
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    // 拖拽动物
                    change.consume()
                    val animal = getAnimalAtPosition(change.position)
                    animal?.let {
                        onAnimalDrag(it.id, dragAmount)
                    }
                }
            }
    )
}
```

### 7.2 震动反馈

```kotlin
@Composable
fun rememberHapticFeedback(): HapticFeedback {
    val hapticFeedback = LocalHapticFeedback.current
    
    return remember {
        object {
            fun performClick() {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            
            fun performSuccess() {
                // 成功震动模式
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            
            fun performError() {
                // 错误震动模式
                repeat(2) {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    Thread.sleep(100)
                }
            }
        }
    }
}
```

## 8. 可访问性设计

### 8.1 语义标签

```kotlin
@Composable
fun AccessibleTimerDisplay(time: Long) {
    Column(
        modifier = Modifier.semantics {
            // 为屏幕阅读器提供描述
            contentDescription = "专注时间: ${formatTimeForAccessibility(time)}"
            // 标记为重要信息
            heading()
        }
    ) {
        Text(
            text = formatTime(time),
            style = AppTypography.H1,
            modifier = Modifier.semantics {
                // 禁用默认的文本语义，使用父容器的描述
                invisibleToUser()
            }
        )
        
        Text(
            text = "专注中",
            style = AppTypography.BodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

### 8.2 高对比度支持

```kotlin
@Composable
fun HighContrastCard(
    content: @Composable () -> Unit
) {
    val isHighContrast = LocalAccessibilityManager.current?.isHighTextContrastEnabled == true
    
    Card(
        modifier = Modifier.border(
            width = if (isHighContrast) 2.dp else 0.dp,
            color = if (isHighContrast) {
                MaterialTheme.colorScheme.onSurface
            } else {
                Color.Transparent
            }
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighContrast) {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        content()
    }
}
```