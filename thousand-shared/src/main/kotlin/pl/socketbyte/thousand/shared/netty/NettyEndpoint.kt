package pl.socketbyte.thousand.shared.netty

import com.esotericsoftware.kryo.Kryo

/**
 * Netty endpoint interface containing basic client/server functions like ssl, start and adding listeners
 */
interface NettyEndpoint {
    val kryo: ThreadLocal<Kryo>
    val futureResolver: FutureResolver

    fun applyCertificate()
    fun start()
    fun close()
    fun addListener(listener: NettyListener)
}