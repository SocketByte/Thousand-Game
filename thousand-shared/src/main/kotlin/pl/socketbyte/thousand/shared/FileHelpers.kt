package pl.socketbyte.thousand.shared

import java.util.stream.Collectors
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import sun.misc.IOUtils
import java.util.*


/**
 * Very simple method to copy contents from resource file
 * to another file
 *
 * Uses reified type to get valid class loader
 *
 * @param resource Resource path
 */
inline fun <reified T> File.writeContentsFromResource(resource: String) {
    val input = T::class.java.classLoader.getResourceAsStream(resource)
    if (input != null) {
        val reader = BufferedReader(InputStreamReader(input))
        val lines = reader
                .lines()
                .collect(Collectors
                        .joining(System.lineSeparator())) ?: return
        this.writeText(lines)
    }
    return
}