package io.github.jacobzufall.luaverse

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory

object Settings {
    private val appDataPath: Path = Paths.get(System.getenv("APPDATA")).parent
    var luaverseDir: String = "$appDataPath\\Local\\Programs\\Lua"

    /*
    Each map index is a unique nickname of the directory. Each map value is a list containing [0] a description of
    the directory, and [1] the directory itself, as a String.
    TODO: Make this variable a property?
    TODO: Should this be a Map of Maps instead of a Map of Lists?
    */
    var directories: Map<String, List<String>> = mapOf(
        "build" to mutableListOf(
            "The directory where Lua is built to.",
            "$luaverseDir\\Lua"
        ),

        "backup" to mutableListOf(
            "The directory where backups of the Path environment variable are stored.",
            "$luaverseDir\\Backups"
        ),

        "download" to mutableListOf(
            "The directory where Lua's source code is downloaded to prior to being built.",
            "$luaverseDir\\Source"
        ),

        "extract" to mutableListOf(
            "The directory where Lua's source code is extracted to.",
            "$luaverseDir\\Extracted"
        )
    )

    init { for ((_, path) in directories) validateDirectory(Paths.get(path[1])) }

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