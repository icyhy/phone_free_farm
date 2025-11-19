# 手机专注农场（PhoneFocusFarm）

一个帮助用户保持专注的轻量级应用。用户在专注期间禁止触摸、移动或切换应用；专注结束后，应用会依据本次专注时长一次性在农场投放对应的小动物（小鸡/小猫/小狗）。农场中的动物在一个周期内持续累积，用户可在统计页生成农场海报并分享。

## 功能特性
- 严格专注模式：
  - 触摸屏幕、设备明显移动、应用进入后台（Home/Overview）、解锁屏幕、系统中断均会中断计时
  - 专注完成后按本次时长一次性投放动物（不在计时过程中投放/升级）
- 动物累积与农场周期：
  - 动物在一个周期内持续累积，重新开始专注时计时从 0 开始，动物不清空
  - 首页显示“当前农场周期”卡片（开始日期、结束日期、累计专注时长）与“动物概览”卡片
- 实时概览与预测：
  - 专注进行中，首页动物概览实时显示当前累积总数 + 基于当前时长的“预计新增”数量（显示后端累积时与停止后持久化一致）
- 分享农场海报：
  - 统计页“分享农场海报”支持使用农场页面的区域截图作为海报背景内容；若无截图则使用内置草地背景与动物分布图
- 设置项：
  - 测试模式开关：将动物阶段时长改为 10/20/30 秒，便于快速验证
  - 允许暂停开关：控制主页是否展示“暂停/继续”按钮
  - 动物阶段基础时长（滑块）：作为“鸡/猫/狗”分解的基础时长（分别为 1×、2×、3×）

## 动物投放与计数规则
- 不保留升级链，仅三类动物：小鸡（CHICKEN）、小猫（CAT）、小狗（DOG）
- 计时停止时按贪心分解投放（顺序固定：狗→猫→鸡）：
  - 以基础阶段时长 `T`（测试模式：10s；正常模式：设置页滑块值）
  - 狗数量 = `duration / (3T)`；剩余 `duration %= 3T`
  - 猫数量 = `duration / (2T)`；剩余 `duration %= 2T`
  - 鸡数量 = `duration / T`
- 累积策略：每次专注结束将投放的动物写入数据库，农场总数持续累积

## 关键模块
- `core/timer/TimerManager.kt`
  - 计时状态（`TimerState`）与前台服务管理
  - 中断处理（触摸、移动、后台、解锁、系统中断等）
  - 停止计时时按照本次时长分解并批量插入动物
- `core/detector/InterruptionDetector.kt`
  - 全局触摸监听（Window.Callback）、传感器事件、应用生命周期与 UI 隐藏事件（`ComponentCallbacks2`）
  - 统一上报 `APP_BACKGROUND/TOUCH_EVENT/DEVICE_MOVEMENT` 等中断原因
- `ui/screens/focus/FocusScreen.kt`
  - 周期卡片与动物概览卡片（实时显示累积 + 预计新增）
  - 计时器显示与控制按钮（开始/暂停/继续/再次专注/重置）
- `ui/screens/farm/FarmScreen.kt`
  - 农场画布渲染与动物随机移动（仅鸡/猫/狗三类）
  - 采集农场区域截图用于海报分享
- `ui/screens/stats/StatsScreen.kt`
  - 周期汇总与时间线式记录列表
  - 分享农场海报（优先使用农场区域截图）
- `feature/share/ShareUtil.kt`
  - 海报生成与保存、系统/微信分享（通过系统分享面板）

## 构建与安装
```bash
# 构建 Debug APK（MacOS）
cd PhoneFocusFarm
./gradlew assembleDebug

# 安装到指定设备（请先启用 USB 调试）
adb -s <device_id> install -r app/build/outputs/apk/debug/app-debug.apk
```

## 日志监控（调试中断）
```bash
# 关键标签仅显示
adb -s <device_id> logcat -v time -s InterruptionDetector TimerManager

# 触发流程建议
# 1) 启动应用 → 开始专注
# 2) 触摸屏幕/按Home/Overview → 观察中断日志
```

## 使用说明
- 首页：
  - 周期卡片显示开始/结束日期（不含时分）与累计专注时长
  - 动物概览显示当前累积总数；专注中实时叠加预计新增数量
- 统计页：
  - 查看累计统计与时间线记录（模式、结果、时长、奖励）
  - 点击“分享农场海报”生成海报图片并通过系统分享
- 设置页：
  - 测试模式开关（10/20/30秒）
  - 允许暂停（控制主页是否展示暂停/继续按钮）
  - 动物阶段基础时长（作为鸡/猫/狗分解的基础单位）

## 已知约束
- 微信分享通过系统分享面板触发，依赖设备上微信客户端及系统权限
- 传感器/生命周期检测在少数 ROM 上触发时机可能略有差异；已通过 UI 隐藏与进程生命周期事件增加容错

## 目录结构（节选）
```
PhoneFocusFarm/
  app/src/main/java/com/phonefocusfarm/
    core/
      timer/TimerManager.kt
      detector/InterruptionDetector.kt
    ui/screens/focus/FocusScreen.kt
    ui/screens/farm/FarmScreen.kt
    ui/screens/stats/StatsScreen.kt
    ui/screens/settings/SettingsScreen.kt
    feature/share/ShareUtil.kt
```

## 许可证
- 暂未设置，可根据项目需要添加（如 MIT/Apache-2.0 等）。