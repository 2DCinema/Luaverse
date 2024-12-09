package io.github.ddcinema.luaverse.system_interaction

import java.io.File

class LuaSource(path: String) {
    private val luaFolder: String = path.split("\\").last()
    private val luaVersion: String = luaFolder.split("-").last()
    // Builds Lua in the same directory that the source code is contained in.
    private val buildDirectory: String = File(path).parent

    fun build(): Boolean {
        val command: String = String.format("build.bat \"%s\" \"%s\"", luaVersion, buildDirectory)

        try {
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