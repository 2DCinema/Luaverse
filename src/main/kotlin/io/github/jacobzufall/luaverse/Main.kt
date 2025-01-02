package io.github.jacobzufall.luaverse

fun main() {
    println("Luaverse : Lua version manager for Windows systems.")
    println("Say \"help\" for a list of commands.")

    // Main loop
    while (true) {
        val input: String? = readlnOrNull()

        if (input != null) {
            if (input.lowercase() == "exit") {
                break
            }

            // I don't know if I should use List or Array.
            Command(input.split(" ").toList())

        } else {
            println("Usage: luaverse <command> <args>")
        }
    }

    println("Luaverse : Process ending...")
}