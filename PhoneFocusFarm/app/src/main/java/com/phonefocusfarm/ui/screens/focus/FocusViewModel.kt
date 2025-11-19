package com.phonefocusfarm.ui.screens.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonefocusfarm.common.models.FocusMode
import com.phonefocusfarm.common.models.TimerState
import com.phonefocusfarm.core.timer.TimerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val timerManager: TimerManager
) : ViewModel() {
    
    val timerState: StateFlow<TimerState> = timerManager.timerState
    val focusMode: StateFlow<FocusMode> get() = timerManager.currentMode
    val isTestMode: StateFlow<Boolean> = timerManager.isTestMode
    
    // 简化界面状态
    private val _currentTime = MutableStateFlow(0)
    val currentTime: StateFlow<Int> = _currentTime.asStateFlow()
    
    
    private val _focusTips = MutableStateFlow("保持专注，远离手机干扰")
    val focusTips: StateFlow<String> = _focusTips.asStateFlow()
    
    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog: StateFlow<Boolean> = _showResetDialog.asStateFlow()
    
    init {
        // 模拟计时器更新
        viewModelScope.launch {
            var time = 0
            while (true) {
                kotlinx.coroutines.delay(1000)
                if (timerState.value is TimerState.Incubating) {
                    time++
                    _currentTime.value = time
                } else if (timerState.value is TimerState.Idle) {
                    time = 0
                    _currentTime.value = time
                } else if (timerState.value is TimerState.Completed) {
                    // 完成后等待下一次开始时重置
                    time = 0
                }
            }
        }
    }
    
    fun startTimer() {
        viewModelScope.launch {
            _currentTime.value = 0
            timerManager.startTimer(FocusMode.STRICT)
        }
    }
    
    fun pauseTimer() {
        viewModelScope.launch {
            timerManager.pauseTimer()
        }
    }
    
    fun resumeTimer() {
        viewModelScope.launch {
            timerManager.resumeTimer()
        }
    }
    
    fun resetTimer() {
        viewModelScope.launch {
            timerManager.reset()
        }
    }
    
    fun stopFocus() {
        viewModelScope.launch {
            timerManager.stopTimer()
        }
    }
    
    fun setTestMode(enabled: Boolean) {
        viewModelScope.launch {
            timerManager.setTestMode(enabled)
        }
    }
    
    fun setFocusVisible(visible: Boolean) {
        viewModelScope.launch {
            timerManager.setFocusScreenVisible(visible)
        }
    }
    
    fun showResetDialog() {
        _showResetDialog.value = true
    }
    
    fun hideResetDialog() {
        _showResetDialog.value = false
    }
}