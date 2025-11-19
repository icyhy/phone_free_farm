package com.phonefocusfarm

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.fetchSemanticsNodes
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.By
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FocusTimerFlowTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun testStartFocusSession() {
        // 测试开始专注会话 - 使用正确的按钮文本
        composeTestRule.onNodeWithText("开始专注").performClick()
        
        // 验证计时器状态改变
        runBlocking { delay(1000) }
        // 检查是否显示停止专注按钮
        composeTestRule.onNodeWithText("停止专注").assertExists()
    }
    
    @Test
    fun testStopFocusSession() {
        // 开始专注会话
        composeTestRule.onNodeWithText("开始专注").performClick()
        runBlocking { delay(2000) }
        
        // 停止专注
        composeTestRule.onNodeWithText("停止专注").performClick()
        // 验证回到初始状态
        runBlocking { delay(1000) }
        composeTestRule.onNodeWithText("开始专注").assertExists()
    }
    
    @Test
    fun testFocusCompletion() {
        // 开始专注会话
        composeTestRule.onNodeWithText("开始专注").performClick()
        
        // 等待会话完成（使用测试模式）
        runBlocking { delay(12000) } // 等待更长时间观察状态变化
        
        // 验证完成状态 - 检查是否显示再次专注
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("再次专注").fetchSemanticsNodes().isNotEmpty()
        }
    }
    
    @Test
    fun testAnimalGenerationDuringFocus() {
        // 开始专注会话
        composeTestRule.onNodeWithText("开始专注").performClick()
        
        // 等待动物生成（每30秒生成一个）
        runBlocking { delay(35000) }
        
        // 导航到农场页面检查动物
        composeTestRule.onNodeWithText("农场").performClick()
        
        // 验证动物数量增加
        composeTestRule.onNodeWithText("🐔").assertExists()
    }
    
    @Test
    fun testTimerCompletion() {
        // 开始1分钟专注会话用于测试
        composeTestRule.onNodeWithText("开始专注").performClick()
        
        // 等待会话完成
        runBlocking { delay(65000) }
        
        // 验证会话完成提示
        composeTestRule.onNodeWithText("专注完成").assertExists()
    }
    
    @Test
    fun testBackgroundPermissionHandling() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        
        // 开始专注会话
        composeTestRule.onNodeWithText("开始专注").performClick()
        
        // 模拟按下Home键
        device.pressHome()
        runBlocking { delay(2000) }
        
        // 返回应用
        device.pressRecentApps()
        runBlocking { delay(1000) }
        
        // 选择应用返回
        val appSelector = UiSelector().text("专注农场")
        val appItem = device.findObject(appSelector)
        if (appItem.exists()) {
            appItem.click()
        }
        
        // 验证应用状态正常
        runBlocking { delay(1000) }
        composeTestRule.onNodeWithText("暂停").assertExists()
    }
}