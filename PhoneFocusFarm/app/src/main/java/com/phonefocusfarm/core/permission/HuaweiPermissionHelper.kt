package com.phonefocusfarm.core.permission

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HuaweiPermissionHelper @Inject constructor(
    private val context: Context
) {
    
    fun isHuaweiDevice(): Boolean {
        return Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true) ||
               Build.BRAND.equals("HUAWEI", ignoreCase = true) ||
               Build.MANUFACTURER.equals("HONOR", ignoreCase = true)
    }
    
    fun showHuaweiBatteryOptimizationGuide(activity: Activity) {
        if (!isHuaweiDevice()) return
        
        AlertDialog.Builder(activity)
            .setTitle("华为设备特殊设置")
            .setMessage("""
                为确保专注计时器在后台正常运行，请进行以下设置：
                
                1. 应用启动管理：
                   设置 → 应用 → 应用启动管理 → 找到"专注农场"
                   关闭"自动管理"，手动允许所有权限
                
                2. 电池优化设置：
                   设置 → 电池 → 应用启动管理 → 找到"专注农场"
                   设置为"手动管理"并允许后台活动
                
                3. 后台保护：
                   设置 → 电池 → 应用保护 → 找到"专注农场"
                   设置为"不保护"或添加到白名单
                
                完成设置后，重新打开应用即可正常使用。
            """.trimIndent())
            .setPositiveButton("前往设置") { _, _ ->
                openHuaweiSettings(activity)
            }
            .setNegativeButton("稍后设置") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun openHuaweiSettings(activity: Activity) {
        try {
            // 尝试打开华为的应用启动管理设置
            val intent = Intent().apply {
                setClassName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            try {
                // 备选方案：打开应用信息页面
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${context.packageName}")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                activity.startActivity(intent)
            } catch (e2: Exception) {
                // 最后方案：打开系统设置
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                activity.startActivity(intent)
            }
        }
    }
    
    fun checkHuaweiBatteryOptimization(): Boolean {
        if (!isHuaweiDevice()) return true
        
        // 华为设备主要检查应用启动管理中的后台运行权限
        // 不再强制要求电池优化白名单，因为华为EMUI系统有自己的管理机制
        return try {
            // 检查是否被限制后台活动
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val isBackgroundRestricted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                activityManager.isBackgroundRestricted
            } else {
                false
            }
            
            // 华为设备：如果没有被限制后台活动，就认为电池优化权限已满足
            // 同时检查是否忽略电池优化（某些华为系统可能仍然需要）
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
            val isIgnoringOptimizations = powerManager.isIgnoringBatteryOptimizations(context.packageName)
            
            // 华为设备：只要没有被限制后台活动，或者已忽略电池优化，就认为权限已授予
            !isBackgroundRestricted || isIgnoringOptimizations
        } catch (e: Exception) {
            true // 如果检查失败，假设权限已授予
        }
    }
}