package io.github.cinema2d.luaverse

fun restore(command: Array<String>) {
    val hardRestore = command?.get(2) == "hard" ?: println("Whoops")
    print(hardRestore)
}

fun main() {
    val myArray: Array<String> = arrayOf("restore", "file")
    restore(myArray)
}