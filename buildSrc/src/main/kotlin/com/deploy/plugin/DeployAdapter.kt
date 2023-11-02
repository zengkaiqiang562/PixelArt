package com.deploy.plugin

import org.gradle.api.Project
import java.io.File
import java.util.*
import java.util.regex.Pattern

object DeployAdapter {

    private var deployExt: DeployExtension? = null

    fun adapt(project: Project) {

        println("DeployAdapter  project=${project.name}")

        deployExt = project.extensions.getByName("deployExt") as? DeployExtension
        println("DeployAdapter --> deployExt=${deployExt}")

        val deployExt = this.deployExt ?: return

        val debugPkgName = if (deployExt.project.name == "app") deployExt.debugNamespace else deployExt.debugLibPkgName
        val debugPkgPath = convert2Path(debugPkgName)
        val releasePkgName = if (deployExt.project.name == "app") deployExt.releasePkgName else deployExt.releaseLibPkgName
        val releasePkgPath = convert2Path(releasePkgName)
        println("DeployAdapter --> debugPkgName=${debugPkgName}")
        println("DeployAdapter --> debugPkgPath=${debugPkgPath}")
        println("DeployAdapter --> releasePkgName=${releasePkgName}")
        println("DeployAdapter --> releasePkgPath=${releasePkgPath}")

        val debugMainDir = deployExt.debugMainDir
        val releaseMainDir = deployExt.releaseMainDir
        println("DeployAdapter --> debugMainDir=${debugMainDir}")
        println("DeployAdapter --> releaseMainDir=${releaseMainDir}")

        val debugLibPrefix = deployExt.debugLibPrefix
        val releaseLibPrefix = deployExt.releaseLibPrefix
        println("DeployAdapter --> debugLibPrefix=${debugLibPrefix}")
        println("DeployAdapter --> releaseLibPrefix=${releaseLibPrefix}")

        val releaseMainDirFile = project.file(releaseMainDir)
        println("DeployAdapter --> releaseMainDirFile.absolutePath=${releaseMainDirFile.absolutePath}")
        println("DeployAdapter --> releaseMainDirFile.exists=${releaseMainDirFile.exists()}")
        delete(releaseMainDirFile)

        if (deployExt.debug) { // debug 模式不需要处理
            println("DeployAdapter now is DEBUG !!!")
            return
        }

        // 1. 将所有代码 copy 到正式环境的 main 目录 mainProductDir 中
        project.copy {
            from(debugMainDir)
            into(releaseMainDir)
        }

        // 2. 创建正式环境的 java 包名目录，并将 java 代码移至其中
        project.copy {
            from(releaseMainDir + File.separator + "java" + File.separator + debugPkgPath)
            into(releaseMainDir + File.separator + "java" + File.separator + releasePkgPath)
        }

        // 3. 删除 releaseMainDir 中的开发包名目录
        val debugDirInRelease =  File(project.getProjectDir(), releaseMainDir + File.separator + "java" + File.separator + debugPkgPath)
        var debugParentDir = debugDirInRelease.parentFile
        delete(debugDirInRelease)
        while (debugParentDir.list()?.isEmpty() == true) {
            debugParentDir.delete()
            debugParentDir = debugParentDir.parentFile
        }

        // 4. 修改 .java、.xml、.cpp、.pro 文件中的包名（存在多个 lib 模块时，需要在一个模块中判断是否有其他模块的包名）
        project.parent?.subprojects?.forEach { subProject ->
            val name = subProject.name
            if (name != "app" && !name.startsWith("lib")) {
                println("DeployAdapter subProject=${subProject.name} skip !!!")
                return@forEach
            }
            val debugName = if (subProject.name == "app") deployExt.debugNamespace else libName(deployExt.debugLibPrefix, subProject)
            val releaseName = if (subProject.name == "app") deployExt.releasePkgName else libName(deployExt.releaseLibPrefix, subProject)
            println("DeployAdapter subProject=${subProject.name}, debugName=${debugName}, releaseName=${releaseName}")
            recurse(File(project.projectDir, releaseMainDir), debugName, releaseName)
        }
    }

    fun delete(dir: File) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                delete(file)
            } else if (file.isFile) {
                file.delete()
            }
        }
        dir.delete()
    }

    fun recurse(dir: File, debugPkgName: String, releasePkgName: String) {
        val subFiles = dir.listFiles()
//    println("subFiles.size=${subFiles.size}")
        subFiles?.forEach { file ->
//        println("file.path=${file.path}")
            if (file.isDirectory) {
                recurse(file, debugPkgName, releasePkgName)
            } else if (file.isFile ) {
                when {
                    file.path.endsWith(".java") -> modifyJavaFile(file, debugPkgName, releasePkgName)
                    file.path.endsWith(".xml") -> modifyXmlFile(file, debugPkgName, releasePkgName)
                    file.path.endsWith(".pro") -> modifyProguardFile(file, debugPkgName, releasePkgName)
                    file.path.endsWith(".cpp") -> modifyCppFile(file, debugPkgName, releasePkgName)
                }
            }
        }
    }

    fun modifyJavaFile(file: File, debugName: String, releaseName: String) {
        var content = file.readText()
        content = content.replace(debugName, releaseName)
        file.writeText(content)
    }

    fun modifyXmlFile(file: File, debugName: String, releaseName: String) {
        var content = file.readText()
        content = content.replace(debugName, releaseName)
        file.writeText(content)
    }

    fun modifyProguardFile(file: File, debugName: String, releaseName: String) {
        var content = file.readText()
        content = content.replace(debugName, releaseName)
        file.writeText(content)
    }

    fun modifyCppFile(file: File, debugName: String, releaseName: String) {
        var content = file.readText()
        content = content.replace(convert2Path(debugName), convert2Path(releaseName))
        file.writeText(content)
    }

    fun convert2Path(name: String): String {
        val path = Pattern.compile("\\.").matcher(name).replaceAll("/")
//            val result = pkgName
//            while(result.contains(".")) result.replace(".", File.)
        println("convertLibName2Path  name=${name}, path=${path}")
        return path
    }

    fun libName(prefix: String, project: Project): String {
        val name = project.name
        return when {
            name.startsWith("lib") -> "$prefix.${name.substring("lib".length).toLowerCase(Locale.ROOT)}"
            else -> "$prefix.${name.toLowerCase(Locale.ROOT)}"
        }
    }
}