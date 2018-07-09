package pl.socketbyte.thousand.shared.packet

import java.io.Serializable
import java.util.concurrent.ThreadLocalRandom

/**
 * Abstract Packet class to send between server/client
 * Every packet sent must extend this class
 */
abstract class Packet : Serializable {
    companion object {
        private const val serialVersionUID: Long = 2716586918473261845L
    }

    /**
     * Auto-generated packet identificator.
     *
     * It's used to receive correct callbacks from the server/client.
     * Can be changed accordingly to request.
     *
     * @return Long number between -9223372036854775808 and 9223372036854775807
     * @since 1.0
     * @author SocketByte
     */
    var id = ThreadLocalRandom.current()
            .nextLong(Long.MIN_VALUE, Long.MAX_VALUE)
}