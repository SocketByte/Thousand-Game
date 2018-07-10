package pl.socketbyte.thousand.server.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
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

        println("Client ${ctx.channel().remoteAddress()} connected. (Waiting for authorization)")
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        println("Client ${ctx.channel().remoteAddress()} disconnected.")
        server.removeClient(ctx.channel())
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        // Do nothing
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