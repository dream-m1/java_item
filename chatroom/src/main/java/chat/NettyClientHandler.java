package chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author edz
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private LinkedBlockingQueue<Response> rspQ;

    public NettyClientHandler(LinkedBlockingQueue<Response> rspQ) {
        this.rspQ = rspQ;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Response rsp = new Response(ResponseOperType.LIAN,"已成功连接到服务器，请登录");
        rspQ.put(rsp);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //简单实现，后期需要大量修改
        try {
            Response response = (Response) msg;
            rspQ.put(response);
        }finally{
            //释放ByteBuf里的消息资源
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

