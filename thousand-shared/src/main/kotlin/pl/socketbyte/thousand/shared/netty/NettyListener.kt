package pl.socketbyte.thousand.shared.netty

import io.netty.channel.Channel
import pl.socketbyte.thousand.shared.packet.Packet

/**
 * Server or client adapter for listening incoming packets
 */
interface NettyListener {

    /**
     * Invoked when packet is delivered to the server or client
     *
     * @param[connection] [Channel] the packet is from
     * @param[packet] [Packet] received
     */
    fun received(connection: Channel, packet: Packet)

}