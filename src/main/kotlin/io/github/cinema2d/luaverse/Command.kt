/*
    Currently, all commands return "true" or "false" based on if their execution was successful or not. Currently, the
    value returned is unused, but I am implementing it in case it is needed in the future.
 */

package io.github.cinema2d.luaverse

import java.awt.Desktop
import java.io.File

import io.github.cinema2d.luaverse.system_interaction.LuaSource
import io.github.cinema2d.luaverse.system_interaction.PathEnvironment

class Command(command: Array<String>) {
    // Map commands here.
    /*
        TODO: Possible commands to consider.
            Some sort of command to list off the versions currently installed. Maybe one single list command that can be
            used to list of directories as well?
     */
    private val commands = mapOf(
        "help" to ::helpCommand,
        "backup" to ::backupCommand,
        "build" to ::buildCommand,
        "dir" to ::dirCommand
    )

    init { commands[command[0].lowercase()]?.invoke(command) ?: invalidateCommand(command) }

    /**
     * Designed to be called whenever any command cannot be completed.
     * @param[command] An array containing each command argument.
     * @return false
     */
    private fun invalidateCommand(command: Array<String>): Boolean {
        println("${command.joinToString(separator = " ")} is not a valid command.")
        // Always returns false so that it may be called functionally, if desired.
        return false

    }

    /**
     * Provides a list of commands.
     * @param[command] An array containing each command argument.
     * @return If the command was executed successfully or not.
     */
    private fun helpCommand(command: Array<String>): Boolean {
        try {
            command[1]

            // Eventually, I want to have more detailed information on each command, so that one could say "help build",
            // and it would show them how to use that command.

        } catch (e: ArrayIndexOutOfBoundsException) {
            println("Supported commands:")
            for ((name, _) in commands) {
                println("  - $name")

            }
        }

        return true

    }

    /**
     * Manually creates a backup of the current Path environment variables.
     * @param[command] An array containing each command argument.
     * @return If the command was executed successfully or not.
     */
    private fun backupCommand(command: Array<String>): Boolean {
        if (command.size > 1) return invalidateCommand(command)

        PathEnvironment().backup()
        return true

    }

    /**
     * Builds Lua to the build directory.
     * @param[command] An array containing each command argument.
     * @return If the command was executed successfully or not.
     */
    private fun buildCommand(command: Array<String>): Boolean {
        var result: Boolean = false

        when (command.size) {
            // In case one doesn't include the path as the second argument when they invoke the build command, we will give
            // them another chance to specify it.
            1 -> {
                println("Please specify the path to the Lua source code. \"Makefile\" should be present in the directory.")

            }

            2 -> {
                result = LuaSource(command[1]).build()

            }

            else -> {
                result = invalidateCommand(command)

            }
        }

        return result

    }

    /**
     * Lists of the current directories and a description of each.
     * @param[command] An array containing each command argument.
     * @return If the command was executed successfully or not.
     */
    private fun dirCommand(command: Array<String>): Boolean {
        var result: Boolean = false

        when (command.size) {
            // If the command is simply "dir", the program will list every directory used.
            1 -> {
                println("\n")
                println("[[[DIRECTORY LISTING]]]")
                println("You can open any of the following directories by typing in \"dir <name>\".`")
                println("\n")

                for ((dirName, dirInfo) in Settings.directories) {
                    println(dirName)
                    println(dirInfo[0])
                    println(dirInfo[1])
                    println("\n")

                }

                println("[[[END OF LISTING]]]")
                println("\n")

                result = true

            }

            // Optionally, the user can specify a directory name after dir, and we'll open that directory as a convenience.
            2 -> {
                for ((dirName, dirInfo) in Settings.directories) {
                    if (command[1] == dirName.lowercase()) {
                        val directory = File(dirInfo[1])

                        if (directory.exists() && directory.isDirectory) {
                            Desktop.getDesktop().open(directory)
                            result = true

                        }
                    }
                }
            }

            else -> {
                result = invalidateCommand(command)

            }
        }

        return result

    }
}