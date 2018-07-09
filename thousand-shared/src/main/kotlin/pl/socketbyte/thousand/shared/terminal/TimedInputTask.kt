package pl.socketbyte.thousand.shared.terminal

import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.IOException
import java.util.*
import java.util.concurrent.Callable


/**
 * Simple timed input task, gathers input using scanner
 */
class TimedInputTask : Callable<String> {

    override fun call(): String {
        val br = Scanner(System.`in`)
        var input: String
        do {
            try {
                while (!br.hasNext()) {
                    Thread.sleep(210)
                }
                input = br.nextLine()
            } catch (e: InterruptedException) {
                return ""
            }

        } while ("" == input)
        return input
    }

}