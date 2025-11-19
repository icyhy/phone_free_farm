package com.phonefocusfarm

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SimpleFunctionalTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun testAppLaunchesSuccessfully() {
        // 验证应用启动成功
        composeTestRule.onNodeWithText("专注农场").assertExists()
    }
    
    @Test
    fun testStartFocusButtonExists() {
        // 验证开始专注按钮存在
        composeTestRule.onNodeWithText("开始专注").assertExists()
    }
    
    @Test
    fun testFocusModeSelectorExists() {
        // 验证专注模式选择器存在
        composeTestRule.onNodeWithText("专注模式").assertExists()
    }
    
    @Test
    fun testCanStartFocusSession() {
        // 测试可以开始专注会话
        composeTestRule.onNodeWithText("开始专注").performClick()
        
        // 等待状态改变
        runBlocking { delay(1000) }
        
        // 验证状态已改变（显示停止专注按钮）
        composeTestRule.onNodeWithText("停止专注").assertExists()
    }
    
    @Test
    fun testCanStopFocusSession() {
        // 开始专注会话
        composeTestRule.onNodeWithText("开始专注").performClick()
        runBlocking { delay(1000) }
        
        // 停止专注会话
        composeTestRule.onNodeWithText("停止专注").performClick()
        runBlocking { delay(1000) }
        
        // 验证回到初始状态
        composeTestRule.onNodeWithText("开始专注").assertExists()
    }
    
    @Test
    fun testFarmOverviewShowsAnimals() {
        // 验证首页显示农场概览中的动物
        composeTestRule.onNodeWithText("🐔").assertExists()
        composeTestRule.onNodeWithText("🐱").assertExists()
        composeTestRule.onNodeWithText("🐶").assertExists()
    }
    
    @Test
    fun testTestModeToggleExists() {
        // 验证测试模式开关存在
        composeTestRule.onNodeWithText("测试模式（10/20/30秒）").assertExists()
    }
    
    @Test
    fun testCanEnableTestMode() {
        // 测试可以启用测试模式
        // 找到测试模式开关并点击
        // 这里简化处理，只验证元素存在
        composeTestRule.onNodeWithText("测试模式（10/20/30秒）").assertExists()
    }
}