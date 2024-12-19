package io.github.jacobzufall.luaverse.system_interaction

import java.io.File

// I followed this tutorial for web scraping: https://www.zenrows.com/blog/kotlin-web-scraping#request
import it.skrape.core.*
import it.skrape.fetcher.*

import io.github.jacobzufall.luaverse.Settings

class LuaSource(path: String = "") {
    var luaVersionFiles: MutableMap<String, String> = mutableMapOf()

    private val luaFtp = skrape(HttpFetcher) {
        request { url = "https://www.lua.org/ftp/" }

        response {
            htmlDocument {
                val files = findAll("a")
                    .filter { it.hasAttribute("href") }
                    .map { element ->
                        val name = element.text
                        val href = element.attribute("href")
                        name to href
                    }

                files.forEach {(name, href) ->
                    if (name.startsWith("lua") && !name.startsWith("lua-all")) {
                        // This doesn't work for older versions that only had two numbers.
                        val version: String = name.substring(4, 9)
                        luaVersionFiles[version] = href
                    }
                }
            }
        }
    }


    fun build() {

    }


    // Everything below this comment will be deprecated.
    private val luaFolder: String = path.split("\\").last()
    private val luaVersion: String = luaFolder.split("-").last()

    fun build_old(): Boolean {
        val command: String = String.format("build.bat \"%s\" \"%s\"", luaVersion, Settings.directories["build"])

        try {
            // I originally did this in Lua, and had ChatGPT convert it to Kotlin since I wasn't sure how to do it, sorry.
            val process = ProcessBuilder(command.split(" "))
                .directory(File(System.getProperty("user.dir")))
                .start()

            val exitCode = process.waitFor()

            if (exitCode == 0) {
                println("Batch file executed successfully.")

            } else {
                println("Error: Batch file execution failed with exit code $exitCode.")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Temporary. Should return true or false based on the success of the build.
        return true
    }
}

fun main() {
    val luaSource: LuaSource = LuaSource()
    for ((name, link) in luaSource.luaVersionFiles) {
        println("$name | $link")
    }
}