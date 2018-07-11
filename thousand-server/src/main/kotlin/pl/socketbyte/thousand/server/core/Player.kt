package pl.socketbyte.thousand.server.core

import io.netty.channel.Channel
import pl.socketbyte.thousand.server.server
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketSendMessage
import pl.socketbyte.thousand.shared.packet.data.MessageType

data class Player(val id: Int,
                  val name: String,
                  val channel: Channel,
                  val channelId: String,
                  var score: Int = 0) {
    fun sendPacket(packet: Packet) {
        server.write(channel, packet)
    }

    fun print(text: String = "") {
        sendPacket(PacketSendMessage(MessageType.PRINT, text))
    }

    fun println(text: String = "") {
        sendPacket(PacketSendMessage(MessageType.PRINT_LINE, text))
    }

    fun printlnNative(text: String = "") {
        sendPacket(PacketSendMessage(MessageType.NATIVE, text))
    }

}