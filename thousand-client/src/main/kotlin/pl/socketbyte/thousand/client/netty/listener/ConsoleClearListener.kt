package pl.socketbyte.thousand.client.netty.listener

import io.netty.channel.Channel
import pl.socketbyte.thousand.shared.clearScreen
import pl.socketbyte.thousand.shared.netty.NettyListener
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketConsoleClear

object ConsoleClearListener : NettyListener {
    override fun received(connection: Channel, packet: Packet) {
        if (packet !is PacketConsoleClear)
            return

        clearScreen()
    }
}