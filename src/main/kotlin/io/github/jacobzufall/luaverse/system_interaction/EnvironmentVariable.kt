package io.github.jacobzufall.luaverse.system_interaction

open class EnvironmentVariable(variable: String) {
    val regKey: String = "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment"
    val envVar: String = variable

    /**
     * The environment variable values organized into a list.
     */
    val values: List<String>
        get() {
            val process: Process = ProcessBuilder("cmd.exe", "/c", "reg query \"$regKey\"").start()

            val output: MutableList<String> = process.inputStream.bufferedReader().readText().split("    ").toMutableList()
            process.waitFor()

            val pathValues: MutableList<String> = output[output.indexOf(envVar) + 2].split(";").toMutableList()
            // Sometimes it shows escape characters at the end, which we don't want.
            if (pathValues.last() == "\r\n") pathValues.removeLast()

            return pathValues.toList()
        }

    /**
     * The environment variable values as they are presented in the registry, which is as a string delineated by
     * semicolons.
     */
    val rawValues: String
        get() = values.joinToString(separator = ";")
}