package com.deploy.plugin

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Project
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.util.*
import java.util.regex.Pattern

object DeployLoader {

    /**
     * map 保存 deploy.json 中的key-value，以及：
     *      projectDir: 子Project的路径
     *      sign：根据 jks 文件解析出的签名字符串
     *      localConfig: 加密后的本地全局配置
     *      vpnlist: 加密后的本地 VPN 列表
     *
     */
    var map: MutableMap<String, Any> = mutableMapOf()
    var allProjectPkgMap: MutableMap<String, Any> = mutableMapOf()
    lateinit var project: Project

    /**
     * 每个子 Project 中都会执行一遍 load 方法
     */
    fun load(project: Project) {
        this.project = project
        val rootProject = project.parent
        val deployFile = rootProject?.file("deploy.json")
        println("ConfigLoader  deployFilePath=${deployFile?.absolutePath}")
        val slurper = JsonSlurper();
        val result = slurper.parse(deployFile)
        println ("ConfigLoader result.class= ${result?.javaClass}")
        if (result != null && result is Map<*, *>) {
            result.forEach { entry ->
                map[entry.key as String] = entry.value as Any
            }
            map["projectDir"] = project.projectDir
//            createSign()
            createLocalConfig()
//            createVpnlist()
        }

        println("ConfigLoader  map=${map}")
    }

    private fun createSign() {
        val fixSign = map["fixSign"].let { if (it is String) it else "" }
        if (fixSign != "") {
            map["sign"] = fixSign
            return
        }
        val signPath = map["signPath"].let { if (it is String) it else "" }
        val signPwd = map["signPwd"].let { if (it is String) it else "" }
        val signFile = project.parent?.file(signPath)
        if (signFile != null) {
            val keyStore = KeyStore.getInstance(signFile, signPwd.toCharArray())
            val aliases = keyStore.aliases()
            while(aliases.hasMoreElements()) {
                val alias = aliases.nextElement()
                val certificate = keyStore.getCertificate(alias)
                println("certificate=${certificate.javaClass}")
                if (certificate is X509Certificate) {
                    val encoded = certificate.encoded
                    val messageDigest = MessageDigest.getInstance("SHA")
                    messageDigest.update(encoded)
                    val digest = messageDigest.digest()
                    val hexString = ConvertUtils.bytes2HexString(digest, true)
                    println("hexString=${hexString}")
                    val encodeToString = Base64.getEncoder().encodeToString(digest)
                    println("encodeToString=${encodeToString}")
                    map["sign"] = encodeToString
                }
            }
        }
    }

    private fun createLocalConfig() {
        val configFile = project.parent?.file("config.json")
        if (configFile != null) {
            /*
            byte[] gzCompress = GzipUtils.zip(sProfileCache.getBytes(StandardCharsets.UTF_8));
            byte[] encodeBase64 = Base64.encodeBase64(gzCompress);
            String encodePrifleCache = new String(encodeBase64, StandardCharsets.UTF_8);
            System.out.println("encodePrifleCache=" + encodePrifleCache);
             */
            if (map["encryptConfig"].let { if (it is Boolean) it else false }) { // 是否加密本地配置
                val gzCompress = GzipUtils.zip(configFile.readText().toByteArray())
                val encodeBase64 = org.apache.commons.codec.binary.Base64.encodeBase64(gzCompress)
                val encodeLocalConfig = String(encodeBase64, StandardCharsets.UTF_8)
                map["localConfig"] = encodeLocalConfig
            } else {
                // 转一遍json，再转回成字符串以便去掉换行符；并将 字符串中的 " 替换成 \"
                map["localConfig"] = Pattern.compile("\"").matcher(JsonOutput.toJson(JsonSlurper().parse(configFile))).replaceAll("\\\\\"")
            }
        }
    }

    private fun createVpnlist() {
        val vpnlistFile = project.parent?.file("vpnlist.json")
        if (vpnlistFile != null) {
            val gzCompress = GzipUtils.zip(vpnlistFile.readText().toByteArray())
            val encodeBase64 = org.apache.commons.codec.binary.Base64.encodeBase64(gzCompress)
            val encodeVpnlist = String(encodeBase64, StandardCharsets.UTF_8)
            println("encodeVpnlist=${encodeVpnlist}")
            map["vpnlist"] = encodeVpnlist
        }
    }

    /**
     * 要在 DeployAdapter.adapt 方法执行完后才能调用，否则正式环境下的 cpp 目录还没创建出来
     * 此时，无法在 cpp 目录下创建 define.cmake 文件
     */
    fun createCmakeDefine(project: Project) {
        // TODO 不再生成 define.cmake 文件
//        println("DeployAdapter  project=${project.name}")
//
//        val deployExt = project.extensions.getByName("deployExt") as? DeployExtension ?: return
//        println("DeployAdapter --> deployExt=${deployExt}")
//
//        val mainDir = deployExt.curMainDir
//        /*
//        add_definitions(-DCONFIG_CACHE="H4sIAAAAAAAAAO2VQWuDMBTH7/0UJWdlJkZddhuFncYY7DiKRM00TJMQoy0rfvdFW1y7dmWHliHsnV7e++f/Hr9DspnNbQCaLaR44/kjrw24m78O1T42Y7bTtbYNnrVMSimz2lBtgHOokYqJRSlrZpVGN+xbmwvB9H12NOn0xPFa2ju+8I/e1XNOa2j21FT9fil1qVKuahLXJ9hDQegRQjAKML7xMSS3foAIBj/YrBjPC3N2TvsgdUV7DdgyOBJ2B5XleOqcX7JNpQWVTo0u9HwfRn4E0WXocnENtu9CrkqW5eyf7hXoFrKaGliEcBSSEELvMmAFba8AVjMlJ/fe/i3aIVtufUGi4lqVtC7iiq7jrNHUcCmsKURfinT4CPe7e00maFKymK2NprFdi2mzoz7rPgHa9pRoSwcAAA==")
//        add_definitions(-DSIGN="k4GKLW0a+lekuDyWzUE3ZzBgy8o=")
//        add_definitions(-DCKEY="QEBmuwlGh9f0V8tP")
//        add_definitions(-DCIV="A")
//        add_definitions(-DSKEY="viWQMvRWlTzvChIt")
//        add_definitions(-DSIV="A")
//        add_definitions(-DTAG="SafeNative")
//         */
//        val cppDir = "${map["projectDir"]}${File.separator}${mainDir}/cpp"
//        println("cppDir=${cppDir}")
//        val cppDirFile = File(cppDir)
//        if (!cppDirFile.exists()) {
//            println("DeployAdapter --> $cppDir  don't Exsits !!!")
//            return
//        }
//        val defineCmakeFile = File(cppDirFile, "define.cmake")
//        println("defineCmakeFilePath=${defineCmakeFile.absolutePath}")
//        if (defineCmakeFile.exists()) defineCmakeFile.delete()
//        defineCmakeFile.createNewFile()
//        defineCmakeFile.writeText("""
//            add_definitions(-DCONFIG_CACHE="${map["localConfig"]}")
//            add_definitions(-DSIGN="${map["sign"]}")
//            add_definitions(-DCKEY="${map["ckey"]}")
//            add_definitions(-DCIV="${map["civ"]}")
//            add_definitions(-DSKEY="${map["skey"]}")
//            add_definitions(-DSIV="${map["siv"]}")
//        """.trimIndent())
    }
}