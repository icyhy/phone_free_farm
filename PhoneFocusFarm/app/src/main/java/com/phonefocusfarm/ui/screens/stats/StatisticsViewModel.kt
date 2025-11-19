package com.phonefocusfarm.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonefocusfarm.common.models.AnimalType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor() : ViewModel() {
    
    private val _statistics = MutableStateFlow(Statistics())
    val statistics: StateFlow<Statistics> = _statistics.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            // TODO: 从数据库加载统计数据
            // 临时数据
            _statistics.value = Statistics(
                totalFocusTime = 3600000, // 1小时
                totalSessions = 5,
                averageFocusTime = 720000, // 12分钟
                longestFocusTime = 1800000, // 30分钟
                totalChickens = 3,
                totalCats = 1,
                totalDogs = 0,
                achievements = listOf(
                    Achievement("第一只小鸡", "孵化出第一只小鸡", true),
                    Achievement("专注新手", "累计专注30分钟", true),
                    Achievement("猫咪大师", "拥有5只小猫", false)
                ),
                recentSessions = listOf(
                    Session(System.currentTimeMillis(), 900000, true, AnimalType.CHICKEN),
                    Session(System.currentTimeMillis() - 86400000, 600000, true, null),
                    Session(System.currentTimeMillis() - 172800000, 1200000, true, AnimalType.CAT)
                )
            )
        }
    }
    
    fun shareAchievement(achievement: Achievement) {
        viewModelScope.launch {
            // TODO: 实现成就分享功能
        }
    }
    
    fun shareStatistics() {
        viewModelScope.launch {
            // TODO: 实现统计数据分享功能
        }
    }
}

data class Statistics(
    val totalFocusTime: Long = 0,
    val totalSessions: Int = 0,
    val averageFocusTime: Long = 0,
    val longestFocusTime: Long = 0,
    val totalChickens: Int = 0,
    val totalCats: Int = 0,
    val totalDogs: Int = 0,
    val achievements: List<Achievement> = emptyList(),
    val recentSessions: List<Session> = emptyList()
)

data class Achievement(
    val name: String,
    val description: String,
    val isUnlocked: Boolean
)

data class Session(
    val date: Long,
    val duration: Long,
    val isSuccess: Boolean,
    val animalGenerated: AnimalType?
)