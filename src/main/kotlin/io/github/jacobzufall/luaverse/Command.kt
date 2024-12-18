/*
All commands return "true" or "false" based on if their execution was successful or not. Currently, the value
returned is unused, but I am implementing it in case it is needed in the future.
*/

package io.github.jacobzufall.luaverse

import java.awt.Desktop
import java.io.File

import io.github.jacobzufall.luaverse.system_interaction.LuaSource
import io.github.jacobzufall.luaverse.system_interaction.PathEnvironment

class Command(command: List<String>) {
    // Map commands here.
    /*
    TODO: Possible commands to consider.
        Some sort of command to list off the versions currently installed. Maybe one single list command that can be
        used to list of directories as well?
    */
    private val rootCommands = mapOf(
        "help" to ::helpCommand,
        "backup" to ::backupCommand,
        "restore" to ::restoreCommand,
        "build" to ::buildCommand,
        "dir" to ::dirCommand
    )

    init { rootCommands[command[0].lowercase()]?.invoke(command) ?: invalidateCommand(command) }

    /**
     * Designed to be called whenever any command cannot be completed.
     * @param[command] An array containing each command argument.
     * @return false
     */
    private fun invalidateCommand(command: List<String>): Boolean {
        println("${command.joinToString(separator = " ")} is not a valid command.")
        // Always returns false so that it may be called functionally, if desired.
        return false
    }

    /**
     * Provides a list of commands.
     * @param[command] An array containing each command argument.
     * @return If the command was executed successfully or not.
     */
    private fun helpCommand(command: List<String>): Boolean {
        try {
            command[1]
            /*
            Eventually, I want to have more detailed information on each command, so that one could say "help build",
            and it would show them how to use that command.
            */

        } catch (e: IndexOutOfBoundsException) {
            println("Supported commands:")
            for ((name, _) in rootCommands) println("  - $name")
        }

        return true
    }

    /**
     * Manually creates a backup of the current Path environment variables.
     * @param[command] An array containing each command argument.
     * @return If the command was executed successfully or not.
     */
    private fun backupCommand(command: List<String>): Boolean {
        if (command.size > 1) return invalidateCommand(command)
        return PathEnvironment().backup()
    }

    private fun restoreCommand(command: List<String>): Boolean {
        when (command.size) {
            1 -> {
                println("Please specify the path to the backup. Backups can be found by invoking the \"dir backup\" " +
                        "command or navigating to ${Settings.directories["backup"]?.get(1)}.")
                return false
            }

            2, 3 -> {
                val hardRestore: Boolean = command.getOrNull(2) == "hard"
                return PathEnvironment().restore(command[1], hardRestore)
            }

            else -> return invalidateCommand(command)
        }
    }

    /**
     * Builds Lua to the build directory.
     * @param[command] An array containing each command argument.
     * @return If the command was executed successfully or not.
     */
    private fun buildCommand(command: List<String>): Boolean {
        when (command.size) {
            /*
            In case one doesn't include the path as the second argument when they invoke the build command, we will give
            them another chance to specify it.
            */
            1 -> {
                println("Please specify the path to the Lua source code. \"Makefile\" should be present in the directory.")
                return false
            }

            2 -> return LuaSource(command[1]).build_old()

            else -> return invalidateCommand(command)
        }
    }

    /**
     * Lists of the current directories and a description of each.
     * @param[command] An array containing each command argument.
     * @return If the command was executed successfully or not.
     */
    private fun dirCommand(command: List<String>): Boolean {
        when (command.size) {
            // If the command is simply "dir", the program will list every directory used.
            1 -> {
                print("\n")
                println("[[[DIRECTORY LISTING]]]")
                println("You can open any of the following directories by typing in \"dir <name>\".`")
                print("\n")

                for ((dirName, dirInfo) in Settings.directories) {
                    println(dirName)
                    println(dirInfo[0])
                    println(dirInfo[1])
                    print("\n")
                }

                println("[[[END OF LISTING]]]")
                print("\n")

                return true
            }

            // Optionally, the user can specify a directory name after dir, and we'll open that directory as a convenience.
            2 -> {
                for ((dirName, dirInfo) in Settings.directories) {
                    if (command[1] == dirName.lowercase()) {
                        val directory = File(dirInfo[1])

                        if (directory.exists() && directory.isDirectory) {
                            Desktop.getDesktop().open(directory)
                            return true
                        }
                    }
                }

                return false
            }

            else -> return invalidateCommand(command)
        }
    }
}