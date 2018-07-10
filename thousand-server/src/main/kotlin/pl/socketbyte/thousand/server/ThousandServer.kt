package pl.socketbyte.thousand.server

import pl.socketbyte.thousand.server.core.Game
import pl.socketbyte.thousand.server.netty.NettyServer
import pl.socketbyte.thousand.shared.settings.Settings

val settings = Settings.load("config.ini")
lateinit var game: Game
lateinit var server: NettyServer

val serverThread = ServerThread()

/**
 * Main class of Thousand server
 */
fun main(args: Array<String>) {
    serverThread.start()
}

fun printlnSync(text: String = "") {
    serverThread.info(text)
}

fun printSync(text: String = "") {
    serverThread.info(text)
}