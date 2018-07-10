package pl.socketbyte.thousand.shared.packet

data class PacketPlayerChoice(val choice: Int = 0)
    : Packet()