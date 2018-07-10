package pl.socketbyte.thousand.server.netty.listener

import io.netty.channel.Channel
import pl.socketbyte.thousand.shared.netty.NettyListener
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketPlayerChoice

object PlayerChoiceListener : NettyListener {
    override fun received(connection: Channel, packet: Packet) {
        if (packet !is PacketPlayerChoice)
            return
    }
}