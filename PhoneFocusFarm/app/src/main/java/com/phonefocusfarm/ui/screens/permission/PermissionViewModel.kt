package com.phonefocusfarm.ui.screens.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonefocusfarm.core.permission.PermissionManager
import com.phonefocusfarm.core.permission.HuaweiPermissionHelper
import com.phonefocusfarm.core.permission.PermissionStatus
import com.phonefocusfarm.core.permission.PermissionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val permissionManager: PermissionManager,
    private val huaweiPermissionHelper: HuaweiPermissionHelper
) : ViewModel() {
    
    private val _permissionStatus = MutableStateFlow(PermissionStatus())
    val permissionStatus: StateFlow<PermissionStatus> = _permissionStatus.asStateFlow()
    
    private val _missingPermissions = MutableStateFlow<List<PermissionType>>(emptyList())
    val missingPermissions: StateFlow<List<PermissionType>> = _missingPermissions.asStateFlow()
    
    private val _isHuaweiDevice = MutableStateFlow(false)
    val isHuaweiDevice: StateFlow<Boolean> = _isHuaweiDevice.asStateFlow()
    
    init {
        checkDeviceType()
        refreshPermissionStatus()
    }
    
    private fun checkDeviceType() {
        _isHuaweiDevice.value = huaweiPermissionHelper.isHuaweiDevice()
    }
    
    fun refreshPermissionStatus() {
        viewModelScope.launch {
            val status = permissionManager.checkAllPermissions()
            _permissionStatus.value = status
            _missingPermissions.value = permissionManager.getMissingPermissions()
        }
    }
    
    fun requestPermission(permissionType: PermissionType) {
        viewModelScope.launch {
            // 权限请求将在UI层处理，这里只更新状态
            refreshPermissionStatus()
        }
    }
    
    fun areAllPermissionsGranted(): StateFlow<Boolean> {
        return _permissionStatus.map { it.allGranted }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    }
}