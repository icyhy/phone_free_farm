package com.phonefocusfarm.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonefocusfarm.core.data.dao.IncubationSessionDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val sessionDao: IncubationSessionDao
) : ViewModel() {
    private val _totalFocusTime = MutableStateFlow(0L)
    val totalFocusTime: StateFlow<Long> = _totalFocusTime.asStateFlow()

    private val _successfulCount = MutableStateFlow(0)
    val successfulCount: StateFlow<Int> = _successfulCount.asStateFlow()

    private val _longestFocus = MutableStateFlow(0L)
    val longestFocus: StateFlow<Long> = _longestFocus.asStateFlow()

    private val _recentSessions = MutableStateFlow<List<com.phonefocusfarm.core.data.entity.IncubationSessionEntity>>(emptyList())
    val recentSessions: StateFlow<List<com.phonefocusfarm.core.data.entity.IncubationSessionEntity>> = _recentSessions.asStateFlow()

    init {
        viewModelScope.launch {
            sessionDao.getTotalFocusTime().collect { t ->
                _totalFocusTime.value = t ?: 0L
            }
        }
        viewModelScope.launch {
            sessionDao.getSuccessfulSessionCount().collect { c ->
                _successfulCount.value = c
            }
        }
        viewModelScope.launch {
            sessionDao.getLongestFocusTime().collect { l ->
                _longestFocus.value = l ?: 0L
            }
        }
        viewModelScope.launch {
            sessionDao.getRecentSessions(20).collect { list ->
                _recentSessions.value = list
            }
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            sessionDao.deleteSession(id)
        }
    }
}