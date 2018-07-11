package pl.socketbyte.thousand.client.netty.listener

import io.netty.channel.Channel
import pl.socketbyte.thousand.client.GameThread
import pl.socketbyte.thousand.client.client
import pl.socketbyte.thousand.client.gameThread
import pl.socketbyte.thousand.shared.netty.NettyListener
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketPlayerChoice

object PlayerChoiceListener : NettyListener {
    override fun received(connection: Channel, packet: Packet) {
        if (packet !is PacketPlayerChoice)
            return
        val input = gameThread.timedInput("Please input your choice (10 seconds):", 10)
        if (input == null) {
            client!!.writeResponse(packet, PacketPlayerChoice(-1))
            return
        }
        client!!.writeResponse(packet, PacketPlayerChoice(input.toInt()))
    }
}