package com.phonefocusfarm.ui.screens.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonefocusfarm.core.data.dao.CycleDao
import com.phonefocusfarm.core.data.dao.IncubationSessionDao
import com.phonefocusfarm.core.data.repository.SettingsRepository
import com.phonefocusfarm.common.models.CycleType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CycleOverviewViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val cycleDao: CycleDao,
    private val sessionDao: IncubationSessionDao
) : ViewModel() {
    private val _cycleStart = MutableStateFlow<Long?>(null)
    val cycleStart: StateFlow<Long?> = _cycleStart.asStateFlow()

    private val _cycleEnd = MutableStateFlow<Long?>(null)
    val cycleEnd: StateFlow<Long?> = _cycleEnd.asStateFlow()

    private val _cycleDuration = MutableStateFlow<Long>(0L)
    val cycleDuration: StateFlow<Long> = _cycleDuration.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getAnimalUpgradeConfig().collect { cfg ->
                val now = System.currentTimeMillis()
                val start = when (cfg.cycleType) {
                    CycleType.DAILY -> dayStart(now)
                    CycleType.WEEK -> weekStart(now)
                    CycleType.MONTH -> monthStart(now)
                    else -> dayStart(now)
                }
                val end = now
                _cycleStart.value = start
                _cycleEnd.value = end
                sessionDao.getSessionsInRange(start, end).collect { sessions ->
                    _cycleDuration.value = sessions.filter { it.result.name == "SUCCESS" }.sumOf { it.duration }
                }
            }
        }
    }

    private fun dayStart(ts: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = ts
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun weekStart(ts: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = ts
        cal.firstDayOfWeek = java.util.Calendar.MONDAY
        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun monthStart(ts: Long): Long {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = ts
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}