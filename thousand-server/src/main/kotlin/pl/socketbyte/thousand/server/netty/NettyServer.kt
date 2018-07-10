package pl.socketbyte.thousand.server.netty

import com.esotericsoftware.kryo.Kryo
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.SelfSignedCertificate
import kotlinx.coroutines.experimental.async
import pl.socketbyte.thousand.shared.netty.FutureResolver
import pl.socketbyte.thousand.shared.netty.NettyEndpoint
import pl.socketbyte.thousand.shared.netty.NettyListener
import pl.socketbyte.thousand.shared.netty.kryo.KryoDecoder
import pl.socketbyte.thousand.shared.netty.kryo.KryoEncoder
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketKeepAlive
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class NettyServer(private val port: Int)
    : NettyEndpoint {

    private val executors = Executors.newScheduledThreadPool(2)

    val clients = mutableListOf<Channel>()

    private val bannedClients = mutableListOf<Channel>()
    private val listeners = mutableListOf<NettyListener>()
    private var sslContext: SslContext? = null

    override val kryo: Kryo = Kryo()
    override val futureResolver: FutureResolver = FutureResolver()

    private lateinit var channel: Channel

    fun addClient(channel: Channel) {
        clients.add(channel)
    }

    fun hasClient(channel: Channel): Boolean {
        return clients.contains(channel)
    }

    fun removeClient(channel: Channel) {
        clients.remove(channel)
    }

    fun banClient(channel: Channel) {
        bannedClients.add(channel)
    }

    fun isBanned(channel: Channel): Boolean {
        return bannedClients.contains(channel)
    }

    override fun applyCertificate() {
        if (System.getProperty("ssl") != null) {
            val ssc = SelfSignedCertificate()
            sslContext = SslContextBuilder
                    .forServer(ssc.certificate(), ssc.privateKey())
                    .build()
        }
    }

    override fun start() {
        applyCertificate()

        val channelClass: Class<out ServerChannel> =
                if (Epoll.isAvailable())
                    EpollServerSocketChannel::class.java
                else
                    NioServerSocketChannel::class.java

        val bossGroup: MultithreadEventLoopGroup
        val workerGroup: MultithreadEventLoopGroup

        if (Epoll.isAvailable()) {
            bossGroup = EpollEventLoopGroup(1)
            workerGroup = EpollEventLoopGroup(4)
        }
        else {
            bossGroup = NioEventLoopGroup(1)
            workerGroup = NioEventLoopGroup(4)
        }

        val bootstrap = ServerBootstrap()
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(channelClass)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        if (ch == null)
                            return

                        val pipeline = ch.pipeline()
                        if (sslContext != null)
                            pipeline.addLast(sslContext?.newHandler(ch.alloc()))

                        pipeline.addLast("frame", LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                        pipeline.addLast("decoder", KryoDecoder(kryo))
                        pipeline.addLast("encoder", KryoEncoder(kryo, 4 * 1024, 16 * 1024))
                        pipeline.addLast("handler", NettyChannelHandler(this@NettyServer, listeners))
                    }
                })

        channel = bootstrap.bind(port).channel()

        // Add default request transformer
        addListener(object : NettyListener {
            override fun received(connection: Channel, packet: Packet) {
                futureResolver.complete(packet)
            }
        })

        // Run TCP keep-alive task
        executors.scheduleAtFixedRate({
            for (client in clients) {
                @Suppress("DeferredResultUnused")
                async {
                    // If received, do nothing.
                    writeAndRequestElseDisconnect<PacketKeepAlive>(client, PacketKeepAlive()) { }
                }
            }
        }, 5, 5, TimeUnit.SECONDS)
    }

    override fun close() {
        channel.close()
    }

    fun writeResponse(channel: Channel, request: Packet, response: Packet) {
        response.id = request.id
        write(channel, response)
    }

    suspend inline fun <reified T> writeAndRequest(channel: Channel, packet: Packet, block: (T) -> Unit) {
        write(channel, packet)

        futureResolver.register(packet.id)
        val result = futureResolver.await(packet.id)

        if (result == null || result !is T)
            return

        block(result)
    }

    suspend inline fun <reified T> writeAndRequest(channel: Channel, packet: Packet): T? {
        write(channel, packet)

        futureResolver.register(packet.id)
        val result = futureResolver.await(packet.id)

        if (result == null || result !is T)
            return null

        return result
    }

    private suspend inline fun <reified T> writeAndRequestElseDisconnect(channel: Channel, packet: Packet, block: (T) -> Unit) {
        try {
            write(channel, packet)

            futureResolver.register(packet.id)
            val result = futureResolver.await(packet.id)

            if (result == null || result !is T)
                return

            block(result)
        } catch (ex: CancellationException) {
            println("Client $channel did not respond, disconnecting...")
            removeClient(channel)
            channel.disconnect()
        }
    }

    fun write(channel: Channel, packet: Packet) {
        channel.writeAndFlush(packet)
    }

    fun writeAll(packet: Packet) {
        for (channel in clients)
            channel.writeAndFlush(packet)
    }

    override fun addListener(listener: NettyListener) {
        listeners.add(listener)
    }
}