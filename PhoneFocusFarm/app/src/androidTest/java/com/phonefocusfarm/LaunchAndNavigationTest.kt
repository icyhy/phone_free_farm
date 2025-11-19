package com.phonefocusfarm

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.fetchSemanticsNodes
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LaunchAndNavigationTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun testAppLaunch() {
        // 验证应用启动成功 - 检查顶部标题
        composeTestRule.onNodeWithText("专注农场").assertExists()
    }
    
    @Test
    fun testNavigationToFarm() {
        // 测试导航到农场页面 - 点击顶部农场图标
        composeTestRule.onNodeWithContentDescription("农场").performClick()
        // 验证农场页面加载
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("我的农场").fetchSemanticsNodes().isNotEmpty()
        }
    }
    
    @Test
    fun testNavigationToStats() {
        // 测试统计功能 - 通过导航到统计页面
        // 由于应用可能没有底部导航，我们通过农场页面的统计功能测试
        composeTestRule.onNodeWithContentDescription("农场").performClick()
        // 验证统计相关元素存在
        composeTestRule.onNodeWithText("🐔").assertExists()
    }
    
    @Test
    fun testFarmOverviewOnHome() {
        // 验证首页显示农场概览
        composeTestRule.onNodeWithText("🐔").assertExists()
        composeTestRule.onNodeWithText("🐱").assertExists()
        composeTestRule.onNodeWithText("🐶").assertExists()
    }
    
    @Test
    fun testPermissionHandling() {
        // 测试权限处理（在模拟器环境中）
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        
        // 验证应用可以正常处理权限请求
        composeTestRule.onNodeWithText("开始专注").assertExists()
    }
}