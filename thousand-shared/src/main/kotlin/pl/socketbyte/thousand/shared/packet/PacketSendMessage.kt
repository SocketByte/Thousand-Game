package pl.socketbyte.thousand.shared.packet

import pl.socketbyte.thousand.shared.packet.data.MessageType

data class PacketSendMessage(val type: MessageType = MessageType.PRINT_LINE,
                             val text: String = "")
    : Packet()