# 手机专注农场应用 - 项目概览

## 项目状态

✅ **已完成阶段：**
- 产品需求文档
- 技术架构设计  
- UI/UX设计规范
- Android项目基础结构搭建
- 核心数据模型定义
- 基础UI组件开发
- 导航框架搭建

🚧 **进行中阶段：**
- 核心计时功能实现
- 依赖注入配置

## 技术栈

### 核心技术
- **语言**: Kotlin 100%
- **UI框架**: Jetpack Compose
- **架构**: MVVM + Clean Architecture + Hilt依赖注入
- **数据库**: Room + SQLite
- **异步**: Kotlin Coroutines + Flow
- **最低API**: 29 (Android 10)
- **目标API**: 34 (Android 14)

### 主要依赖库
```kotlin
// UI和动画
implementation "androidx.compose:compose-bom:2024.10.01"
implementation "androidx.compose.ui:ui"
implementation "androidx.compose.material3:material3"

// 3D渲染
implementation "androidx.graphics:graphics-core:1.0.0"

// 数据持久化
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"

// 依赖注入
implementation "com.google.dagger:hilt-android:2.48"

// 协程
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"

// 微信分享
implementation 'com.tencent.mm.opensdk:wechat-sdk-android:6.8.0'
```

## 项目结构

```
app/
├── core/                    # 核心功能模块
│   ├── timer/              # 计时器管理
│   ├── detector/           # 中断检测器
│   ├── permission/         # 权限管理
│   ├── data/               # 数据层
│   ├── notification/       # 通知管理
│   └── service/            # 前台服务
├── feature-farm/           # 农场功能模块
│   ├── render/            # 3D渲染引擎
│   ├── entities/          # 游戏实体
│   ├── ai/                # AI行为系统
│   └── interaction/       # 交互系统
├── feature-stats/          # 统计功能模块
│   ├── charts/            # 图表组件
│   ├── achievements/      # 成就系统
│   └── export/            # 数据导出
├── feature-share/          # 分享功能模块
│   ├── poster/            # 海报生成
│   └── wechat/            # 微信分享
├── ui/                     # UI层
│   ├── components/        # 可复用组件
│   ├── screens/           # 页面
│   └── navigation/        # 导航
├── common/                 # 公共模块
│   ├── models/            # 数据模型
│   ├── constants/         # 常量
│   └── utils/             # 工具类
└── di/                     # 依赖注入
    └── AppModule.kt        # 应用模块
```

## 核心功能实现状态

### 1. 专注计时系统 ✅
- [x] 计时器状态机设计
- [x] 基础计时功能
- [x] 测试模式切换
- [x] 进度计算逻辑
- [ ] 与前台服务集成
- [ ] 中断检测集成

### 2. 数据持久化 ✅
- [x] Room数据库设计
- [x] 实体类定义
- [x] DAO接口设计
- [x] 类型转换器
- [ ] 数据库迁移策略
- [ ] 数据备份恢复

### 3. UI界面 ✅
- [x] 主界面（专注页面）
- [x] 农场界面框架
- [x] 统计界面框架
- [x] 导航框架
- [x] 主题和样式
- [ ] 3D农场渲染
- [ ] 动画效果

### 4. 权限管理 🔄
- [x] 权限常量定义
- [ ] 权限请求逻辑
- [ ] 权限状态监控
- [ ] 系统设置引导

## 下一步开发计划

### 短期目标（1-2周）
1. **完善核心计时功能**
   - 集成前台服务
   - 实现中断检测
   - 添加通知功能

2. **完成权限管理**
   - 实现权限请求流程
   - 添加权限引导界面
   - 处理权限拒绝场景

3. **优化UI体验**
   - 完善农场界面
   - 添加交互动画
   - 优化响应式布局

### 中期目标（2-4周）
1. **3D农场渲染**
   - 实现基础3D渲染
   - 添加动物模型
   - 实现动画系统

2. **数据统计功能**
   - 完善统计界面
   - 添加图表组件
   - 实现成就系统

3. **微信分享集成**
   - 集成微信SDK
   - 实现海报生成
   - 添加分享功能

### 长期目标（4-8周）
1. **性能优化**
   - 优化渲染性能
   - 减少电量消耗
   - 提升用户体验

2. **测试和调试**
   - 单元测试覆盖
   - 集成测试
   - 真机测试

3. **发布准备**
   - 应用签名
   - 发布配置
   - 文档完善

## 开发环境要求

### 必需环境
- Android Studio Arctic Fox (2020.3.1) 或更高版本
- JDK 11 或更高版本
- Android SDK API 34
- Kotlin 2.0.21

### 推荐配置
- 16GB RAM 或更高
- SSD 存储
- 真机测试设备（Mate40 Pro/Mate30）

## 已知问题和限制

1. **当前限制**
   - 3D渲染功能尚未实现
   - 微信分享需要配置AppID
   - 部分权限处理待完善

2. **技术债务**
   - 需要添加更多错误处理
   - 性能监控待实现
   - 单元测试覆盖率待提升

## 贡献指南

### 代码规范
- 遵循Kotlin官方代码规范
- 使用Compose最佳实践
- 保持函数简洁（<50行）
- 添加必要的文档注释

### 提交规范
- 使用清晰的中文提交信息
- 按功能模块组织提交
- 添加适当的测试用例
- 更新相关文档

## 联系方式

- 项目负责人：[待填写]
- 技术负责人：[待填写]
- 项目邮箱：[待填写]

---

**最后更新**: 2025年11月15日
**项目状态**: 开发中 🚧
**完成度**: 约40%