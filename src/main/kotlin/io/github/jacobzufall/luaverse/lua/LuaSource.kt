package io.github.jacobzufall.luaverse.lua

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

import io.github.jacobzufall.luaverse.Settings
import io.github.jacobzufall.luaverse.utility.VersionString

object LuaSource {
    fun download(version: VersionString) {
        fun fetchFileFromFtp(url: String) {
            val client: OkHttpClient = OkHttpClient()
            val request: Request =  Request.Builder().url(url).build()
            val response: Response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val outputFile: File = File(Settings.directories["download"]!![1], "lua-${version.rawVersion}.tar.gz")

                response.body?.byteStream().use { input ->
                    outputFile.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                }
            }
        }

        when (version.rawVersion) {
            // The latest version is always the first value of luaVersionFiles.
            "latest" -> fetchFileFromFtp(LuaVersions.luaVersionFiles.firstNotNullOf { it.value })
            else -> LuaVersions.luaVersionFiles[version.delimitedVersion]?.let { fetchFileFromFtp(it) } ?: println("Version does not exist.")
        }
    }

    /*
    TODO: This function should target the /src/ folder. I'm not sure if specific cases will need to be accounted for
     based on what version of Lua is being downloaded.
    */
    fun extract(version: String) {
        // Checks if a version of the binary is already downloaded.
        var compressedSourceFile: File = File("${Settings.directories["download"]!![1]}\\lua-")
    }
}