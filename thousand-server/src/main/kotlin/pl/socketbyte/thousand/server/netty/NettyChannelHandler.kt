package pl.socketbyte.thousand.server.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.TimeoutException
import io.netty.handler.timeout.WriteTimeoutException
import pl.socketbyte.thousand.server.core.Player
import pl.socketbyte.thousand.server.game
import pl.socketbyte.thousand.server.printlnSync
import pl.socketbyte.thousand.shared.RED
import pl.socketbyte.thousand.shared.YELLOW_BRIGHT
import pl.socketbyte.thousand.shared.netty.NettyListener
import pl.socketbyte.thousand.shared.packet.Packet
import pl.socketbyte.thousand.shared.packet.PacketPlayerLogin
import pl.socketbyte.thousand.shared.packet.PacketSendMessage
import pl.socketbyte.thousand.shared.packet.data.MessageType
import java.util.concurrent.TimeUnit

class NettyChannelHandler(
        private val server: NettyServer,
        private val listeners: List<NettyListener>)
    : SimpleChannelInboundHandler<Any>() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        if (server.clients.size >= 4) {
            server.write(ctx.channel(), PacketSendMessage(MessageType.PRINT_LINE,
                    RED + "Server is full"))
            ctx.disconnect()
            return
        }
        server.addClient(ctx.channel())
        val playerId = server.clients.size - 1

        server.executors.schedule({
            if (game.getPlayer(playerId) == null) {
                // player not authorized in time
                server.write(ctx.channel(), PacketSendMessage(MessageType.PRINT_LINE,
                        YELLOW_BRIGHT + "You were disconnected from the server because " +
                                "you did not enter your name in time (20 sec), please try again."))
                ctx.channel().disconnect()
            }
        }, 20, TimeUnit.SECONDS)
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
        if (msg !is Packet)
            return

        if (msg is PacketPlayerLogin) {
            val name: String =
                    if (msg.name == "")
                        "Player " + (server.clients.size - 1)
                    else msg.name

            val player = game.addPlayer(Player(
                    (server.clients.size - 1),
                    name,
                    ctx.channel(),
                    ctx.channel().id().asShortText()))

            printlnSync("Player $name joined the game (${server.clients.size}/4)")
            game.clearScreen(player)
            game.broadcastPrintln(YELLOW_BRIGHT + "Player $name joined the game (${server.clients.size}/4)")
            return
        }

        for (adapter in listeners) {
            adapter.received(ctx.channel(), msg)
        }
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext?) {
        ctx?.flush()
    }
}