# Session Export - 2026-07-04

## 概述

本次会话完成了 KiliKili 项目中 OpenVA 虚拟应用引擎的集成工作。

## 导出的文件

| 文件 | 说明 |
|------|------|
| `session-summary.md` | 会话总结，包含任务概述和完成的工作 |
| `changelog.md` | 变更日志，包含新增功能和修改文件 |
| `usage-guide.md` | 使用指南，包含 API 参考和使用示例 |
| `file-list.md` | 文件变更列表，包含新增和修改的文件 |
| `session.json` | 会话数据文件，包含元数据和统计信息 |

## 任务摘要

### 主要工作

1. **创建 private 模块** - 集成 OpenVA 虚拟应用引擎
2. **实现系统调用过滤器** - 枚举、过滤、日志记录
3. **创建 UI 界面** - OpenVA 管理和过滤器配置
4. **修复构建问题** - Gson 重复类、ConstraintLayout 缺失、R8 missing class
5. **修复图片加载** - 视频详情页等页面的封面图片
6. **迁移包名** - 从 `rj.kilikili.openva` 到 `rj.kilikili.private`

### 编译状态

- ✅ Debug 构建成功
- ✅ Release 构建成功

### 包名架构

- **Android namespace**: `rj.kilikili.openva`（因为 `private` 是 Java 关键字）
- **Kotlin 包名**: `rj.kilikili.private`
- **源码路径**: `private/src/main/java/rj/kilikili/private/`

## 使用方法

### 初始化

```kotlin
import rj.kilikili.private.OpenVAHelper

OpenVAHelper.init(context)
```

### 安装虚拟应用

```kotlin
val result = OpenVAHelper.installPackage("com.example.app", userId = 0)
if (result.success) {
    // 安装成功
}
```

### 检查系统调用

```kotlin
import rj.kilikili.private.syscall.SyscallType

val result = OpenVAHelper.checkSyscall(
    SyscallType.GET_IMEI,
    "com.example.app",
    userId = 0
)
```

## 相关文件

### private 模块

```
private/
├── build.gradle.kts
├── proguard-rules.pro
├── consumer-rules.pro
├── README.md
└── src/main/java/rj/kilikili/private/
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

## 注意事项

1. OpenVA 需要在 Android 5.0 (API 21) 及以上版本运行
2. 支持的 ABI：arm64-v8a, armeabi-v7a
3. 虚拟应用的数据隔离基于 userId
4. 不要尝试克隆宿主应用本身
5. 系统调用过滤器功能需要在虚拟应用进程中才能生效