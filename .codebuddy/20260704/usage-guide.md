# OpenVA 使用指南

## 快速开始

### 1. 初始化 OpenVA

在 `Application.onCreate()` 中初始化：

```kotlin
import rj.kilikili.private.OpenVAHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        OpenVAHelper.init(this)
    }
}
```

### 2. 安装虚拟应用

```kotlin
// 从包名安装
val result = OpenVAHelper.installPackage("com.example.app", userId = 0)
if (result.success) {
    println("安装成功: ${result.packageName}")
} else {
    println("安装失败: ${result.message}")
}

// 从 APK 文件安装
val result = OpenVAHelper.installPackageFromApk("/path/to/app.apk", userId = 0)
```

### 3. 启动虚拟应用

```kotlin
val success = OpenVAHelper.launchApp("com.example.app", userId = 0)
if (success) {
    println("启动成功")
} else {
    println("启动失败")
}
```

### 4. 卸载虚拟应用

```kotlin
OpenVAHelper.uninstallApp("com.example.app", userId = 0)
```

### 5. 查询应用

```kotlin
// 检查是否已安装
val installed = OpenVAHelper.isAppInstalled("com.example.app", userId = 0)

// 获取所有已安装应用
val packages = OpenVAHelper.getInstalledPackages(userId = 0)
```

### 6. 在虚拟应用进程内获取信息

```kotlin
// 获取当前虚拟用户ID
val userId = OpenVAHelper.getCurrentUserId()

// 获取当前虚拟应用包名
val packageName = OpenVAHelper.getCurrentPackageName()
```

## 系统调用过滤器

### 基本使用

```kotlin
import rj.kilikili.private.syscall.SyscallType
import rj.kilikili.private.syscall.FilterResult

// 检查系统调用是否允许
val result = OpenVAHelper.checkSyscall(
    SyscallType.GET_IMEI,
    "com.example.app",
    userId = 0
)

when (result) {
    is FilterResult.Allow -> println("允许调用")
    is FilterResult.Deny -> println("拒绝: ${result.reason}")
    is FilterResult.Redirect -> println("重定向到: ${result.replacement.displayName}")
    is FilterResult.Mock -> println("模拟返回: ${result.returnValue}")
}
```

### 创建预设过滤器

```kotlin
import rj.kilikili.private.syscall.FilterPreset

val filterManager = OpenVAHelper.getSyscallFilterManager()

// 隐私保护过滤器
val privacyFilter = filterManager.createPresetFilter(FilterPreset.PRIVACY_PROTECT)
filterManager.registerFilter(privacyFilter)

// 仅网络过滤器
val networkFilter = filterManager.createPresetFilter(FilterPreset.NETWORK_ONLY)
filterManager.registerFilter(networkFilter)
```

### 创建自定义过滤器

```kotlin
import rj.kilikili.private.syscall.*

val customFilter = RuleBasedSyscallFilter(
    name = "自定义过滤器",
    description = "阻止特定系统调用",
    priority = 50,
    rules = listOf(
        SyscallRule(
            id = "block_location",
            name = "阻止位置访问",
            condition = RuleCondition.MatchCategory(setOf(SyscallCategory.LOCATION)),
            action = RuleAction.Deny("位置访问被阻止")
        ),
        SyscallRule(
            id = "mock_imei",
            name = "模拟IMEI",
            condition = RuleCondition.MatchSyscall(setOf(SyscallType.GET_IMEI)),
            action = RuleAction.Mock("000000000000000")
        )
    )
)

filterManager.registerFilter(customFilter)
```

### 查看日志

```kotlin
// 获取所有日志
val logs = OpenVAHelper.getSyscallLogger().getLogs()

// 按包名筛选
val appLogs = OpenVAHelper.getSyscallLogger().getLogsByPackage("com.example.app")

// 获取被拒绝的日志
val deniedLogs = OpenVAHelper.getSyscallLogger().getDeniedLogs()

// 导出日志
val logText = OpenVAHelper.exportSyscallLogs()

// 清除日志
OpenVAHelper.clearSyscallLogs()
```

