package com.phonefocusfarm.common.constants

object AppConstants {
    // 应用配置
    const val APP_NAME = "专注农场"
    const val DATABASE_NAME = "focus_farm.db"
    const val SHARED_PREFS_NAME = "focus_farm_prefs"
    
    // 计时器配置
    const val TIMER_INTERVAL_MS = 1000L  // 1秒
    const val INCUBATION_CHICKEN_MS = 15 * 60 * 1000L  // 15分钟
    const val INCUBATION_CAT_MS = 30 * 60 * 1000L      // 30分钟
    const val INCUBATION_DOG_MS = 60 * 60 * 1000L      // 60分钟
    
    // 测试模式配置
    const val TEST_MODE_CHICKEN_MS = 10 * 1000L        // 10秒
    const val TEST_MODE_CAT_MS = 20 * 1000L            // 20秒
    const val TEST_MODE_DOG_MS = 30 * 1000L            // 30秒
    
    // 通知配置
    const val NOTIFICATION_CHANNEL_FOCUS = "focus_timer"
    const val NOTIFICATION_CHANNEL_ACHIEVEMENT = "achievement"
    const val NOTIFICATION_ID_FOCUS = 1001
    const val NOTIFICATION_ID_ACHIEVEMENT = 1002
    
    // 前台服务配置
    const val SERVICE_ID_FOCUS_TIMER = 2001
    
    // 动物配置
    const val MAX_ANIMALS_COUNT = 50
    const val ANIMAL_SPEED_CHICKEN = 50f
    const val ANIMAL_SPEED_CAT = 60f
    const val ANIMAL_SPEED_DOG = 70f
    const val ANIMAL_SIZE_CHICKEN = 30f
    const val ANIMAL_SIZE_CAT = 40f
    const val ANIMAL_SIZE_DOG = 50f
    
    // 传感器配置
    const val SENSOR_THRESHOLD_MOVEMENT = 2.0f  // 移动检测阈值
    const val SENSOR_SAMPLE_RATE_MS = 100L      // 传感器采样率
    
    // 分享配置
    const val SHARE_IMAGE_QUALITY = 90
    const val SHARE_IMAGE_MAX_SIZE = 1024 * 1024  // 1MB
    
    // 数据库配置
    const val DATABASE_VERSION = 2
    
    // 动画配置
    const val ANIMATION_DURATION_MS = 300
    const val FRAME_RATE_TARGET = 60
    const val FRAME_RATE_MIN = 45
    
    // 权限请求码
    const val REQUEST_CODE_USAGE_STATS = 3001
    const val REQUEST_CODE_NOTIFICATIONS = 3002
    const val REQUEST_CODE_BATTERY_OPTIMIZATION = 3003
    
    // 微信分享配置
    const val WECHAT_APP_ID = "your_wechat_app_id"  // 需要替换为实际的微信AppID
}