package pl.socketbyte.thousand.shared.netty

import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.withTimeout
import pl.socketbyte.thousand.shared.packet.Packet
import java.util.concurrent.CompletableFuture

/**
 * Future resolver containing all incoming callbacks and completing them upon arrival
 * Used for requesting data from client/server
 * Uses kotlin coroutines and [CompletableFuture] extension [CompletableFuture.await]
 */
class FutureResolver {

    private val futures =
            mutableMapOf<Long, CompletableFuture<Packet>>()

    /**
     * Completes the future and allows to unlock the coroutine
     *
     * @param[packet] [Packet] that arrived
     */
    fun complete(packet: Packet) {
        if (!futures.containsKey(packet.id))
            return // that means there's no registered request for that packet

        futures[packet.id]?.complete(packet)
    }

    /**
     * Creates new completable future for packet request based on packet id
     *
     * @param[id] [Packet.id]
     */
    fun register(id: Long) {
        futures[id] = CompletableFuture()
    }

    /**
     * Blocks the coroutine and waits for the packet to arrive
     * It also removes old, completed future as it's no longer useful
     *
     * @return [Packet] that can be nullable
     * @param[id] [Packet.id]
     */
    suspend fun await(id: Long): Packet? {
        val packet = withTimeout(3000) {
            futures[id]?.await()
        }
        futures.remove(id)
        return packet
    }

}