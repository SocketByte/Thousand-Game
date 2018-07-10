package pl.socketbyte.thousand.client.netty.listener

import io.netty.channel.Channel
import pl.socketbyte.thousand.client.gameThread
import pl.socketbyte.thousand.client.jna.NativeConsole
import pl.socketbyte.thousand.client.printlnSync
import pl.socketbyte.thousand.shared.RESET
import pl.socketbyte.thousand.shared.netty.NettyListener
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketSendMessage
import pl.socketbyte.thousand.shared.packet.data.MessageType

object SendMessageListener : NettyListener {
    override fun received(connection: Channel, packet: Packet) {
        if (packet !is PacketSendMessage)
            return

        when (packet.type) {
            MessageType.NATIVE -> gameThread.printlnNative(packet.text)
            MessageType.PRINT_LINE -> gameThread.println(packet.text + RESET)
            MessageType.PRINT -> gameThread.print(packet.text + RESET)
        }
    }
}