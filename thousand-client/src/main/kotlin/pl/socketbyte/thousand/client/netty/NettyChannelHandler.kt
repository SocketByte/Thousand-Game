package pl.socketbyte.thousand.client.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import pl.socketbyte.thousand.client.client
import pl.socketbyte.thousand.client.gameThread
import pl.socketbyte.thousand.shared.netty.NettyListener
import pl.socketbyte.thousand.shared.packet.Packet

class NettyChannelHandler(
        private val listeners: List<NettyListener>)
    : SimpleChannelInboundHandler<Any>() {

    override fun channelActive(ctx: ChannelHandlerContext?) {

    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        client = null
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
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