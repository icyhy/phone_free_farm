# PhoneFocusFarm - Android Studio 构建指南

## 📱 项目概述
PhoneFocusFarm 是一个创新的专注力管理应用，结合了3D农场渲染、动物AI行为和丰富的交互反馈系统。

## ✨ 主要功能
- **3D农场渲染**: 使用OpenGL ES 2.0实现的真实3D农场环境
- **动物AI系统**: 智能动物行为模拟，包括idle、wandering、chasing、fleeing、grazing、sleeping、playing等状态
- **丰富动画**: 缩放、淡入淡出、旋转、弹跳、震动等多种动画效果
- **音效系统**: 完整的音效管理，包括背景音、交互音、成就音效
- **专注计时器**: 基于番茄工作法的专注时间管理
- **微信集成**: 社交分享功能

## 🔧 开发环境要求

### 必需环境
- **Android Studio**: Arctic Fox (2020.3.1) 或更高版本
- **Android SDK**: API 29 (Android 10) 或更高版本
- **JDK**: 11 或更高版本
- **Gradle**: 8.0 或更高版本

### 推荐配置
- **RAM**: 8GB 或更高
- **存储**: 至少 5GB 可用空间
- **GPU**: 支持OpenGL ES 2.0

## 🚀 构建步骤

### 1. 环境准备
```bash
# 检查Java版本
java -version

# 检查Android SDK
ls $ANDROID_HOME

# 检查Gradle
gradle --version
```

### 2. 项目导入
1. 打开Android Studio
2. 选择 "Open an Existing Project"
3. 导航到项目根目录：`/Volumes/doc/home/Documents/2025/phone_free_farm/PhoneFocusFarm`
4. 点击 "OK" 等待项目同步

### 3. 依赖项同步
```bash
# 在项目根目录运行
./gradlew build --refresh-dependencies
```

### 4. 构建APK
#### 调试版本 (Debug)
```bash
./gradlew assembleDebug
```

#### 发布版本 (Release)
```bash
./gradlew assembleRelease
```

### 5. 构建输出
构建完成后，APK文件位于：
- **调试版本**: `app/build/outputs/apk/debug/app-debug.apk`
- **发布版本**: `app/build/outputs/apk/release/app-release.apk`

## 📁 项目结构

```
PhoneFocusFarm/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/phonefocusfarm/
│   │       │   ├── MainActivity.kt                 # 主活动
│   │       │   ├── common/                          # 通用组件
│   │       │   ├── core/                              # 核心功能
│   │       │   ├── di/                                # 依赖注入
│   │       │   └── ui/                              # UI组件
│   │       │       ├── components/                    # 基础组件
│   │       │       │   ├── AnimationManager.kt      # 动画管理器
│   │       │       │   ├── SoundManager.kt          # 音效管理器
│   │       │       │   ├── FeedbackSystem.kt        # 反馈系统
│   │       │       │   └── farm3d/                  # 3D农场组件
│   │       │       │       ├── EnhancedFarm3DScreen.kt
│   │       │       │       ├── Farm3DRenderer.kt
│   │       │       │       ├── Animal3D.kt
│   │       │       │       └── ai/                  # AI行为系统
│   │       └── res/                                 # 资源文件
├── build.gradle                                       # 项目构建配置
├── gradle.properties                                  # Gradle属性
└── settings.gradle                                    # 项目设置
```

## 🔍 关键依赖项

### UI和动画
```gradle
implementation "androidx.compose.ui:ui:1.5.4"
implementation "androidx.compose.material3:material3:1.1.2"
implementation "androidx.compose.animation:animation:1.5.4"
```

### 依赖注入
```gradle
implementation "com.google.dagger:hilt-android:2.48"
kapt "com.google.dagger:hilt-compiler:2.48"
```

### 数据存储
```gradle
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"
```

### 协程
```gradle
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1"
```

### 微信SDK
```gradle
implementation "com.tencent.mm.opensdk:wechat-sdk-android:6.8.0"
```

## 🎯 构建变体

### Debug版本
- 启用调试模式
- 包含调试符号
- 禁用代码混淆
- 启用日志输出

### Release版本
- 启用代码混淆
- 优化APK大小
- 禁用调试日志
- 启用签名验证

## ⚠️ 常见问题

### 1. 构建失败
```bash
# 清理并重新构建
./gradlew clean build
```

### 2. 依赖项冲突
```bash
# 查看依赖树
./gradlew dependencies
```

### 3. 内存不足
```bash
# 增加Gradle内存
export GRADLE_OPTS="-Xmx4g -Xms2g"
```

### 4. OpenGL ES问题
确保设备支持OpenGL ES 2.0，在`AndroidManifest.xml`中添加：
```xml
<uses-feature android:glEsVersion="0x00020000" android:required="true" />
```

## 📱 设备兼容性

### 最低要求
- **Android版本**: 10 (API 29)
- **RAM**: 2GB
- **存储**: 100MB可用空间
- **GPU**: OpenGL ES 2.0支持

### 推荐配置
- **Android版本**: 11 (API 30)或更高
- **RAM**: 4GB或更高
- **存储**: 500MB可用空间
- **GPU**: OpenGL ES 3.0支持

## 🔧 性能优化

### 构建优化
- 启用增量编译
- 使用构建缓存
- 优化依赖项
- 并行构建

### 运行时优化
- 3D渲染优化
- 内存管理
- 电池优化
- 后台任务管理

## 📊 构建统计

基于当前代码库的预估构建统计：
- **构建时间**: 2-5分钟（取决于机器性能）
- **APK大小**: ~15.2MB
- **方法数**: ~8,000
- **依赖库**: 25+

## 🚀 下一步

构建完成后，您可以：
1. 在Android设备上安装APK进行测试
2. 发布到应用商店
3. 继续开发新功能
4. 优化性能和用户体验

## 📞 支持

如果在构建过程中遇到问题，请检查：
1. 开发环境配置
2. 依赖项版本兼容性
3. 网络连接（用于下载依赖）
4. 系统资源（内存、存储）

---

**祝您构建顺利！🎉**