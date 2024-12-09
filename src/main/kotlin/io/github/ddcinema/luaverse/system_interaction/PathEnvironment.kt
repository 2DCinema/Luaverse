package io.github.ddcinema.luaverse.system_interaction

import com.google.gson.Gson

class PathEnvironment {
    private var envVariables: List<String> = System.getenv("Path").split(";")

    init {
        println(envVariables)
    }

    /**
     * Creates a backup of the current Path system environment variable.
     */
    private fun backup() {


    }

    private fun findVersion(version: String): String? {
        for (pathvariable in envVariables) {
            val pathObjects: List<String> = pathvariable.split("\\")

            if (pathObjects[pathObjects.size - 2] == "lua$version") {
                return pathvariable

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

fun main() {
    PathEnvironment()
}