package com.phonefocusfarm.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.phonefocusfarm.MainActivity
import com.phonefocusfarm.R
import com.phonefocusfarm.common.constants.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FocusTimerService : Service() {
    
    @Inject
    lateinit var notificationManager: NotificationManager
    
    private var startTime: Long = 0
    private var duration: Long = 0
    private var isRunning = false
    private var updateThread: Thread? = null
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> {
                startTime = intent.getLongExtra(EXTRA_START_TIME, System.currentTimeMillis())
                duration = intent.getLongExtra(EXTRA_DURATION, 0)
                startForegroundService()
            }
            ACTION_STOP_TIMER -> stopForegroundService()
            ACTION_UPDATE_NOTIFICATION -> {
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "专注进行中"
                val content = intent.getStringExtra(EXTRA_CONTENT) ?: "计时中..."
                updateNotification(title, content)
            }
            ACTION_UPDATE_PROGRESS -> {
                val progress = intent.getFloatExtra(EXTRA_PROGRESS, 0f)
                val remainingTime = intent.getLongExtra(EXTRA_REMAINING_TIME, 0)
                updateProgressNotification(progress, remainingTime)
            }
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppConstants.NOTIFICATION_CHANNEL_FOCUS,
                getString(R.string.notification_channel_focus),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "专注计时通知"
                setShowBadge(false)
                enableVibration(false)
                setSound(null, null)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun startForegroundService() {
        isRunning = true
        val notification = createNotification("专注进行中", "计时开始")
        startForeground(AppConstants.NOTIFICATION_ID_FOCUS, notification)
        
        // 启动更新线程
        startUpdateThread()
    }
    
    private fun stopForegroundService() {
        isRunning = false
        updateThread?.interrupt()
        updateThread = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    private fun updateNotification(title: String, content: String) {
        val notification = createNotification(title, content)
        notificationManager.notify(AppConstants.NOTIFICATION_ID_FOCUS, notification)
    }
    
    private fun updateProgressNotification(progress: Float, remainingTime: Long) {
        val minutes = (remainingTime / 60000).toInt()
        val seconds = ((remainingTime % 60000) / 1000).toInt()
        val timeString = String.format("%02d:%02d", minutes, seconds)
        
        val content = "剩余时间: $timeString"
        val notification = createProgressNotification("专注进行中", content, progress)
        notificationManager.notify(AppConstants.NOTIFICATION_ID_FOCUS, notification)
    }
    
    private fun startUpdateThread() {
        updateThread = Thread {
            while (isRunning && !Thread.currentThread().isInterrupted) {
                try {
                    Thread.sleep(1000) // 每秒更新一次
                    
                    if (isRunning) {
                        val elapsed = System.currentTimeMillis() - startTime
                        val minutes = (elapsed / 60000).toInt()
                        val seconds = ((elapsed % 60000) / 1000).toInt()
                        val timeString = String.format("%02d:%02d", minutes, seconds)
                        
                        val notification = createNotification("专注进行中", "已专注: $timeString")
                        notificationManager.notify(AppConstants.NOTIFICATION_ID_FOCUS, notification)
                    }
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
        updateThread?.start()
    }
    
    private fun createNotification(title: String, content: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, AppConstants.NOTIFICATION_CHANNEL_FOCUS)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(false)
            .build()
    }
    
    private fun createProgressNotification(title: String, content: String, progress: Float): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val progressPercent = (progress * 100).toInt()
        
        return NotificationCompat.Builder(this, AppConstants.NOTIFICATION_CHANNEL_FOCUS)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(false)
            .setProgress(100, progressPercent, false)
            .build()
    }
    
    companion object {
        const val ACTION_START_TIMER = "START_TIMER"
        const val ACTION_STOP_TIMER = "STOP_TIMER"
        const val ACTION_UPDATE_NOTIFICATION = "UPDATE_NOTIFICATION"
        const val ACTION_UPDATE_PROGRESS = "UPDATE_PROGRESS"
        
        // Extra keys
        const val EXTRA_START_TIME = "START_TIME"
        const val EXTRA_DURATION = "DURATION"
        const val EXTRA_TITLE = "TITLE"
        const val EXTRA_CONTENT = "CONTENT"
        const val EXTRA_PROGRESS = "PROGRESS"
        const val EXTRA_REMAINING_TIME = "REMAINING_TIME"
        
        fun createStartIntent(context: Context, startTime: Long, duration: Long = 0): Intent {
            return Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_START_TIMER
                putExtra(EXTRA_START_TIME, startTime)
                putExtra(EXTRA_DURATION, duration)
            }
        }
        
        fun createStopIntent(context: Context): Intent {
            return Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_STOP_TIMER
            }
        }
        
        fun createUpdateIntent(context: Context, title: String, content: String): Intent {
            return Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_UPDATE_NOTIFICATION
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_CONTENT, content)
            }
        }
        
        fun createProgressIntent(context: Context, progress: Float, remainingTime: Long): Intent {
            return Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_UPDATE_PROGRESS
                putExtra(EXTRA_PROGRESS, progress)
                putExtra(EXTRA_REMAINING_TIME, remainingTime)
            }
        }
    }
}