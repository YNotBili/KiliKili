import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.protobuf)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(libs.versions.javaVersion.get())
    }
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.huanli233.biliwebapi"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.javaVersion.get())
    }
}

dependencies {
    implementation(libs.retrofit2.compat.retrofit)
    //noinspection GradleDependency
    compileOnly(libs.google.gson)
    implementation(libs.retrofit2.compat.converter.gson) {
        exclude(group = "com.google.code.gson")
    }
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.protobuf.javalite)

    implementation(kotlin("reflect"))

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.core)
}