### 获取统计信息

```kotlin
val stats = OpenVAHelper.getSyscallStatistics()
println("总日志: ${stats.totalLogs}")
println("允许: ${stats.allowedCount}")
println("拒绝: ${stats.deniedCount}")
println("允许率: ${stats.allowRate}")
```

## API 参考

### OpenVAHelper

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `init(context)` | 初始化 OpenVA | Unit |
| `installPackage(packageName, userId)` | 安装应用 | InstallResultWrapper |
| `installPackageFromApk(apkPath, userId)` | 从 APK 安装 | InstallResultWrapper |
| `launchApp(packageName, userId)` | 启动应用 | Boolean |
| `uninstallApp(packageName, userId)` | 卸载应用 | Unit |
| `isAppInstalled(packageName, userId)` | 检查是否安装 | Boolean |
| `getInstalledPackages(userId)` | 获取已安装应用 | List<String> |
| `getCurrentUserId()` | 获取当前用户 ID | Int |
| `getCurrentPackageName()` | 获取当前包名 | String? |
| `checkSyscall(type, package, userId, params)` | 检查系统调用 | FilterResult |
| `getSyscallFilterManager()` | 获取过滤器管理器 | SyscallFilterManager |
| `getSyscallLogger()` | 获取日志记录器 | SyscallLogger |
| `getAvailableSyscallTypes()` | 获取所有系统调用类型 | List<SyscallType> |
| `exportSyscallLogs()` | 导出日志 | String |
| `clearSyscallLogs()` | 清除日志 | Unit |
| `getSyscallStatistics()` | 获取统计信息 | LogStatistics |

### InstallResultWrapper

| 属性 | 说明 | 类型 |
|------|------|------|
| `success` | 是否成功 | Boolean |
| `packageName` | 包名 | String? |
| `message` | 消息 | String? |

### FilterResult

| 类型 | 说明 |
|------|------|
| `Allow` | 允许调用 |
| `Deny(reason)` | 拒绝调用 |
| `Redirect(replacement, params)` | 重定向调用 |
| `Mock(returnValue)` | 模拟返回值 |

### SyscallCategory

| 分类 | 描述 |
|------|------|
| ACTIVITY | Activity 管理 |
| PACKAGE | 包管理 |
| CONTENT | 内容提供者 |
| LOCATION | 位置服务 |
| NETWORK | 网络 |
| STORAGE | 存储 |
| MEDIA | 多媒体 |
| CONTACTS | 联系人 |
| CALENDAR | 日历 |
| SMS | 短信与电话 |
| SENSOR | 传感器 |
| BLUETOOTH | 蓝牙 |
| ACCOUNT | 账户 |
| CLIPBOARD | 剪贴板 |
| NOTIFICATION | 通知 |
| SETTINGS | 系统设置 |
| INTENT | Intent |
| PROCESS | 进程 |
| DEVICE | 设备信息 |
| CUSTOM | 自定义 |

### FilterPreset

| 预设 | 描述 |
|------|------|
| ALLOW_ALL | 允许所有系统调用 |
| DENY_ALL | 拒绝所有系统调用 |
| PRIVACY_PROTECT | 隐私保护，阻止敏感系统调用 |
| NETWORK_ONLY | 仅允许网络相关系统调用 |

## 注意事项

1. OpenVA 需要在 Android 5.0 (API 21) 及以上版本运行
2. 支持的 ABI：arm64-v8a, armeabi-v7a
3. 虚拟应用的数据隔离基于 userId，每个 userId 拥有独立的数据空间
4. 不要尝试克隆宿主应用本身，这会导致安全保护机制触发
5. 系统调用过滤器功能需要在虚拟应用进程中才能生效
6. Android namespace 为 `rj.kilikili.openva`，Kotlin 包名为 `rj.kilikili.private`