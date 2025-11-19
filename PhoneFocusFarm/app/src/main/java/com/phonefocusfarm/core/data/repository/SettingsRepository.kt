package com.phonefocusfarm.core.data.repository

import com.phonefocusfarm.common.models.AnimalUpgradeConfig
import com.phonefocusfarm.common.models.CycleType
import com.phonefocusfarm.common.models.AnimalType
import com.phonefocusfarm.core.data.dao.AnimalDao
import com.phonefocusfarm.core.data.dao.CycleDao
import com.phonefocusfarm.core.data.entity.CycleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val animalDao: AnimalDao,
    private val cycleDao: CycleDao
) {
    
    private val _animalUpgradeConfig = MutableStateFlow(AnimalUpgradeConfig())
    val animalUpgradeConfig: Flow<AnimalUpgradeConfig> = _animalUpgradeConfig.asStateFlow()

    private val _allowPause = MutableStateFlow(false)
    val allowPause: Flow<Boolean> = _allowPause.asStateFlow()
    
    suspend fun getAnimalUpgradeConfig(): Flow<AnimalUpgradeConfig> {
        return animalUpgradeConfig
    }

    suspend fun getAllowPause(): Flow<Boolean> {
        return allowPause
    }
    
    suspend fun updateStageDuration(duration: Long) {
        val currentConfig = _animalUpgradeConfig.value
        _animalUpgradeConfig.value = currentConfig.copy(
            stageDuration = duration
        )
    }

    suspend fun updateAllowPause(enabled: Boolean) {
        _allowPause.value = enabled
    }
    
    suspend fun updateCycleType(type: CycleType) {
        val currentConfig = _animalUpgradeConfig.value
        val cycleDuration = when (type) {
            CycleType.DAILY -> 86400000L // 1天
            CycleType.WEEK -> 604800000L // 1周
            CycleType.MONTH -> 2592000000L // 30天
            CycleType.QUARTER -> 7776000000L // 90天
            CycleType.YEAR -> 31536000000L // 365天
            CycleType.CUSTOM -> 86400000L // 默认1天
        }
        
        _animalUpgradeConfig.value = currentConfig.copy(
            cycleType = type,
            cycleDuration = cycleDuration
        )
    }
    
    suspend fun resetFarmCycle(reason: String) {
        // 获取当前动物统计
        val animals = animalDao.getAllAnimals().first()
        val animalCounts = mutableMapOf<AnimalType, Int>()
        for (animal in animals) {
            animalCounts[animal.type] = (animalCounts[animal.type] ?: 0) + 1
        }
        
        // 计算统计数据
        val chickenCount = (animalCounts[AnimalType.CHICKEN] ?: 0) + 
                         (animalCounts[AnimalType.CHICKEN_RED] ?: 0) + 
                         (animalCounts[AnimalType.CHICKEN_FANCY] ?: 0)
        
        val catCount = (animalCounts[AnimalType.CAT] ?: 0) + 
                      (animalCounts[AnimalType.CAT_TABBY] ?: 0) + 
                      (animalCounts[AnimalType.CAT_FAT] ?: 0)
        
        val dogCount = (animalCounts[AnimalType.DOG] ?: 0) + 
                      (animalCounts[AnimalType.DOG_BLACK] ?: 0) + 
                      (animalCounts[AnimalType.DOG_HUSKY] ?: 0)
        
        // 创建周期记录
        val cycle = CycleEntity(
            id = UUID.randomUUID().toString(),
            startTime = System.currentTimeMillis() - 86400000L, // 假设周期从24小时前开始
            endTime = System.currentTimeMillis(),
            type = _animalUpgradeConfig.value.cycleType,
            totalSessions = 0, // 这里可以从其他数据源获取
            totalDuration = 0L, // 这里可以从其他数据源获取
            chickenCount = chickenCount,
            catCount = catCount,
            dogCount = dogCount,
            achievements = emptyList(),
            createdAt = System.currentTimeMillis(),
            resetReason = reason
        )
        
        // 保存周期记录
        cycleDao.insertCycle(cycle)
        
        // 清空所有动物
        animalDao.deleteAllAnimals()
    }
}