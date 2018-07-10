package pl.socketbyte.thousand.shared.netty.kryo

import com.esotericsoftware.kryo.Kryo
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketKeepAlive
import pl.socketbyte.thousand.shared.packet.PacketPlayerChoice
import pl.socketbyte.thousand.shared.packet.PacketSendMessage
import pl.socketbyte.thousand.shared.packet.data.MessageType

object KryoSharedRegister {

    fun registerAll(kryo: Kryo) {
        kryo.register(MessageType::class.java)
        kryo.register(Packet::class.java)
        kryo.register(PacketKeepAlive::class.java)
        kryo.register(PacketSendMessage::class.java)
        kryo.register(PacketPlayerChoice::class.java)
    }

}