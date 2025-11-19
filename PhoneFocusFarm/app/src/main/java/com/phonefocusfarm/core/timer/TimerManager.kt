package com.phonefocusfarm.core.timer

import android.content.Context
import android.content.Intent
import com.phonefocusfarm.common.models.*
import com.phonefocusfarm.core.service.FocusTimerService
import com.phonefocusfarm.core.detector.InterruptionDetector
import com.phonefocusfarm.core.detector.UsageStatsDetector
import dagger.hilt.android.qualifiers.ApplicationContext
import com.phonefocusfarm.core.data.dao.AnimalDao
import com.phonefocusfarm.core.data.dao.IncubationSessionDao
import com.phonefocusfarm.core.data.entity.AnimalEntity
import com.phonefocusfarm.core.data.entity.IncubationSessionEntity
import java.util.UUID
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

interface TimerManager {
    val timerState: StateFlow<TimerState>
    val currentMode: StateFlow<FocusMode>
    val isTestMode: StateFlow<Boolean>
    
    suspend fun startTimer(mode: FocusMode)
    suspend fun stopTimer()
    suspend fun pauseTimer()
    suspend fun resumeTimer()
    suspend fun setTestMode(enabled: Boolean)
    suspend fun setFocusScreenVisible(visible: Boolean)
    
    fun getIncubationThresholds(): IncubationThresholds
    fun getCurrentProgress(): Float
    fun getRemainingTime(): Long

    suspend fun reset()
}

data class IncubationThresholds(
    val chicken: Long,
    val cat: Long,
    val dog: Long
)

// 动物升级路径定义
private val animalUpgradePaths = listOf(
    // 鸡升级路径
    AnimalUpgradePath(AnimalType.CHICKEN, AnimalType.CHICKEN_RED, 1, "红羽鸡"),
    AnimalUpgradePath(AnimalType.CHICKEN_RED, AnimalType.CHICKEN_FANCY, 2, "漂亮鸡"),
    AnimalUpgradePath(AnimalType.CHICKEN_FANCY, AnimalType.CAT, 3, "小猫"),
    
    // 猫升级路径
    AnimalUpgradePath(AnimalType.CAT, AnimalType.CAT_TABBY, 1, "花猫"),
    AnimalUpgradePath(AnimalType.CAT_TABBY, AnimalType.CAT_FAT, 2, "肥猫"),
    AnimalUpgradePath(AnimalType.CAT_FAT, AnimalType.DOG, 3, "小狗"),
    
    // 狗升级路径
    AnimalUpgradePath(AnimalType.DOG, AnimalType.DOG_BLACK, 1, "黑狗"),
    AnimalUpgradePath(AnimalType.DOG_BLACK, AnimalType.DOG_HUSKY, 2, "哈士奇")
)

class TimerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val interruptionDetector: InterruptionDetector,
    private val usageStatsDetector: UsageStatsDetector,
    private val animalDao: AnimalDao,
    private val incubationSessionDao: IncubationSessionDao
) : TimerManager {
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    override val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    
    private val _currentMode = MutableStateFlow(FocusMode.STRICT)
    override val currentMode: StateFlow<FocusMode> = _currentMode.asStateFlow()
    
    private val _isTestMode = MutableStateFlow(false)
    override val isTestMode: StateFlow<Boolean> = _isTestMode.asStateFlow()
    
    private var currentSession: IncubationSession? = null
    private var startTime: Long = 0
    private var updateJob: kotlinx.coroutines.Job? = null
    private var interruptionJob: kotlinx.coroutines.Job? = null
    private var milestoneStage: Int = 0
    private var isPaused: Boolean = false
    private var pausedAt: Long = 0L
    private var animalUpgradeConfig: AnimalUpgradeConfig = AnimalUpgradeConfig() // 动物升级配置
    private val _focusScreenVisible = MutableStateFlow(false)
    private val focusScreenVisible: StateFlow<Boolean> = _focusScreenVisible.asStateFlow()
    
    override suspend fun startTimer(mode: FocusMode) {
        _currentMode.value = mode
        startTime = System.currentTimeMillis()
        milestoneStage = 0
        isPaused = false
        
        currentSession = IncubationSession(
            startTime = startTime,
            mode = mode
        )
        
        _timerState.value = TimerState.Incubating(
            startTime = startTime,
            progress = 0f
        )
        
        // 启动前台服务
        val serviceIntent = FocusTimerService.createStartIntent(context, startTime)
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
        
        // 启动定期更新
        startPeriodicUpdates()
        
        // 启动中断检测
        startInterruptionMonitoring()
    }
    
    override suspend fun stopTimer() {
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        currentSession?.let { session ->
            val result = calculateIncubationResult(duration)
            val counts = computeAnimalCounts(duration)

            val completedSession = session.copy(
                endTime = endTime,
                duration = duration,
                result = result,
                animalGenerated = null
            )

            GlobalScope.launch {
                // 持久化会话
                incubationSessionDao.insertSession(
                    IncubationSessionEntity(
                        id = completedSession.id,
                        startTime = completedSession.startTime,
                        endTime = completedSession.endTime,
                        duration = completedSession.duration,
                        result = completedSession.result,
                        mode = completedSession.mode,
                        interruptionReason = completedSession.interruptionReason,
                        animalGenerated = null,
                        createdAt = completedSession.createdAt
                    )
                )

                // 根据时长一次性投放动物（不再升级）
                repeat(counts.dog) { insertAnimal(AnimalType.DOG) }
                repeat(counts.cat) { insertAnimal(AnimalType.CAT) }
                repeat(counts.chicken) { insertAnimal(AnimalType.CHICKEN) }
            }

            _timerState.value = TimerState.Completed(duration, result)
        }
        
        currentSession = null
        
        // 停止定期更新
        updateJob?.cancel()
        updateJob = null
        
        // 停止中断检测
        interruptionJob?.cancel()
        interruptionJob = null
        interruptionDetector.stopMonitoring()
        usageStatsDetector.stopMonitoring()
        
        // 停止前台服务
        val serviceIntent = FocusTimerService.createStopIntent(context)
        context.startService(serviceIntent)
    }
    
    override suspend fun pauseTimer() {
        if (_timerState.value is TimerState.Incubating) {
            isPaused = true
            pausedAt = System.currentTimeMillis()
            updateJob?.cancel()
            val duration = pausedAt - startTime
            _timerState.value = TimerState.Paused(duration)
        }
    }
    
    override suspend fun resumeTimer() {
        if (_timerState.value is TimerState.Paused || _timerState.value is TimerState.Interrupted) {
            isPaused = false
            startPeriodicUpdates()
            _timerState.value = TimerState.Incubating(
                startTime = startTime,
                currentAnimal = null,
                progress = getCurrentProgress()
            )
        }
    }
    
    override suspend fun setTestMode(enabled: Boolean) {
        _isTestMode.value = enabled
    }
    
    override suspend fun setFocusScreenVisible(visible: Boolean) {
        _focusScreenVisible.value = visible
    }
    
    override fun getIncubationThresholds(): IncubationThresholds {
        val stageDuration = if (_isTestMode.value) 10000L else animalUpgradeConfig.stageDuration // 测试模式10秒，正常模式使用配置
        return if (_isTestMode.value) {
            IncubationThresholds(
                chicken = stageDuration, // 10秒
                cat = stageDuration * 2,     // 20秒
                dog = stageDuration * 3      // 30秒
            )
        } else {
            IncubationThresholds(
                chicken = stageDuration,  // 使用配置的时长
                cat = stageDuration * 2,     // 2倍时长
                dog = stageDuration * 3      // 3倍时长
            )
        }
    }
    
    override fun getCurrentProgress(): Float {
        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - startTime
        val thresholds = getIncubationThresholds()
        
        return when {
            elapsed < thresholds.chicken -> elapsed.toFloat() / thresholds.chicken
            elapsed < thresholds.cat -> (elapsed - thresholds.chicken).toFloat() / (thresholds.cat - thresholds.chicken)
            elapsed < thresholds.dog -> (elapsed - thresholds.cat).toFloat() / (thresholds.dog - thresholds.cat)
            else -> 1f
        }
    }
    
    override fun getRemainingTime(): Long {
        val currentTime = System.currentTimeMillis()
        val elapsed = currentTime - startTime
        val thresholds = getIncubationThresholds()
        
        return when {
            elapsed < thresholds.chicken -> thresholds.chicken - elapsed
            elapsed < thresholds.cat -> thresholds.cat - elapsed
            elapsed < thresholds.dog -> thresholds.dog - elapsed
            else -> 0
        }
    }
    
    private fun updateNotification() {
        val progress = getCurrentProgress()
        val remainingTime = getRemainingTime()
        
        val serviceIntent = FocusTimerService.createProgressIntent(context, progress, remainingTime)
        context.startService(serviceIntent)
    }
    
    private fun calculateIncubationResult(duration: Long): IncubationResult {
        return if (duration >= getIncubationThresholds().chicken) {
            IncubationResult.SUCCESS
        } else {
            IncubationResult.INTERRUPTED
        }
    }
    
    private data class AnimalCounts(val chicken: Int, val cat: Int, val dog: Int)

    private fun computeAnimalCounts(duration: Long): AnimalCounts {
        val t = getIncubationThresholds()
        var remaining = duration
        val dog = (remaining / t.dog).toInt()
        remaining %= t.dog
        val cat = (remaining / t.cat).toInt()
        remaining %= t.cat
        val chicken = (remaining / t.chicken).toInt()
        return AnimalCounts(chicken = chicken, cat = cat, dog = dog)
    }
    
    private fun startPeriodicUpdates() {
        updateJob?.cancel()
        updateJob = kotlinx.coroutines.GlobalScope.launch {
            var lastAnimalSpawnTime = startTime // 记录上次投放动物的时间
            
            while (true) {
                delay(1000) // 每秒更新一次
                
                if (_timerState.value is TimerState.Incubating && !isPaused) {
                    val currentTime = System.currentTimeMillis()
                    val elapsed = currentTime - startTime
                    val progress = getCurrentProgress()
                    val remainingTime = getRemainingTime()
                    
                    // 更新状态中的进度
                    _timerState.value = TimerState.Incubating(
                        startTime = startTime,
                        currentAnimal = null,
                        progress = progress
                    )
                    
                    // 更新通知
                    updateNotification()

                    // 简化逻辑：计时期间不进行任何动物投放或升级，统一在停止计时时按总时长一次性投放
                    
                } else {
                    break
                }
            }
        }
    }
    
    private suspend fun checkAndUpgradeAnimals(elapsed: Long) {
        val now = System.currentTimeMillis()
        
        // 获取所有在场的动物
        val activeAnimalsFlow = animalDao.getActiveAnimals()
        val activeAnimals = activeAnimalsFlow.firstOrNull() ?: return
        
        for (animal in activeAnimals) {
            // 使用updatedAt作为阶段开始时间，而不是createdAt
            val stageStartTime = animal.updatedAt
            val timeInStage = now - stageStartTime
            val stageDuration = if (_isTestMode.value) 10000L else animalUpgradeConfig.stageDuration
            
            // 检查是否需要升级阶段（必须满足最小阶段时长）
            if (timeInStage >= stageDuration) {
                val nextUpgradePath = animalUpgradePaths.find { 
                    it.fromType == animal.type 
                }
                
                if (nextUpgradePath != null) {
                    val upgraded = animal.copy(
                        type = nextUpgradePath.toType,
                        updatedAt = now  // 更新阶段开始时间
                    )
                    animalDao.updateAnimal(upgraded)
                }
            }
        }
    }

    override suspend fun reset() {
        updateJob?.cancel()
        interruptionJob?.cancel()
        interruptionDetector.stopMonitoring()
        usageStatsDetector.stopMonitoring()
        val stopIntent = FocusTimerService.createStopIntent(context)
        context.startService(stopIntent)
        
        // 清空所有动物（修复首页重置不生效问题）
        kotlinx.coroutines.GlobalScope.launch {
            animalDao.deleteAllAnimals()
        }
        
        currentSession = null
        milestoneStage = 0
        isPaused = false
        startTime = 0L
        _timerState.value = TimerState.Idle
    }

    private suspend fun insertAnimal(type: AnimalType) {
        val now = System.currentTimeMillis()
        val entity = AnimalEntity(
            id = UUID.randomUUID().toString(),
            type = type,
            posX = 0f,
            posY = 0f,
            velX = 0f,
            velY = 0f,
            state = "ACTIVE",
            createdAt = now,
            updatedAt = now
        )
        animalDao.insertAnimal(entity)
    }

    private suspend fun upgradeOrInsert(from: AnimalType, to: AnimalType) {
        val target = animalDao.getOneActiveAnimalByType(from.name)
        val now = System.currentTimeMillis()
        if (target != null) {
            val upgraded = target.copy(
                type = to,
                updatedAt = now
            )
            animalDao.updateAnimal(upgraded)
        } else {
            insertAnimal(to)
        }
    }
    
    private fun startInterruptionMonitoring() {
        interruptionDetector.startMonitoring()
        usageStatsDetector.startMonitoring()
        
        interruptionJob?.cancel()
        interruptionJob = kotlinx.coroutines.GlobalScope.launch {
            // 监听触摸和移动中断
            launch {
                interruptionDetector.interruptionEvents.collect { reason ->
                    handleInterruption(reason)
                }
            }
            
            // 监听应用使用中断
            launch {
                usageStatsDetector.interruptionEvents.collect { reason ->
                    handleInterruption(reason)
                }
            }
            // 监听焦点页面可见性
            launch {
                focusScreenVisible.collect { visible ->
                    if (!visible && _currentMode.value == FocusMode.STRICT) {
                        handleInterruption(InterruptionReason.APP_BACKGROUND)
                    }
                }
            }
        }
    }
    
    private fun handleInterruption(reason: InterruptionReason) {
        android.util.Log.d("TimerManager", "handleInterruption: $reason, mode=${_currentMode.value}")
        when (reason) {
            InterruptionReason.TOUCH_EVENT -> {
                // 触摸检测 - 统一中断
                interruptTimer(reason)
            }
            InterruptionReason.DEVICE_MOVEMENT -> {
                // 设备移动 - 统一中断
                interruptTimer(reason)
            }
            InterruptionReason.APP_BACKGROUND -> {
                // 应用切换到后台（Home/Overview）——任何模式中断
                interruptTimer(reason)
            }
            InterruptionReason.SCREEN_UNLOCK -> {
                // 屏幕解锁 - 统一中断
                interruptTimer(reason)
            }
            InterruptionReason.USAGE_STATS_DENIED -> {
                // 权限被拒 - 在任何模式下都会中断
                interruptTimer(reason)
            }
            InterruptionReason.SYSTEM_INTERRUPT -> {
                // 系统中断 - 在任何模式下都会中断
                interruptTimer(reason)
            }
        }
    }
    
    private fun interruptTimer(reason: InterruptionReason) {
        val currentState = _timerState.value
        if (currentState is TimerState.Incubating) {
            val currentTime = System.currentTimeMillis()
            val duration = currentTime - startTime
            
            _timerState.value = TimerState.Interrupted(
                reason = reason,
                duration = duration
            )
            
            // 停止定时器
            kotlinx.coroutines.GlobalScope.launch {
                stopTimer()
            }
        }
    }
}
