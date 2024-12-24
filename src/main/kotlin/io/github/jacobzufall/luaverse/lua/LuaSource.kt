package io.github.jacobzufall.luaverse.lua

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import io.github.jacobzufall.luaverse.lua.LuaVersions
import io.github.jacobzufall.luaverse.Settings

class LuaSource {
    fun download(version: String) {
        when (version.lowercase()) {
            "latest" -> {
                val latestSourceFileUrl: String = LuaVersions.luaVersionFiles.firstNotNullOf { it.value }

                val client: OkHttpClient = OkHttpClient()
                val request: Request =  Request.Builder().url(latestSourceFileUrl).build()
                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    response.body?.byteStream().use {
                        // Need to figure out a better way to control the directories Map.
                        input -> File(Settings.directories["download"]!![1]).outputStream().use {
                            output -> input!!.copyTo(output)
                        }
                    }
                }
            }
        }
    }

    fun extract(version: String) {
        // Checks if a version of the binary is already downloaded.
        var compressedSourceFile: File = File("${Settings.directories["download"]!![1]}\\lua-")
    }
}