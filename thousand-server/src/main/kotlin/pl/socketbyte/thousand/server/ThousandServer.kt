package pl.socketbyte.thousand.server

import pl.socketbyte.thousand.server.core.Game
import pl.socketbyte.thousand.server.netty.NettyServer
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketKeepAlive
import pl.socketbyte.thousand.shared.settings.Settings

val settings = Settings.load("config.ini")
lateinit var game: Game
lateinit var server: NettyServer

/**
 * Main class of Thousand server
 */
fun main(args: Array<String>) {
    ServerThread().start()
}