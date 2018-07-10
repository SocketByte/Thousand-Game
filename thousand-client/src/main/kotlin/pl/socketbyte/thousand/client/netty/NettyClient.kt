package pl.socketbyte.thousand.client.netty

import com.esotericsoftware.kryo.Kryo
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.MultithreadEventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import kotlinx.coroutines.experimental.async
import pl.socketbyte.thousand.shared.netty.FutureResolver
import pl.socketbyte.thousand.shared.netty.NettyEndpoint
import pl.socketbyte.thousand.shared.netty.NettyListener
import pl.socketbyte.thousand.shared.netty.kryo.KryoDecoder
import pl.socketbyte.thousand.shared.netty.kryo.KryoEncoder
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketKeepAlive

open class NettyClient(val address: String, val port: Int)
    : NettyEndpoint {

    private val listeners = mutableListOf<NettyListener>()
    private var sslContext: SslContext? = null

    override val kryo: Kryo = Kryo()
    override val futureResolver: FutureResolver = FutureResolver()

    lateinit var channel: Channel

    override fun applyCertificate() {
        if (System.getProperty("ssl") != null) {
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build()
        }
    }

    override fun start() {
        applyCertificate()

        val channelClass: Class<out Channel> =
                if (Epoll.isAvailable())
                    EpollSocketChannel::class.java
                else
                    NioSocketChannel::class.java

        val group: MultithreadEventLoopGroup =
                if (Epoll.isAvailable())
                    EpollEventLoopGroup()
                else NioEventLoopGroup()

        val bootstrap = Bootstrap()
        bootstrap
                .group(group)
                .channel(channelClass)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        if (ch == null)
                            return

                        val pipeline = ch.pipeline()
                        if (sslContext != null)
                            pipeline.addLast(sslContext?.newHandler(ch.alloc(), address, port))

                        pipeline.addLast("frame", LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                        pipeline.addLast("decoder", KryoDecoder(kryo))
                        pipeline.addLast("encoder", KryoEncoder(kryo, 4 * 1024, 16 * 1024))
                        pipeline.addLast("handler", NettyChannelHandler(listeners))
                    }
                })

        val future = bootstrap.connect(address, port)
        channel = future.channel()

        // Add default request transformer
        addListener(object : NettyListener {
            override fun received(connection: Channel, packet: Packet) {
                futureResolver.complete(packet)
            }
        })

        // Add default TCP keep alive responder
        addListener(object : NettyListener {
            override fun received(connection: Channel, packet: Packet) {
                if (packet !is PacketKeepAlive)
                    return

                writeResponse(packet, PacketKeepAlive())
            }
        })
    }

    override fun close() {
        channel.close()
    }

    fun writeResponse(request: Packet, response: Packet) {
        response.id = request.id
        write(response)
    }

    suspend inline fun <reified T> writeAndRequest(packet: Packet, block: (T) -> Unit) {
        write(packet)

        futureResolver.register(packet.id)
        val result = futureResolver.await(packet.id)

        if (result == null || result !is T)
            return

        block(result)
    }

    suspend inline fun <reified T> writeAndRequest(packet: Packet): T? {
        write(packet)

        futureResolver.register(packet.id)
        val result = futureResolver.await(packet.id)

        if (result == null || result !is T)
            return null

        return result
    }

    fun write(packet: Any) {
        channel.writeAndFlush(packet)
    }

    override fun addListener(listener: NettyListener) {
        listeners.add(listener)
    }
}