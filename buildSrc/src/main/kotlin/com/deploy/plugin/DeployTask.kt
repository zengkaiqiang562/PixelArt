package com.deploy.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class DeployTask: DefaultTask() {

//    private var deployExt: DeployExtension? = null
//
//    @TaskAction
//    fun exec() {
//        println("DeployTask running  this.project=${this.project}, task.name=${this.name}")
//
//        deployExt = this.project.extensions.getByName("deployExt") as? DeployExtension
//        println("DeployTask --> deployExt=${deployExt}")
//
//        val deployExt = this.deployExt
//        if (deployExt != null) {
//            // 1. 将所有代码 copy 到正式环境的 main 目录 mainProductDir 中
//            this.project.copy {
//                from(deployExt.debugMainDir)
//                into(deployExt.releaseMainDir)
//            }
//
//            // 2. 创建正式环境的 java 包名目录，并将 java 代码移至其中
//            this.project.copy {
//                from(deployExt.releaseMainDir + File.separator + "java" + File.separator + deployExt.debugPkgPath)
//                into(deployExt.releaseMainDir + File.separator + "java" + File.separator + deployExt.releasePkgPath)
//            }
//
//            // 3. 删除 releaseMainDir 中的开发包名目录
//            val debugDirInRelease =  File(project.getProjectDir(), deployExt.releaseMainDir + File.separator + "java" + File.separator + deployExt.debugPkgPath)
//            var debugParentDir = debugDirInRelease.parentFile
//            delete(debugDirInRelease)
//            while (debugParentDir.list()?.isEmpty() == true) {
//                debugParentDir.delete()
//                debugParentDir = debugParentDir.parentFile
//            }
//
//            // 4. 修改 .java、.xml、.cpp、.pro 文件中的包名
//            recurse(File(project.getProjectDir(), deployExt.releaseMainDir))
//
////            // 5. 修改混淆文件中的包名
////            val proguardFile = File(project.getProjectDir(), "proguard-rules.pro")
////            if (proguardFile.exists()) {
////                modifyProguardFile(proguardFile)
////            }
//        }
//    }
//
//    fun delete(dir: File) {
//        dir.listFiles()?.forEach { file ->
//            if (file.isDirectory) {
//                delete(file)
//            } else if (file.isFile) {
//                file.delete()
//            }
//        }
//        dir.delete()
//    }
//
//    fun recurse(dir: File) {
//        val subFiles = dir.listFiles()
////    println("subFiles.size=${subFiles.size}")
//        subFiles?.forEach { file ->
////        println("file.path=${file.path}")
//            if (file.isDirectory) {
//                recurse(file)
//            } else if (file.isFile ) {
//                when {
//                    file.path.endsWith(".java") -> modifyJavaFile(file)
//                    file.path.endsWith(".xml") -> modifyXmlFile(file)
//                    file.path.endsWith(".cpp") -> modifyCppFile(file)
//                    file.path.endsWith(".pro") -> modifyProguardFile(file)
//                }
//            }
//        }
//    }
//
//    fun modifyJavaFile(file: File) {
//        var content = file.readText()
//        content = content.replace(deployExt!!.debugPkgName, deployExt!!.releasePkgName)
//        file.writeText(content)
//    }
//
//    fun modifyXmlFile(file: File) {
//        var content = file.readText()
//        content = content.replace(deployExt!!.debugPkgName, deployExt!!.releasePkgName)
//        file.writeText(content)
//    }
//
//    fun modifyProguardFile(file: File) {
//        var content = file.readText()
//        content = content.replace(deployExt!!.debugPkgName, deployExt!!.releasePkgName)
//        file.writeText(content)
//    }
//
//    fun modifyCppFile(file: File) {
//        var content = file.readText()
//        content = content.replace(deployExt!!.debugPkgPath, deployExt!!.releasePkgPath)
//        file.writeText(content)
//    }
}