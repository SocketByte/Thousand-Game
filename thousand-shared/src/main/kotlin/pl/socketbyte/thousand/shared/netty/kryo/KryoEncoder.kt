package pl.socketbyte.thousand.shared.netty.kryo

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Output
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class KryoEncoder(private val kryo: Kryo, bufferSize: Int, maxBufferSize: Int) : MessageToByteEncoder<Any>() {
    private val output: Output = Output(bufferSize, maxBufferSize)

    override fun encode(ctx: ChannelHandlerContext, msg: Any, out: ByteBuf) {
        output.clear()
        kryo.writeClassAndObject(output, msg)
        val len = output.position()
        out.writeShort(len)
        out.writeBytes(output.buffer, 0, len)
    }
}