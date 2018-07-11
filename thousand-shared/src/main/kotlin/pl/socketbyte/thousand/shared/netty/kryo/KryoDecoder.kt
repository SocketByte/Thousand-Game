package pl.socketbyte.thousand.shared.netty.kryo

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.ByteBufferInput
import com.esotericsoftware.kryo.pool.KryoPool
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class KryoDecoder(private val kryo: Kryo) : ByteToMessageDecoder() {
    private val input = ByteBufferInput()

    override fun decode(ctx: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        val nioBuf = byteBuf.nioBuffer()
        input.setBuffer(nioBuf)
        out.add(kryo.readClassAndObject(input))
        byteBuf.skipBytes(nioBuf.position())
    }
}