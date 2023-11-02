package com.deploy.plugin

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GzipUtils {
    private const val TAG = "GzipUtils"
    const val BUFFER = 1024

    /**
     * GZip 压缩
     *
     * @param data 未压缩的数据
     */
    fun zip(data: ByteArray?): ByteArray? {
        var output: ByteArray? = null
        var bais: ByteArrayInputStream? = null
        var baos: ByteArrayOutputStream? = null
        try {
            bais = ByteArrayInputStream(data)
            baos = ByteArrayOutputStream()

            // 压缩
            zipInternal(bais, baos)
            output = baos.toByteArray()
            baos.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            println("$TAG--> zip() failed !!! e=$e")
        } finally {
            try {
                baos?.close()
                bais?.close()
            } catch (e: Exception) {
                e.printStackTrace()
                println("$TAG--> zip() io close failed !!! e=$e")
            }
        }
        return output
    }

    /**
     * GZip 解压
     *
     * @param data 压缩后的数据
     * @return 返回解压后的未压缩数据
     */
    fun unzip(data: ByteArray?): ByteArray? {
        var bais: ByteArrayInputStream? = null
        var baos: ByteArrayOutputStream? = null
        var result: ByteArray? = null
        try {
            bais = ByteArrayInputStream(data)
            baos = ByteArrayOutputStream()

            // 解压缩
            unzipInternal(bais, baos)
            result = baos.toByteArray()
            baos.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            println("$TAG--> unzip() failed !!! e=$e")
        } finally {
            try {
                baos?.close()
                bais?.close()
            } catch (e: Exception) {
                e.printStackTrace()
                println("$TAG--> unzip() io close failed !!! e=$e")
            }
        }
        return result
    }

    /**
     *
     * @param is 压缩源 io
     * @param os 压缩结果 io
     */
    private fun zipInternal(`is`: InputStream, os: OutputStream) {
        var gos: GZIPOutputStream? = null
        try {
            gos = GZIPOutputStream(os)
            var count: Int
            val data = ByteArray(BUFFER)
            while (`is`.read(data, 0, BUFFER).also { count = it } != -1) {
                gos.write(data, 0, count)
            }
            gos.finish()
            gos.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            println("$TAG--> zipInternal() failed !!! e=$e")
        } finally {
            try {
                gos?.close()
            } catch (e: Exception) {
                e.printStackTrace()
                println("$TAG--> zipInternal() io close failed !!! e=$e")
            }
        }
    }

    /**
     * 数据解压缩
     *
     * @param is 解压源 io
     * @param os 解压后的结果 io
     */
    private fun unzipInternal(`is`: InputStream, os: OutputStream) {
        var gis: GZIPInputStream? = null
        try {
            gis = GZIPInputStream(`is`)
            var count: Int
            val data = ByteArray(BUFFER)
            while (gis.read(data, 0, BUFFER).also { count = it } != -1) {
                os.write(data, 0, count)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("$TAG--> unzipInternal() failed !!! e=$e")
        } finally {
            try {
                gis?.close()
            } catch (e: Exception) {
                e.printStackTrace()
                println("$TAG--> unzipInternal() io close failed !!! e=$e")
            }
        }
    }
}