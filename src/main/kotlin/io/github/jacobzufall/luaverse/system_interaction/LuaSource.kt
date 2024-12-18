package io.github.jacobzufall.luaverse.system_interaction

import io.github.jacobzufall.luaverse.Settings
import java.io.File

class LuaSource(path: String) {
    private val luaFolder: String = path.split("\\").last()
    private val luaVersion: String = luaFolder.split("-").last()

    fun build() {

    }

    fun build_old(): Boolean {
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