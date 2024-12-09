package io.github.cinema2d.luaverse

import io.github.cinema2d.luaverse.system_interaction.LuaSource

class Command(command: Array<String>) {
    // Map commands here.
    private val commands = mapOf(
        "help" to ::helpCommand,
        "build" to ::buildCommand,
        "list" to ::listDirCommand
    )

    init {
        val commandAction = commands[command[0].lowercase()]

        if (commandAction != null) {
            if (!commandAction(command)) {
                // TODO: Improve this. This should only be temporary while the commands are fleshed out.
                println("Unknown error.")

            }

        } else {
            println("Unknown command: $command")

        }
    }

    private fun invalidateCommand(command: Array<String>): Boolean {
        println("${command.joinToString(separator = " ")} is not a valid command.")
        // Always returns false so that it may be called functionally, if desired.
        return false

    }

    // Build commands below here.
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

    private fun buildCommand(command: Array<String>): Boolean {
        var result: Boolean = false

        fun build(path: String): Boolean {
            val file: LuaSource = LuaSource(path)
            return file.build()

        }

        // IntelliJ keeps suggestion to "simplify this comparison" by removing the statement altogether. However, this
        // is extremely counterintuitive.
        if (command[1] != null) {
            result = build(command[1])

        // In case one doesn't include the path as the second argument when they invoke the build command, we will give
        // them another chance to specify it.
        } else {
            while (true) {
                println("Please specify the path to the Lua source code. \"Makefile\" should be present in the directory.")
                val input: String? = readlnOrNull()

                if (input != null) {
                    result = build(input)
                    break

                }
            }
        }

        return result

    }

    private fun listDirCommand(command: Array<String>): Boolean {
        // Alternatively, this next line can be commented out and the command will ignore any commands after the first
        // one. So long as the first one is "list".
        if (command.size > 1) return invalidateCommand(command)

        println("\n")
        println("[[[DIRECTORY LISTING]]]")
        println("\n")

        for ((dirName, dirInfo) in Settings.directories) {
            println(dirName)
            println(dirInfo[0])
            println(dirInfo[1])
            println("\n")

        }

        println("[[[END OF LISTING]]]")
        println("\n")

        return true

    }
}