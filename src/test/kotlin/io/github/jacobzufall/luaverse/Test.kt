package io.github.jacobzufall.luaverse

fun getSystemEnvironmentVariables(): Map<String, String> {
    val systemVariables = mutableMapOf<String, String>()
    try {
        // Run the 'reg' command to query the system environment variables
        val process = ProcessBuilder(
            "cmd.exe", "/c", "reg query \"HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment\""
        ).start()

        // Read the output
        val output: String = process.inputStream.bufferedReader().readText()
        process.waitFor()

        val outputList: List<String> = output.split("    ")
        val pathIndexes: List<Int> = listOf(
            outputList.indexOf("Path"),
            outputList.indexOf("Path") + 1,
            outputList.indexOf("Path") + 2
        )

        println(outputList[pathIndexes[0]])
        println(outputList[pathIndexes[1]])
        println(outputList[pathIndexes[2]])


    } catch (e: Exception) {
        e.printStackTrace()
    }
    return systemVariables
}

fun main() {
    val systemVariables = getSystemEnvironmentVariables()
//    println("System Environment Variables:")
//    systemVariables.forEach { (key, value) ->
//        println("$key = $value")
//    }
}
