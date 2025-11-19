package com.phonefocusfarm.core.detector

import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import com.phonefocusfarm.common.models.InterruptionReason
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageStatsDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val _interruptionEvents = MutableSharedFlow<InterruptionReason>()
    val interruptionEvents: SharedFlow<InterruptionReason> = _interruptionEvents.asSharedFlow()
    
    private val handler = Handler(Looper.getMainLooper())
    private var monitoringRunnable: Runnable? = null
    private var isMonitoring = false
    private var lastForegroundApp: String? = null
    private var lastCheckTime: Long = 0
    
    private val usageStatsManager: UsageStatsManager? by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
    }
    
    private val packageManager: PackageManager by lazy {
        context.packageManager
    }
    
    private val ourPackageName = context.packageName
    
    fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true
        
        lastCheckTime = System.currentTimeMillis()
        startMonitoringLoop()
    }
    
    fun stopMonitoring() {
        isMonitoring = false
        monitoringRunnable?.let { handler.removeCallbacks(it) }
        monitoringRunnable = null
    }
    
    private fun startMonitoringLoop() {
        monitoringRunnable = object : Runnable {
            override fun run() {
                if (!isMonitoring) return
                
                checkCurrentAppUsage()
                
                // 每2秒检查一次
                handler.postDelayed(this, 2000)
            }
        }
        
        monitoringRunnable?.let { handler.post(it) }
    }
    
    private fun checkCurrentAppUsage() {
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - 5000 // 检查过去5秒的使用情况
        
        try {
            val usageStats = usageStatsManager?.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                startTime,
                currentTime
            )
            
            if (usageStats.isNullOrEmpty()) {
                return
            }
            
            // 找到最近使用的应用
            var mostRecentApp: String? = null
            var mostRecentTime: Long = 0
            
            for (usageStat in usageStats) {
                if (usageStat.lastTimeUsed > mostRecentTime) {
                    mostRecentTime = usageStat.lastTimeUsed
                    mostRecentApp = usageStat.packageName
                }
            }
            
            // 如果最近使用的应用不是我们的应用，且与上次不同，则发出中断信号
            if (mostRecentApp != null && 
                mostRecentApp != ourPackageName && 
                mostRecentApp != lastForegroundApp) {
                
                lastForegroundApp = mostRecentApp
                
                // 检查是否是系统应用或重要应用
                if (!isSystemApp(mostRecentApp)) {
                    notifyInterruption(InterruptionReason.SYSTEM_INTERRUPT)
                }
            }
            
        } catch (e: Exception) {
            // 忽略异常，继续监控
            e.printStackTrace()
        }
    }
    
    private fun isSystemApp(packageName: String): Boolean {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: PackageManager.NameNotFoundException) {
            true // 如果找不到应用信息，认为是系统应用
        }
    }
    
    private fun notifyInterruption(reason: InterruptionReason) {
        _interruptionEvents.tryEmit(reason)
    }
}