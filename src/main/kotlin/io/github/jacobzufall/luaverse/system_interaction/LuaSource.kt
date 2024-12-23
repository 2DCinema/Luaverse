package io.github.jacobzufall.luaverse.system_interaction

import java.io.File


import io.github.jacobzufall.luaverse.Settings

class LuaSource(path: String = "") {
    fun downloadLuaVersion(version: String) {


    }

    fun build() {

    }

    @Deprecated("This value is deprecated.")
    private val luaFolder: String = path.split("\\").last()
    @Deprecated("This value is deprecated.")
    private val luaVersion: String = luaFolder.split("-").last()

    @Deprecated("This function is deprecated. Use build() instead.")
    fun oldBuild(): Boolean {
        val command: String = String.format("build.bat \"%s\" \"%s\"", luaVersion, Settings.directories["build"])

        try {
            // I originally did this in Lua, and had ChatGPT convert it to Kotlin since I wasn't sure how to do it, sorry.
            val process = ProcessBuilder(command.split(" "))
                .directory(File(System.getProperty("user.dir")))
                .start()

            val exitCode = process.waitFor()

            if (exitCode == 0) {
                println("Batch file executed successfully.")

            } else {
                println("Error: Batch file execution failed with exit code $exitCode.")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Temporary. Should return true or false based on the success of the build.
        return true
    }
}