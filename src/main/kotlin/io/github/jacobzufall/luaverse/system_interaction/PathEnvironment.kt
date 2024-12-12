package io.github.jacobzufall.luaverse.system_interaction

import java.io.File

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import io.github.jacobzufall.luaverse.Settings


class PathEnvironment {
    private val pathValues: List<String>
        get() {
            val processBuilder: ProcessBuilder = ProcessBuilder()
            processBuilder.command(
                "cmd.exe",
                "/c",
                "reg query \"HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment\""
            )
            val process: Process = processBuilder.start()

            val output: MutableList<String> = process.inputStream.bufferedReader().readText().split("    ").toMutableList()
            process.waitFor()

            val pathValues: MutableList<String> = output[output.indexOf("Path") + 2].split(";").toMutableList()
            // Sometimes it shows escape characters at the end, which we don't want.
            if (pathValues.last() == "\r\n") pathValues.removeLast()

            return pathValues.toList()
        }

    /**
     * Creates a backup of the current Path system environment variable and stores it in a JSON file.
     * This works as a fail-safe in case something goes wrong so that a user can revert any unwanted changes.
     * Ideally, this function should be run before any changes are made.
     * @return If the backup was completed successfully or not.
     */
    fun backup(): Boolean {
        val file: File = File("${Settings.directories["backup"]?.get(1)}\\luaverse_path-backup_${System.currentTimeMillis()}.json")
        val prettyJson: Json = Json { prettyPrint = true }
        file.writeText(prettyJson.encodeToString(pathValues))

        if (file.exists()) {
            println("Backup created at $file")
            return true
        } else {
            println("Failed to create backup file in ${Settings.directories["backup"]?.get(1)}.")
            return false
        }
    }

    /**
     * Restores a backup created by backup().
     * @param[backupFile] The file to restore, created by backup().
     * @param[hardRestore] If the environment variable should be restored EXACTLY as it was. If not, the missing values
     * will be simply added.
     * @return If the restore was completed successfully or not.
     */
    fun restore(backupFile: String, hardRestore: Boolean = false): Boolean {
        // Extra backup, in case someone restores the wrong file.
        println("Creating redundant backup.")
        backup()
        
        val fileToRestore: File = File("${Settings.directories["backup"]?.get(1)}\\$backupFile")
        
        if (fileToRestore.exists()) {
            val jsonString: String = fileToRestore.readText()
            val json = Json { ignoreUnknownKeys = true }
            val restoredEnvValues: List<String> = json.decodeFromString<List<String>>(jsonString)

            /*
            A hard restore directly sets PATH to equal restoredEnvValues as a string with ";" delimiters. A soft
            restore goes through the PATH and checks which values are not in envValues that are in restoredEnvValues.
            If a value isn't in envValues, it's added. Values in envValues that aren't in restoredEnvValues are
            effectively left alone.
            */
            if (hardRestore) {
                // Not entirely sure if this is correct yet.
                val processBuilder: ProcessBuilder = ProcessBuilder()
                processBuilder.command(
                    "cmd",
                    "/c",
                    "reg add \"HKLM\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment\" /v Path /t REG_EXPAND_SZ /d \"$currentPath;$path\" /f"
                )
                val process: Process = processBuilder.start()



            } else {
                // This adds it to the user path, not system path, so this needs to be fixed.
                for (value in restoredEnvValues) {
                    val processBuilder: ProcessBuilder = ProcessBuilder()
                    processBuilder.command("cmd", "/c", "setx PATH \"%PATH%;$value\"")
                    val process: Process = processBuilder.start()

                    val exitCode: Int = process.waitFor()

                    if (exitCode != 0) {
                        println("Failed to add $value to PATH.")
                    } else {
                        println("Successfully added $value to PATH.")
                    }
                }
            }


            return true

        } else {
            println("Could not locate $backupFile in ${Settings.directories["backup"]?.get(1)}.")
            return false
        }
    }

    /**
     * Attempts to find the specified version of Lua in the Path environment variable.
     */
    private fun findVersion(version: String): String? {
        for (pathVariable in pathValues) {
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
