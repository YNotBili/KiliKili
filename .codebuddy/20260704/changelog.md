# Changelog - 2026-07-04

## 新增功能

### OpenVA 集成
- 创建 `private` 模块用于集成 OpenVA 虚拟应用引擎
- 实现 `OpenVAHelper` 类，提供简单的 API 封装
- 支持安装、启动、卸载虚拟应用
- 支持多用户（多开）管理

### 系统调用过滤器
- 实现系统调用枚举类型（`SyscallType`），覆盖 Android 框架中常见的系统调用
- 实现过滤器接口（`SyscallFilter`），支持允许、拒绝、重定向、模拟四种过滤结果
- 实现过滤器管理器（`SyscallFilterManager`），支持基于规则的过滤器配置
- 实现日志记录器（`SyscallLogger`），记录所有系统调用的过滤结果
- 提供预设过滤器：允许全部、拒绝全部、隐私保护、仅网络

### UI 界面
- 创建 `OpenVAScreen`，用于管理虚拟应用
- 创建 `SyscallFilterScreen`，用于配置系统调用过滤器规则
- 支持查看系统调用列表、日志、统计信息

### Bug 修复
- 修复视频详情页封面图片加载问题（HTTP/HTTPS 协议处理）
- 修复收藏夹、搜索结果、热门系列详情页的图片加载问题

## 新增文件

### private 模块
```
private/
├── build.gradle.kts
├── proguard-rules.pro
├── consumer-rules.pro
├── README.md
└── src/main/
    ├── AndroidManifest.xml
    └── java/rj/kilikili/private/
        ├── OpenVAHelper.kt
        ├── InstallResultWrapper.kt
        ├── ExampleUsage.kt
        └── syscall/
            ├── SyscallType.kt
            ├── SyscallFilter.kt
            ├── SyscallFilterManager.kt
            └── SyscallLogger.kt
```

### app 模块
```
app/src/main/java/rj/kilikili/ui/screens/openva/
├── OpenVAScreen.kt
└── SyscallFilterScreen.kt
```

## 修改文件

### 配置文件
- `settings.gradle.kts`: 添加 `include(":private")`
- `app/build.gradle.kts`: 添加 `implementation(project(":private"))`
- `BiliWebApi/build.gradle.kts`: Gson 改为 `compileOnly`
- `app/proguard-rules.pro`: 添加 `-dontwarn me.weishu.reflection.Reflection`

### 源代码
- `app/src/main/java/rj/kilikili/KiliKili.kt`: 添加 OpenVA 初始化
- `app/src/main/java/rj/kilikili/ui/navigation/Routes.kt`: 添加 OpenVA 和 SyscallFilter 路由
- `app/src/main/java/rj/kilikili/ui/screens/main/MainScreen.kt`: 添加页面路由
- `app/src/main/java/rj/kilikili/ui/screens/video/VideoDetailScreen.kt`: 修复封面图片
- `app/src/main/java/rj/kilikili/ui/screens/popular/PopularSeriesDetailScreen.kt`: 修复图片
- `app/src/main/java/rj/kilikili/ui/screens/favorite/FavoriteScreen.kt`: 修复图片
- `app/src/main/java/rj/kilikili/ui/screens/search/SearchResultScreen.kt`: 修复图片

### 资源文件
- `app/src/main/res/values/strings.xml`: 添加 OpenVA 和系统调用过滤器相关的字符串资源

## 依赖变更

### private 模块
```kotlin
implementation("rj.openva:Bcore:5.0.0")
implementation("rj.openva:black-reflection:5.0.0")
ksp("rj.openva:compiler-ksp:5.0.0")
compileOnly(libs.google.gson)
implementation(libs.androidx.constraintlayout)
```

### BiliWebApi 模块
```kotlin
compileOnly(libs.google.gson)  // 原为 implementation
```

### app 模块
```kotlin
implementation(project(":private"))
implementation(libs.google.gson)  // 统一提供运行时 Gson
```

## 技术细节

### 包名架构
- Android namespace: `rj.kilikili.openva`（因为 `private` 是 Java 关键字）
- Kotlin 包名: `rj.kilikili.private`
- 源码路径: `private/src/main/java/rj/kilikili/private/`

### InstallResult 封装
- 由于 `InstallResult` 类在 app 模块中不可见
- 创建 `InstallResultWrapper` 类进行封装
- 提供 `success`、`packageName`、`message` 属性

### Gson 依赖策略
- `BiliWebApi` 和 `private` 模块使用 `compileOnly`（仅编译时可见）
- `app` 模块使用 `implementation`（打包到 APK）
- 避免 R8 构建时的重复类错误

### 图片加载修复
- B 站图片 URL 可能使用 `//` 开头（协议相对 URL）
- 使用 `toHttpsUrl()` 扩展函数统一转换为 HTTPS
- 修复了 VideoDetailScreen、PopularSeriesDetailScreen、FavoriteScreen、SearchResultScreen

## 编译状态

- ✅ private 模块编译成功
- ✅ app 模块编译成功
- ✅ Release 构建成功
- ✅ 所有功能已实现并集成