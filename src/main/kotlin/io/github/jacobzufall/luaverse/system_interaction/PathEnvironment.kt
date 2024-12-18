package io.github.jacobzufall.luaverse.system_interaction

import java.io.File

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import io.github.jacobzufall.luaverse.Settings


class PathEnvironment {
    // Should PathEnvironment just inherit EnvironmentVariable?
    private val pathEnvVar: EnvironmentVariable = EnvironmentVariable("Path")

    /**
     * Creates a backup of the current Path system environment variable and stores it in a JSON file. This works as a
     * fail-safe in case something goes wrong so that a user can revert any unwanted changes. Ideally, this function
     * should be run before any changes are made.
     * @return If the backup was completed successfully or not.
     */
    fun backup(): Boolean {
        val file: File = File("${Settings.directories["backup"]?.get(1)}\\luaverse_path-backup_${System.currentTimeMillis()}.json")
        val prettyJson: Json = Json { prettyPrint = true }
        file.writeText(prettyJson.encodeToString(pathEnvVar.values))

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

            var exitCode: Int = 0

            /*
            A hard restore completely overrides the PATH variable to match the restore file.

            A soft restore compares the current PATH variable to the backup, and adds in any values found in the backup
                that are not found in the PATH.
            */
            if (hardRestore) {
                println("Performing hard restore of $backupFile.")
                /*
                Takes the restoredEnvValues and converts it to a string delineated by semicolons, with a trailing
                semicolon since that's what Windows does. This is then loaded into the Path environment variable.
                */
                val process: Process = ProcessBuilder("reg", "add", pathEnvVar.regKey, "/v",
                    pathEnvVar.envVar, "/t", "REG_EXPAND_SZ", "/d", restoredEnvValues.joinToString(separator = ";") + ";",
                    "/f").inheritIO().start()

                exitCode = process.waitFor()

            } else {
                println("Performing soft restore of $backupFile.")
                val valuesToRestore: MutableList<String> = mutableListOf()

                for (value in restoredEnvValues) {
                    if (value !in pathEnvVar.values) {
                        valuesToRestore.add(value)
                    }
                }

                /*
                Tacks values that were found in the backup and not in the Path environment variable on to the end of the
                current value.
                */
                val newPathValue: String = pathEnvVar.rawValues + valuesToRestore.joinToString(separator = ";") + ";"

                val process: Process = ProcessBuilder("reg", "add", pathEnvVar.regKey, "/v",
                    pathEnvVar.envVar, "/t", "REG_EXPAND_SZ", "/d", newPathValue, "/f").inheritIO().start()

                exitCode = process.waitFor()

            }

            if (exitCode != 0) {
                println("Failed to restore $backupFile.")

            } else {
                println("$backupFile was successfully restored.")
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
        for (pathVariable in pathEnvVar.values) {
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