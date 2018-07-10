package pl.socketbyte.thousand.shared.packet

data class PacketSendMessage(val text: String)
    : Packet()