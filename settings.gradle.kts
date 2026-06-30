pluginManagement {
    repositories {
        maven { url = uri("https://www.jitpack.io") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven {
            url = uri("https://maven.aliyun.com/repository/google")
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://www.jitpack.io") }
        maven {
            url = uri("https://maven.aliyun.com/repository/google")
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                excludeGroupByRegex("androidx\\..*")
            }
        }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
        google()
        mavenCentral()
        mavenLocal()

    }
    versionCatalogs {
        create("libs")
    }
}

rootProject.name = "KiliKili"
include(":app")
include(":ijkplayer-java")
include(":DanmakuFlameMaster")
include(":brotlij")
include(":BiliWebApi")
include(":NativeBitmap")
