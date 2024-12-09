package io.github.cinema2d.luaverse.system_interaction

import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import io.github.cinema2d.luaverse.Settings

class PathEnvironment {
    private var envVariables: List<String> = System.getenv("Path").split(";")

    /**
     * Creates a backup of the current Path system environment variable and stores it in a JSON file.
     * This works as a fail-safe in case something goes wrong so that a user can revert any unwanted changes.
     * Ideally, this function should be run before any changes are made.
     */
     fun backup() {
        val file: File = File("${Settings.directories["backup"]?.get(1)}\\luaverse_path-backup_${System.currentTimeMillis()}.json")
        val prettyJson: Json = Json{prettyPrint = true}
        file.writeText(prettyJson.encodeToString(envVariables))

        println("Backup created at $file")

    }

    /**
     * Restores a backup created by backup().
     */
//    private fun restore(file) {
//        // Extra backup, in case someone restores the wrong file.
//        backup()
//
//
//
//    }

    /**
     * Attempts to find the specified version of Lua in the Path environment variable.
     */
    private fun findVersion(version: String): String? {
        for (pathVariable in envVariables) {
            val pathObjects: List<String> = pathVariable.split("\\")

            if (pathObjects[pathObjects.size - 2] == "lua$version") {
                return pathVariable

            }
        }

        return null

    }

//    fun addVersion(version: String): Boolean {}

    fun removeVersion(version: String): Boolean {
        // Versions are usually displayed as "x.x.x" when downloaded, but they are installed as "xxx".
        val cleanVersion: String = version.split(".").joinToString(separator = "")
        val pathVariable: String? = findVersion(cleanVersion)

        val process = ProcessBuilder("setx", pathVariable, "").start()
        return process.waitFor() == 0

    }

//    fun removeAllVersions(version: String): Boolean {}
}
