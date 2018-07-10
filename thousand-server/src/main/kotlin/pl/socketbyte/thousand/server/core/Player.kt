package pl.socketbyte.thousand.server.core

import io.netty.channel.Channel
import pl.socketbyte.thousand.shared.packet.Packet

data class Player(val id: Int, val channel: Channel) {
    fun sendPacket(packet: Packet) {

    }
}