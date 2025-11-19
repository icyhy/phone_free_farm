package com.phonefocusfarm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.phonefocusfarm.core.permission.PermissionManager
import com.phonefocusfarm.core.permission.PermissionType
import com.phonefocusfarm.ui.navigation.AppNavigation
import com.phonefocusfarm.ui.theme.PhoneFocusFarmTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var permissionManager: PermissionManager
    
    private var permissionCallback: ((Boolean) -> Unit)? = null
    
    // 权限请求启动器
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionCallback?.invoke(isGranted)
        permissionCallback = null
    }
    
    // 系统设置返回结果
    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 返回后刷新权限状态
        permissionCallback?.invoke(true)
        permissionCallback = null
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhoneFocusFarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        onRequestPermission = { permissionType, callback ->
                            handlePermissionRequest(permissionType, callback)
                        }
                    )
                }
            }
        }
    }
    
    private fun handlePermissionRequest(permissionType: PermissionType, callback: (Boolean) -> Unit) {
        permissionCallback = callback
        
        when (permissionType) {
            PermissionType.NOTIFICATIONS -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    callback(true) // Android 13以下不需要请求通知权限
                }
            }
            
            PermissionType.USAGE_STATS -> {
                // 跳转到系统设置页面
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                settingsLauncher.launch(intent)
            }
            
            PermissionType.BATTERY_OPTIMIZATION -> {
                // 跳转到电池优化设置
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                settingsLauncher.launch(intent)
            }
            
            PermissionType.FOREGROUND_SERVICE,
            PermissionType.WAKE_LOCK -> {
                // 这些权限在AndroidManifest中声明即可，不需要动态请求
                callback(true)
            }
        }
    }
}