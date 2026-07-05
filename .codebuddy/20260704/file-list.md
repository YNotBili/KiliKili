# 文件变更列表 - 2026-07-04

## 新增文件

### private 模块
1. `private/build.gradle.kts`
2. `private/proguard-rules.pro`
3. `private/consumer-rules.pro`
4. `private/README.md`
5. `private/src/main/AndroidManifest.xml`
6. `private/src/main/java/rj/kilikili/private/OpenVAHelper.kt`
7. `private/src/main/java/rj/kilikili/private/InstallResultWrapper.kt`
8. `private/src/main/java/rj/kilikili/private/ExampleUsage.kt`
9. `private/src/main/java/rj/kilikili/private/syscall/SyscallType.kt`
10. `private/src/main/java/rj/kilikili/private/syscall/SyscallFilter.kt`
11. `private/src/main/java/rj/kilikili/private/syscall/SyscallFilterManager.kt`
12. `private/src/main/java/rj/kilikili/private/syscall/SyscallLogger.kt`

### app 模块
13. `app/src/main/java/rj/kilikili/ui/screens/openva/OpenVAScreen.kt`
14. `app/src/main/java/rj/kilikili/ui/screens/openva/SyscallFilterScreen.kt`

## 修改文件

### 配置文件
15. `settings.gradle.kts` - 添加 `include(":private")`
16. `app/build.gradle.kts` - 添加 `implementation(project(":private"))`
17. `BiliWebApi/build.gradle.kts` - Gson 改为 `compileOnly`
18. `app/proguard-rules.pro` - 添加 `-dontwarn me.weishu.reflection.Reflection`

### 源代码
19. `app/src/main/java/rj/kilikili/KiliKili.kt` - 添加 OpenVA 初始化
20. `app/src/main/java/rj/kilikili/ui/navigation/Routes.kt` - 添加 OpenVA 和 SyscallFilter 路由
21. `app/src/main/java/rj/kilikili/ui/screens/main/MainScreen.kt` - 添加页面路由
22. `app/src/main/java/rj/kilikili/ui/screens/video/VideoDetailScreen.kt` - 修复封面图片
23. `app/src/main/java/rj/kilikili/ui/screens/popular/PopularSeriesDetailScreen.kt` - 修复图片
24. `app/src/main/java/rj/kilikili/ui/screens/favorite/FavoriteScreen.kt` - 修复图片
25. `app/src/main/java/rj/kilikili/ui/screens/search/SearchResultScreen.kt` - 修复图片

### 资源文件
26. `app/src/main/res/values/strings.xml` - 添加 OpenVA 和系统调用过滤器相关的字符串资源

## 文件说明

### private/build.gradle.kts
private 模块的构建配置文件，包含 OpenVA 依赖配置。namespace 为 `rj.kilikili.openva`。

### private/proguard-rules.pro
private 模块的 ProGuard 规则文件，保留 OpenVA 和 BlackReflection 类。

### private/consumer-rules.pro
private 模块的消费者 ProGuard 规则文件。

### private/README.md
private 模块的 README 文档，包含使用说明和 API 参考。

### private/src/main/AndroidManifest.xml
private 模块的 AndroidManifest 文件，包名为 `rj.kilikili.openva`。

### private/src/main/java/rj/kilikili/private/OpenVAHelper.kt
OpenVA 辅助类，提供简单的 API 封装。包名为 `rj.kilikili.private`。

### private/src/main/java/rj/kilikili/private/InstallResultWrapper.kt
InstallResult 包装类，使 InstallResult 对 app 模块可见。

### private/src/main/java/rj/kilikili/private/ExampleUsage.kt
OpenVA 使用示例。

### private/src/main/java/rj/kilikili/private/syscall/SyscallType.kt
系统调用枚举类型，定义了 Android 框架中常见的系统调用。

### private/src/main/java/rj/kilikili/private/syscall/SyscallFilter.kt
系统调用过滤器接口和相关类。

### private/src/main/java/rj/kilikili/private/syscall/SyscallFilterManager.kt
系统调用过滤器管理器。

### private/src/main/java/rj/kilikili/private/syscall/SyscallLogger.kt
系统调用日志记录器。

### app/src/main/java/rj/kilikili/ui/screens/openva/OpenVAScreen.kt
OpenVA 管理界面。

### app/src/main/java/rj/kilikili/ui/screens/openva/SyscallFilterScreen.kt
系统调用过滤器配置界面。

### settings.gradle.kts
添加了 `include(":private")`。

### app/build.gradle.kts
添加了 `implementation(project(":private"))`。

### BiliWebApi/build.gradle.kts
Gson 从 `implementation` 改为 `compileOnly`。

### app/proguard-rules.pro
添加了 `-dontwarn me.weishu.reflection.Reflection`。

### KiliKili.kt
添加了 OpenVA 初始化代码。

### Routes.kt
添加了 OpenVA 和 SyscallFilter 路由。

### MainScreen.kt
添加了 OpenVA 和 SyscallFilter 页面路由。

### strings.xml
添加了 OpenVA 和系统调用过滤器相关的字符串资源。

### VideoDetailScreen.kt
修复封面图片加载（添加 `toHttpsUrl()`）。

### PopularSeriesDetailScreen.kt
修复图片加载（添加 `toHttpsUrl()`）。

### FavoriteScreen.kt
修复图片加载（添加 `toHttpsUrl()`）。

### SearchResultScreen.kt
修复图片加载（添加 `toHttpsUrl()`）。

## 统计信息

- 新增文件：14 个
- 修改文件：12 个
- 总计：26 个文件