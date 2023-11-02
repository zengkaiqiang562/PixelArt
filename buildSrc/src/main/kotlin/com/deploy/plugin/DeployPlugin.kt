package com.deploy.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File

class DeployPlugin: Plugin<Project> {

    /**
     * 每个子 Project 中都会执行此方法，分别为每个子 Project 添加扩展和任务
     */
    override fun apply(project: Project) {
        println ("hello DeployPlugin in project=${project.name}, rootProject=${project.parent?.name}")

        if (project.parent == null) return

        DeployLoader.load(project)

        project.extensions.create("deployExt", DeployExtension::class.java)

//        project.tasks.create("deployTask", DeployTask::class.java)
        DeployAdapter.adapt(project) // 代替 DeployTask 来修改包路径和包名

//        DeployLoader.createCmakeDefine(project)


        // 在清理 Project 后也将 src/main/release 文件夹清理掉
        clean(project)

        // 重命名并压缩安装包
        aabRenameAndZip(project)
    }

    private fun clean(project: Project) {
        project.afterEvaluate {
            val localProject = this
            tasks.getByName("clean").doLast {
                val deployExt = localProject.extensions.getByName("deployExt")
                if (deployExt is DeployExtension) {
                    println("delete ${deployExt.releaseMainDir} in ${localProject.name} ")
                    DeployAdapter.delete(localProject.file(deployExt.releaseMainDir))
                }
            }
        }
    }

    private fun aabRenameAndZip(project: Project) {
        project.afterEvaluate {
            val localProject = this
            if (localProject.name != "app") return@afterEvaluate

            val outDirFile = localProject.file("${localProject.projectDir}${File.separator}release")


            tasks.getByName("bundleRelease").doFirst {
                println("aabRenameAndZip ${localProject.name} bundleRelease doFirst")
                val aabFile = File(outDirFile, "app-release.aab")
                println("aabRenameAndZip aabFile.exists=${aabFile.exists()}")
            }

            tasks.getByName("bundleRelease").doLast {
                println("aabRenameAndZip ${localProject.name} bundleRelease doLast")

                val aabFile = File(outDirFile, "app-release.aab")
                println("aabRenameAndZip aabFile.exists=${aabFile.exists()}")

                val deployExt = localProject.extensions.getByName("deployExt")
                if (aabFile.exists() && deployExt is DeployExtension) {
                    val newFileName = "${deployExt.outputName}-${deployExt.versionName}-${deployExt.versionCode}.aab"
                    val newAabFile = File(outDirFile, newFileName)
                    if (newAabFile.exists()) newAabFile.delete()

                    if (aabFile.renameTo(newAabFile)) { // 重命名成功后开始压缩
                        // 压缩 newAabFile
                        zip(newAabFile, localProject, deployExt)
                    }
                }
            }
        }
    }

    // 采用 7z 压缩，打开 AS 前必须先安装 7z 并将其添加到环境变量中
    private fun zip(aabFile: File, project: Project, deployExt: DeployExtension) {
        val aabFilePath = aabFile.absolutePath
        val zipFilePath = "${aabFilePath.substring(0, aabFilePath.lastIndexOf("."))}.zip"
        println("zip aabFilePath=${aabFilePath}, zipFilePath=${zipFilePath}")

        try {
            project.exec {
                val command = "7z a $zipFilePath -p${deployExt.zipwd} $aabFilePath"
                println("zip command=${command}")
                executable = "cmd" // linux 中 cmd 改为 bash
                args = listOf("/c", command) // linux 中 /c 改为 -c
            }
        } catch (e: Exception) {
            println("zip Exception=$e")
        }
    }
}