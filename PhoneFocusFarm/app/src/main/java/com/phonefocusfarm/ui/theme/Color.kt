package com.phonefocusfarm.ui.theme

import androidx.compose.ui.graphics.Color

// 应用色彩定义 - 基于设计规范
object AppColors {
    // 主品牌色
    val Primary = Color(0xFF4CAF50)        // 绿色 - 代表农场和自然
    val PrimaryVariant = Color(0xFF388E3C) // 深绿色
    
    // 辅助色
    val Secondary = Color(0xFFFFA726)      // 橙色 - 温暖和活力
    val SecondaryVariant = Color(0xFFFF9800) // 深橙色
    
    // 动物颜色
    val Chicken = Color(0xFFFFEB3B)         // 小鸡 - 明黄色
    val Cat = Color(0xFFFF6F00)             // 小猫 - 橙黄色
    val Dog = Color(0xFF8D6E63)             // 小狗 - 棕褐色
    
    // 状态色
    val Success = Color(0xFF4CAF50)         // 成功 - 绿色
    val Warning = Color(0xFFFF9800)         // 警告 - 橙色
    val Error = Color(0xFFF44336)           // 错误 - 红色
    val Info = Color(0xFF2196F3)            // 信息 - 蓝色
    
    // 背景色
    val Background = Color(0xFFF5F5F5)      // 浅灰色背景
    val Surface = Color(0xFFFFFFFF)       // 白色表面
    
    // 文字色
    val OnPrimary = Color(0xFFFFFFFF)       // 主色上的白色文字
    val OnSecondary = Color(0xFF000000)     // 辅助色上的黑色文字
    val OnBackground = Color(0xFF212121)    // 背景上的深灰文字
    val OnSurface = Color(0xFF424242)       // 表面上的中灰文字
}

// 深色模式色彩
object DarkAppColors {
    val Primary = Color(0xFF81C784)         // 浅绿色
    val PrimaryVariant = Color(0xFF66BB6A)   // 中绿色
    val Secondary = Color(0xFFFFB74D)       // 浅橙色
    val SecondaryVariant = Color(0xFFFFA726) // 中橙色
    
    val Background = Color(0xFF121212)      // 深灰色背景
    val Surface = Color(0xFF1E1E1E)       // 稍浅的表面
    val OnBackground = Color(0xFFE0E0E0)     // 浅灰文字
    val OnSurface = Color(0xFFBDBDBD)      // 中灰文字
}

// 临时颜色定义 - 用于开发阶段
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)