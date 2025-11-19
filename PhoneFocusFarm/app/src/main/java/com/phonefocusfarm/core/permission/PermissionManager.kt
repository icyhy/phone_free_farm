package com.phonefocusfarm.core.permission

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import com.phonefocusfarm.BuildConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.phonefocusfarm.common.constants.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val huaweiPermissionHelper: HuaweiPermissionHelper
) {
    
    fun checkAllPermissions(): PermissionStatus {
        return PermissionStatus(
            usageStats = checkUsageStatsPermission(),
            notifications = checkNotificationPermission(),
            batteryOptimization = checkBatteryOptimizationPermission(),
            foregroundService = checkForegroundServicePermission(),
            wakeLock = checkWakeLockPermission()
        )
    }
    
    fun checkUsageStatsPermission(): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Android 13以下不需要此权限
        }
    }
    
    fun checkBatteryOptimizationPermission(): Boolean {
        if (isEmulator() || BuildConfig.DEBUG) return true
        return if (huaweiPermissionHelper.isHuaweiDevice()) {
            huaweiPermissionHelper.checkHuaweiBatteryOptimization()
        } else {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }
    }
    
    fun checkForegroundServicePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.FOREGROUND_SERVICE
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun checkWakeLockPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WAKE_LOCK
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun requestUsageStatsPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }
    
    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                AppConstants.REQUEST_CODE_NOTIFICATIONS
            )
        }
    }
    
    fun requestBatteryOptimizationPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        activity.startActivity(intent)
    }
    
    fun getMissingPermissions(): List<PermissionType> {
        val missingPermissions = mutableListOf<PermissionType>()
        val status = checkAllPermissions()
        
        if (!status.usageStats) missingPermissions.add(PermissionType.USAGE_STATS)
        if (!status.notifications) missingPermissions.add(PermissionType.NOTIFICATIONS)
        if (!status.batteryOptimization && !(isEmulator() || BuildConfig.DEBUG)) missingPermissions.add(PermissionType.BATTERY_OPTIMIZATION)
        if (!status.foregroundService) missingPermissions.add(PermissionType.FOREGROUND_SERVICE)
        if (!status.wakeLock) missingPermissions.add(PermissionType.WAKE_LOCK)
        
        return missingPermissions
    }
    
    fun isAllPermissionsGranted(): Boolean {
        return getMissingPermissions().isEmpty()
    }

    private fun isEmulator(): Boolean {
        val fp = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val product = Build.PRODUCT.lowercase()
        val brand = Build.BRAND.lowercase()
        val device = Build.DEVICE.lowercase()
        return fp.startsWith("generic") || fp.contains("emulator") ||
                model.contains("emulator") || product.contains("sdk_gphone") ||
                product.contains("google_sdk") || brand.contains("generic") ||
                device.contains("generic")
    }
}

data class PermissionStatus(
    val usageStats: Boolean = false,
    val notifications: Boolean = false,
    val batteryOptimization: Boolean = false,
    val foregroundService: Boolean = false,
    val wakeLock: Boolean = false
) {
    val allGranted: Boolean
        get() = usageStats && notifications && batteryOptimization && 
                foregroundService && wakeLock
}

enum class PermissionType(val displayName: String, val description: String) {
    USAGE_STATS(
        "使用情况访问权限",
        "需要此权限来检测您是否在使用其他应用"
    ),
    NOTIFICATIONS(
        "通知权限",
        "需要此权限来显示专注计时通知"
    ),
    BATTERY_OPTIMIZATION(
        "电池优化白名单",
        "请将应用添加到电池优化白名单，以确保专注计时正常工作"
    ),
    FOREGROUND_SERVICE(
        "前台服务权限",
        "需要此权限来维持后台计时服务"
    ),
    WAKE_LOCK(
        "唤醒锁定权限",
        "需要此权限来保持CPU唤醒状态"
    )
}