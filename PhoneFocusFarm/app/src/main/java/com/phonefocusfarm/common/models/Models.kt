package com.phonefocusfarm.common.models

import java.util.UUID

// 基础实体接口
interface BaseEntity {
    val id: String
}

// 动物类型枚举
enum class AnimalType(val displayName: String, val emoji: String) {
    CHICKEN("小鸡", "🐥"),
    CHICKEN_RED("红羽鸡", "🐓"),
    CHICKEN_FANCY("漂亮鸡", "🦃"),
    CAT("小猫", "🐱"),
    CAT_TABBY("花猫", "🐈"),
    CAT_FAT("肥猫", "🙀"),
    DOG("小狗", "🐶"),
    DOG_BLACK("黑狗", "🐕"),
    DOG_HUSKY("哈士奇", "🦮"),
    PIG("小猪", "🐷"),
    COW("小牛", "🐮"),
    SHEEP("小羊", "🐑");
    
    companion object {
        fun fromString(type: String): AnimalType = valueOf(type.uppercase())
    }
}

// 专注模式枚举 - 根据需求只保留两种模式
enum class FocusMode(val displayName: String) {
    STRICT("严格模式");
    
    companion object {
        fun fromString(mode: String): FocusMode = STRICT
    }
}

// 计时器状态
sealed class TimerState {
    object Idle : TimerState()
    data class Incubating(
        val startTime: Long = System.currentTimeMillis(),
        val currentAnimal: AnimalType? = null,
        val progress: Float = 0f
    ) : TimerState()
    data class Paused(
        val duration: Long
    ) : TimerState()
    data class Interrupted(
        val reason: InterruptionReason,
        val duration: Long
    ) : TimerState()
    data class Completed(
        val duration: Long,
        val result: IncubationResult
    ) : TimerState()
}

// 中断原因
enum class InterruptionReason(val displayName: String) {
    TOUCH_EVENT("触摸事件"),
    APP_BACKGROUND("应用后台"),
    DEVICE_MOVEMENT("设备移动"),
    SCREEN_UNLOCK("屏幕解锁"),
    USAGE_STATS_DENIED("权限被拒"),
    SYSTEM_INTERRUPT("系统中断");
    
    companion object {
        fun fromString(reason: String): InterruptionReason = valueOf(reason.uppercase())
    }
}

// 孵化结果
enum class IncubationResult(val displayName: String) {
    SUCCESS("成功"),
    INTERRUPTED("中断"),
    FAILED("失败");
    
    companion object {
        fun fromString(result: String): IncubationResult = valueOf(result.uppercase())
    }
}

// 动物实体
data class Animal(
    override val id: String = UUID.randomUUID().toString(),
    val type: AnimalType,
    var position: Position,
    var velocity: Velocity,
    var state: AnimalState = AnimalState.IDLE,
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis(),
    var upgradeStage: Int = 0,  // 升级阶段 (0-2)
    var stageStartTime: Long = System.currentTimeMillis()  // 当前阶段开始时间
) : BaseEntity

// 动物状态
enum class AnimalState {
    IDLE,       // 静止
    WANDERING,  // 游走
    CHASING,    // 追逐
    FLEEING,    // 逃跑
    INTERACTING // 互动中
}

// 位置向量
data class Position(
    var x: Float,
    var y: Float
) {
    fun distanceTo(other: Position): Float {
        val dx = x - other.x
        val dy = y - other.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}

// 速度向量
data class Velocity(
    var x: Float,
    var y: Float
) {
    fun magnitude(): Float = kotlin.math.sqrt(x * x + y * y)
    
    fun normalize(): Velocity {
        val mag = magnitude()
        return if (mag > 0) Velocity(x / mag, y / mag) else Velocity(0f, 0f)
    }
    
    fun limit(maxSpeed: Float): Velocity {
        val mag = magnitude()
        return if (mag > maxSpeed) {
            val ratio = maxSpeed / mag
            Velocity(x * ratio, y * ratio)
        } else this
    }
}

// 孵化会话
data class IncubationSession(
    override val id: String = UUID.randomUUID().toString(),
    val startTime: Long,
    val endTime: Long? = null,
    val duration: Long = 0,
    val result: IncubationResult = IncubationResult.FAILED,
    val mode: FocusMode,
    val interruptionReason: InterruptionReason? = null,
    val animalGenerated: AnimalType? = null,
    val createdAt: Long = System.currentTimeMillis()
) : BaseEntity

// 周期统计
data class Cycle(
    override val id: String = UUID.randomUUID().toString(),
    val startTime: Long,
    val endTime: Long? = null,
    val type: CycleType,
    val totalSessions: Int = 0,
    val totalDuration: Long = 0,
    val animalsGenerated: Map<AnimalType, Int> = emptyMap(),
    val achievements: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val resetReason: String? = null  // 重置原因
) : BaseEntity

// 周期类型
enum class CycleType(val displayName: String) {
    DAILY("日"),
    WEEK("周"),
    MONTH("月"),
    QUARTER("季度"),
    YEAR("年"),
    CUSTOM("自定义");
    
    companion object {
        fun fromString(type: String): CycleType = valueOf(type.uppercase())
    }
}

// 成就
data class Achievement(
    override val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val icon: String,
    val condition: AchievementCondition,
    val progress: Int = 0,
    val target: Int,
    val unlockedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) : BaseEntity

// 成就条件
sealed class AchievementCondition {
    data class TotalFocusTime(val targetMinutes: Int) : AchievementCondition()
    data class ConsecutiveFocusTime(val targetMinutes: Int) : AchievementCondition()
    data class AnimalsGenerated(val targetCount: Int, val animalType: AnimalType?) : AchievementCondition()
    data class FocusSessions(val targetCount: Int) : AchievementCondition()
    data class PerfectDays(val targetDays: Int) : AchievementCondition()
}

// 动物升级配置
data class AnimalUpgradeConfig(
    val stageDuration: Long = 10000,
    val cycleType: CycleType = CycleType.WEEK,
    val cycleDuration: Long = 604800000,
    val maxStage: Int = 2
)

// 动物升级路径
data class AnimalUpgradePath(
    val fromType: AnimalType,
    val toType: AnimalType,
    val requiredStage: Int,
    val displayName: String
)

// 农场背景
data class FarmBackground(
    val id: String = UUID.randomUUID().toString(),
    val type: BackgroundType,
    val color: Int,
    val pattern: String? = null
)

// 背景类型
enum class BackgroundType(val displayName: String) {
    GRASSLAND("草地"),
    FOREST("森林"),
    DESERT("沙漠"),
    SNOW("雪地"),
    CUSTOM("自定义");
    
    companion object {
        fun fromString(type: String): BackgroundType = valueOf(type.uppercase())
    }
}
