#!/bin/bash

# 专注农场自动化测试脚本
# 用于在模拟器上运行全面的功能测试

echo "=== 专注农场自动化测试开始 ==="
echo "时间: $(date)"

# 设置环境变量
export ANDROID_HOME=/Volumes/doc/home/Documents/2025/phone_free_farm/PhoneFocusFarm/tools/android-sdk
export JAVA_HOME=/Volumes/doc/home/Documents/2025/phone_free_farm/PhoneFocusFarm/tools/jdk-17.jdk/Contents/Home
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools

# 检查模拟器连接
echo "检查模拟器连接状态..."
adb devices

# 等待模拟器完全启动
echo "等待模拟器完全启动..."
adb wait-for-device
sleep 5

# 解锁屏幕（如果需要）
echo "解锁屏幕..."
adb shell input keyevent 82
sleep 2

# 清理之前的测试结果
echo "清理之前的测试结果..."
rm -rf app/build/reports/androidTests
rm -rf app/build/outputs/androidTest-results

# 运行所有测试
echo "开始运行自动化测试..."
echo "1. 运行启动和导航测试..."
./gradlew connectedAndroidTest --tests="*.LaunchAndNavigationTest" --continue

echo "2. 运行专注计时器流程测试..."
./gradlew connectedAndroidTest --tests="*.FocusTimerFlowTest" --continue

echo "3. 运行农场交互测试..."
./gradlew connectedAndroidTest --tests="*.FarmInteractionTest" --continue

echo "4. 运行通知测试..."
./gradlew connectedAndroidTest --tests="*.NotificationsTest" --continue

# 运行所有测试（汇总）
echo "运行所有测试（汇总模式）..."
./gradlew connectedAndroidTest --continue

# 收集测试结果
echo "收集测试结果..."
TEST_RESULTS_DIR="test_results_$(date +%Y%m%d_%H%M%S)"
mkdir -p $TEST_RESULTS_DIR

# 复制测试报告
if [ -d "app/build/reports/androidTests" ]; then
    cp -r app/build/reports/androidTests $TEST_RESULTS_DIR/reports
fi

# 复制测试结果
if [ -d "app/build/outputs/androidTest-results" ]; then
    cp -r app/build/outputs/androidTest-results $TEST_RESULTS_DIR/results
fi

# 生成测试摘要
echo "生成测试摘要..."
cat > $TEST_RESULTS_DIR/test_summary.txt << EOF
专注农场自动化测试报告
生成时间: $(date)

测试环境:
- 模拟器: Android Emulator
- 测试框架: Espresso + Compose UI Test
- 测试类型: 功能测试、UI测试、集成测试

测试覆盖范围:
1. 应用启动和页面导航
2. 专注计时器功能（开始、暂停、恢复、重置）
3. 动物生成机制
4. 农场渲染和交互
5. 通知系统
6. 权限处理

测试结果:
- 详细报告请查看 reports 目录
- 原始结果请查看 results 目录

EOF

# 截屏保存当前状态
echo "保存当前屏幕截图..."
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png $TEST_RESULTS_DIR/screenshot.png
adb shell rm /sdcard/screenshot.png

# 收集日志
echo "收集应用日志..."
adb logcat -d > $TEST_RESULTS_DIR/logcat.txt

# 显示测试摘要
echo ""
echo "=== 测试完成摘要 ==="
echo "测试结果保存在: $TEST_RESULTS_DIR"
echo ""

# 检查测试结果
if [ -d "$TEST_RESULTS_DIR/reports" ]; then
    echo "✓ 测试报告已生成"
else
    echo "✗ 测试报告生成失败"
fi

if [ -f "$TEST_RESULTS_DIR/screenshot.png" ]; then
    echo "✓ 屏幕截图已保存"
else
    echo "✗ 屏幕截图保存失败"
fi

if [ -f "$TEST_RESULTS_DIR/logcat.txt" ]; then
    echo "✓ 应用日志已收集"
else
    echo "✗ 应用日志收集失败"
fi

echo ""
echo "=== 自动化测试完成 ==="
echo "请查看 $TEST_RESULTS_DIR 目录获取详细结果"