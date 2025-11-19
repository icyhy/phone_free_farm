package com.phonefocusfarm

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PhoneFocusFarmApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 初始化全局配置
        initializeApp()
    }
    
    private fun initializeApp() {
        // 初始化通知渠道
        // 初始化崩溃报告
        // 初始化性能监控
        // 初始化微信SDK
    }
}