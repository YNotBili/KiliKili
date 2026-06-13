package rj.hotupdate.gradleplugin

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

class MappingRecorderPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("hotUpdateConfig", HotUpdateExtension::class.java)

        val recordTask = project.tasks.register(
            "recordMappingForHotUpdate",
            RecordMappingTask::class.java
        )

        project.afterEvaluate {
            try {
                val mergeAssets = project.tasks.findByName("mergeReleaseAssets")
                if (mergeAssets != null) {
                    mergeAssets.dependsOn(recordTask)
                }
            } catch (_: Exception) {}
        }
    }
}

abstract class RecordMappingTask : DefaultTask() {

    @TaskAction
    fun record() {
        val buildDir = project.layout.buildDirectory.get().asFile
        val mappingFile = buildDir.resolve("outputs/mapping/release/mapping.txt")
        val assetsDir = buildDir.resolve("generated/assets/hotupdate")
        assetsDir.mkdirs()

        if (mappingFile.exists()) {
            mappingFile.copyTo(assetsDir.resolve("mapping.txt"), overwrite = true)
        }

        val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
            .format(java.util.Date())
        assetsDir.resolve("build.json").writeText(
            """{"buildTime":"$now","version":"${project.version}"}"""
        )
    }
}

open class HotUpdateExtension {
    var enableDexOverride: Boolean = false
}