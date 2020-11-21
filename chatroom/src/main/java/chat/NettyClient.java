package chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author edz
 */
public class NettyClient {
    private String host;
    private int port;

    private Channel channel;

    //消息队列
    private LinkedBlockingQueue<Response> rspQ =new LinkedBlockingQueue<>();

    public LinkedBlockingQueue<Response> getRspQ() {
        return rspQ;
    }

    public Channel getChannel() {
        return channel;
    }

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        Response rsp = new Response(ResponseOperType.LOCAL,
                "正在连接服务器......");
        rspQ.put(rsp);
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 配置启动辅助类
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    //启用INFO日志级别
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new KryoEncoder());
                            ch.pipeline().addLast(new KryoDecoder());
                            ch.pipeline().addLast(new NettyClientHandler(rspQ));
                        }
                    });
            // 异步连接服务器，同步等待连接成功
            ChannelFuture f = b.connect(host, port).sync();
            this.channel = f.channel();
            // 等待连接关闭
            this.channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient("127.0.0.1", 8889);
        client.start();

    }
}
