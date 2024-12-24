package io.github.jacobzufall.luaverse.utility

class VersionString(delimited_version: String) {
    /*
    A user can input a version number in any format, so long as it delimited consistently. For example, 1.2.3 is okay,
    since it is delimited, and it only has one delimiter. 1.2-3 and 123 are not okay, but 1.24.1 and 1-24-1 are.
    */
    init {
        val uniqueChars: Set<Char> = delimited_version.toSet()


    }
}