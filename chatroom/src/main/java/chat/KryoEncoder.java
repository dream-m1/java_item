package chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * netty的编码器，将字节写入
 * 封装了kryo的序列化器
 *
 * kryo是一个高性能的序列化/反序列化工具，由于其变长存储特性并使用了字节码生成机制，拥有较高的运行速度和较小的体积。
 */
public class KryoEncoder<T> extends MessageToByteEncoder<Object> {

    private KryoSerializer serializer = new KryoSerializer();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          Object obj, ByteBuf byteBuf) throws Exception {
        byte[] bytes = serializer.serialize(obj);
        int dataLen = bytes.length;
        //将消息的长度作为消息头
        byteBuf.writeInt(dataLen);
        //消息的字节码为消息体
        byteBuf.writeBytes(bytes);
    }
}
