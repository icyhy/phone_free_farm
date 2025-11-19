package com.phonefocusfarm.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonefocusfarm.common.models.CycleType
import com.phonefocusfarm.common.models.AnimalUpgradeConfig
import com.phonefocusfarm.core.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val timerManager: com.phonefocusfarm.core.timer.TimerManager
) : ViewModel() {
    
    private val _stageDuration = MutableStateFlow(600000L) // 默认10分钟
    val stageDuration: StateFlow<Long> = _stageDuration.asStateFlow()
    
    private val _cycleType = MutableStateFlow(CycleType.DAILY)
    val cycleType: StateFlow<CycleType> = _cycleType.asStateFlow()
    
    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog: StateFlow<Boolean> = _showResetDialog.asStateFlow()

    private val _allowPause = MutableStateFlow(false)
    val allowPause: StateFlow<Boolean> = _allowPause.asStateFlow()

    val isTestMode: StateFlow<Boolean> = timerManager.isTestMode
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getAnimalUpgradeConfig().collect { config ->
                _stageDuration.value = config.stageDuration
                _cycleType.value = config.cycleType
            }
        }
        viewModelScope.launch {
            settingsRepository.getAllowPause().collect { enabled ->
                _allowPause.value = enabled
            }
        }
    }
    
    fun setStageDuration(duration: Long) {
        viewModelScope.launch {
            settingsRepository.updateStageDuration(duration)
            _stageDuration.value = duration
        }
    }
    
    fun setCycleType(type: CycleType) {
        viewModelScope.launch {
            settingsRepository.updateCycleType(type)
            _cycleType.value = type
        }
    }

    fun setAllowPause(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateAllowPause(enabled)
            _allowPause.value = enabled
        }
    }

    fun setTestMode(enabled: Boolean) {
        viewModelScope.launch {
            timerManager.setTestMode(enabled)
        }
    }
    
    fun showResetDialog() {
        _showResetDialog.value = true
    }
    
    fun hideResetDialog() {
        _showResetDialog.value = false
    }
    
    fun resetFarm() {
        viewModelScope.launch {
            settingsRepository.resetFarmCycle("用户手动重置")
        }
    }
}