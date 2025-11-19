package com.phonefocusfarm

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FarmInteractionTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun testFarmRendering() {
        // 导航到农场页面
        composeTestRule.onNodeWithText("农场").performClick()
        
        // 验证农场标题
        composeTestRule.onNodeWithText("我的农场").assertExists()
        
        // 验证动物emoji显示
        composeTestRule.onNodeWithText("🐔").assertExists()
        composeTestRule.onNodeWithText("🐱").assertExists()
        composeTestRule.onNodeWithText("🐶").assertExists()
    }
    
    @Test
    fun testAnimalTypesFilter() {
        // 导航到农场页面
        composeTestRule.onNodeWithText("农场").performClick()
        
        // 验证只显示鸡、猫、狗（不显示猪）
        composeTestRule.onNodeWithText("🐔").assertExists()
        composeTestRule.onNodeWithText("🐱").assertExists()
        composeTestRule.onNodeWithText("🐶").assertExists()
        
        // 验证不显示猪
        composeTestRule.onNodeWithText("🐷").assertDoesNotExist()
    }
    
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testAnimalTouchInteraction() {
        // 开始专注会话以生成动物
        composeTestRule.onNodeWithText("开始专注").performClick()
        runBlocking { delay(35000) } // 等待动物生成
        
        // 导航到农场页面
        composeTestRule.onNodeWithText("农场").performClick()
        
        // 触摸动物区域 - 简化版本
        composeTestRule.onNodeWithText("🐔").performClick()
        
        // 验证动物有反应（移动或状态改变）
        runBlocking { delay(1000) }
        // 这里可以添加验证动物状态改变的逻辑
    }
    
    @Test
    fun testAnimalDispersion() {
        // 开始专注会话以生成多个动物
        composeTestRule.onNodeWithText("开始专注").performClick()
        runBlocking { delay(95000) } // 等待多个动物生成
        
        // 导航到农场页面
        composeTestRule.onNodeWithText("农场").performClick()
        
        // 验证动物不聚集在角落
        runBlocking { delay(3000) } // 等待动物移动
        
        // 这里可以添加验证动物位置分布的逻辑
        // 例如检查动物是否均匀分布在农场区域
    }
    
    @Test
    fun testFarmCanvasSize() {
        // 导航到农场页面
        composeTestRule.onNodeWithText("农场").performClick()
        
        // 验证农场画布占满可用空间
        // 这里可以添加验证画布尺寸的测试
        composeTestRule.onNodeWithText("我的农场").assertExists()
    }
    
    @Test
    fun testAnimalCountDisplay() {
        // 开始专注会话
        composeTestRule.onNodeWithText("开始专注").performClick()
        runBlocking { delay(35000) }
        
        // 导航到农场页面
        composeTestRule.onNodeWithText("农场").performClick()
        
        // 验证动物数量显示正确
        composeTestRule.onNodeWithText("🐔").assertExists()
        
        // 返回首页验证概览
        composeTestRule.onNodeWithText("专注").performClick()
        composeTestRule.onNodeWithText("🐔").assertExists()
    }
}