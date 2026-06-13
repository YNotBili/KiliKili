plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        register("mapping-recorder") {
            id = "rj.hotupdate.gradle-plugin"
            implementationClass = "rj.hotupdate.gradleplugin.MappingRecorderPlugin"
        }
    }
}