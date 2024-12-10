package io.github.cinema2d.luaverse.system_interaction

import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import io.github.cinema2d.luaverse.Settings

class PathEnvironment {
    private val envValues: List<String>
        get() = System.getenv("Path").split(";")

    /**
     * Creates a backup of the current Path system environment variable and stores it in a JSON file.
     * This works as a fail-safe in case something goes wrong so that a user can revert any unwanted changes.
     * Ideally, this function should be run before any changes are made.
     * @return If the backup was completed successfully or not.
     */
    fun backup(): Boolean {
        // Using a safe (?) call in case the user just deletes a path when they try to modify one.
        val file: File = File("${Settings.directories["backup"]?.get(1)}\\luaverse_path-backup_${System.currentTimeMillis()}.json")
        val prettyJson: Json = Json { prettyPrint = true }
        file.writeText(prettyJson.encodeToString(envValues))

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

        val file: File = File("${Settings.directories["backup"]?.get(1)}\\$backupFile")

        if (file.exists()) {
            val jsonString: String = file.readText()
            val json = Json { ignoreUnknownKeys = true }
            val restoredEnvValues: List<String> = json.decodeFromString<List<String>>(jsonString)

            /*
            This loop compares each value in restoredEnvValues to envValues. If a value is in restoredEnvValues but
            isn't in envValues, it is added to envValues.
            */
            for (value in restoredEnvValues) {
                println("If you're seeing this, it means you forgot to write part of the code!")
                // TODO: Actually write the values.
            }

            /*
            A hard restore restores the environment variable to EXACTLY how it was when the backup was created.
            This means, if there's a value in envValues that isn't in restoredEnvValues, we DELETE it from the
            environment variable. A soft restore is the default, and the user has to be explicit for a hard restore.

            My reasoning for this is that we can't accidentally delete something if we don't delete anything at all.
            So I'll leave it up to the user to accept that risk. They can always go in and manually remove values if
            they're uncomfortable.
            */
            if (hardRestore) {
                for (value in envValues) {
                    println("If you're seeing this, it means you forgot to write part of the code!")
                    // TODO: Actually write the values.
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
        for (pathVariable in envValues) {
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
