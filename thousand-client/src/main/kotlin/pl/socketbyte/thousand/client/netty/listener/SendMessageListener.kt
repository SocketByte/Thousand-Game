package pl.socketbyte.thousand.client.netty.listener

import io.netty.channel.Channel
import pl.socketbyte.thousand.client.printlnSync
import pl.socketbyte.thousand.shared.netty.NettyListener
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketSendMessage

object SendMessageListener : NettyListener {
    override fun received(connection: Channel, packet: Packet) {
        if (packet !is PacketSendMessage)
            return

        printlnSync(packet.text)
    }
}