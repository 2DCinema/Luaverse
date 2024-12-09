package io.github.cinema2d.luaverse

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory

object Settings {
    private val appDataPath: Path = Paths.get(System.getenv("APPDATA")).parent

    // Each map index is a unique nickname of the directory. Each map value is a list containing [0] a description of
    // the directory, and [1] the directory itself, as a String.
    // TODO: Make this variable private?
    var directories: Map<String, List<String>> = mapOf(
        "build" to mutableListOf(
            "The directory where Lua is built to.",
            "$appDataPath\\Local\\Programs\\Lua"
        ),

        "backup" to mutableListOf(
            "The directory where backups of the Path environment variable are stored.",
            "$appDataPath\\Roaming\\Luaverse\\Backups"
        )
    )

    init {
        for ((_, path) in directories) {
            validateDirectory(Paths.get(path[1]))

        }
    }

    /**
     * Checks if a directory exists and attempts to create it if it doesn't.
     *
     * @param[directory] Path The directory to validate.
     * @return If the directory was successfully created or not.
     */
    private fun validateDirectory(directory: Path): Boolean {
        var result: Boolean = directory.isDirectory()

        if (!result) {
            Files.createDirectories(directory)
            result = directory.isDirectory()

        }

        return result

    }
}