package pl.socketbyte.thousand.server.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.TimeoutException
import io.netty.handler.timeout.WriteTimeoutException
import pl.socketbyte.thousand.server.printlnSync
import pl.socketbyte.thousand.shared.netty.NettyListener
import pl.socketbyte.thousand.shared.packet.Packet

class NettyChannelHandler(
        private val server: NettyServer,
        private val listeners: List<NettyListener>)
    : SimpleChannelInboundHandler<Any>() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        if (server.isBanned(ctx.channel())) {
            ctx.disconnect()
            return
        }
        if (server.clients.size >= 4) {
            // server.write(ctx.channel(), PacketDisconnectResult("server is full"))
            ctx.disconnect()
            return
        }
        server.addClient(ctx.channel())

        printlnSync("Client ${ctx.channel().remoteAddress()} joined the game (${server.clients.size}/4)")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        server.removeClient(ctx.channel())

        printlnSync("Client ${ctx.channel().remoteAddress()} left the game. (${server.clients.size}/4)")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        if (cause is WriteTimeoutException) {
            printlnSync("${ctx.channel().remoteAddress()} timeouted.")
            server.removeClient(ctx.channel())
            ctx.disconnect()
        }
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
        if (server.isBanned(ctx.channel())) {
            ctx.disconnect()
            return
        }

        if (msg !is Packet)
            return

        for (adapter in listeners) {
            adapter.received(ctx.channel(), msg)
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
        ctx?.flush()
    }
}