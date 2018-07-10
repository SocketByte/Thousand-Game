package pl.socketbyte.thousand.client

import pl.socketbyte.thousand.shared.*
import java.util.*
import java.util.Collections.nCopies
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.logging.Logger


/**
 * This class is definitely not finished :D just testing stuff here :D
 *
 * This is the main class of Thousand client app
 */

val gameThread = GameThread()

fun main(args: Array<String>) {
    gameThread.start()
}

fun printlnSync(text: String = "") {
    gameThread.println(text)
}

fun printSync(text: String = "") {
    gameThread.print(text)
}