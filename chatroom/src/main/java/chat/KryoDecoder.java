package chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 用来解码
 */
public class KryoDecoder extends ByteToMessageDecoder{

    private  final int HEAD_LENGTH = 4;

    private KryoSerializer serializer = new KryoSerializer();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf,
                          List<Object> list) throws Exception {
        //如果消息头不完整
        if(byteBuf.readableBytes() < HEAD_LENGTH) {
            System.out.println("消息头不完整");
            return;
        }
        //记录当前position
        byteBuf.markReaderIndex();
        int msgBodyLen = byteBuf.readInt();
        //如果消息体长度小于0，关闭连接
        if(msgBodyLen < 0) {
            System.out.println("消息体长度小于0");
            channelHandlerContext.close();
        }
        //如果消息体不完整
        if(byteBuf.readableBytes() < msgBodyLen){
            System.out.println("消息体不完整");
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] msgBody = new byte[msgBodyLen];
        byteBuf.readBytes(msgBody);
        //反序列化并加入到
        list.add(serializer.deserialize(msgBody));
    }
}
