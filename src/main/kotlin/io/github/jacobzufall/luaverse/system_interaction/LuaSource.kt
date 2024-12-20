package io.github.jacobzufall.luaverse.system_interaction

import java.io.File

// I (kinda) followed this tutorial for web scraping: https://www.zenrows.com/blog/kotlin-web-scraping#request
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
                        val name: String = element.text
                        val href: String = element.attribute("href")
                        name to href // Never heard of infix functions before.
                    }

                files.forEach {(name, href) ->
                    if (name.startsWith("lua") && !name.startsWith("lua-all")) {
                        /*
                        By default, name appears as "lua-5.4.7.tar.gz. So the first thing we do is split lua off from
                        the rest of the string, and then take that string and split it at the dots so it looks like
                        ["5", "4", "7", "tar", "gz"].
                        */
                        val splitName: List<String> = name.split("-")[1].split(".")
                        /*
                        This adds href to luaVersionFiles with the key being its version. We know that the first index
                        will always be the first number of the version. But since some versions look like x.x.x while
                        others look like x.x, we need to know where the last number is. splitName.size - 2 will equal
                        either 4 or 5, which is where the last version number is.
                        */
                        luaVersionFiles[splitName.subList(0, splitName.size - 2).joinToString(".")] = "https://www.lua.org/ftp/$href"
                    }
                }
            }
        }
    }

    fun getAvailableLuaVersions() {
        print("\n")
        println("If you do not see your desired Lua version, please navigate to https://www.lua.org/ftp/ and download the file manually.")
        println("--- AVAILABLE LUA VERSIONS ---")
        for ((name, _) in luaVersionFiles) {
            println(name)
        }
        println("--- END OF LIST ---")
        print("\n")
    }

    fun build() {

    }


    // Everything below this comment will be deprecated.
    private val luaFolder: String = path.split("\\").last()
    private val luaVersion: String = luaFolder.split("-").last()

    @Deprecated("This function is deprecated. Use build() instead.")
    fun oldBuild(): Boolean {
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