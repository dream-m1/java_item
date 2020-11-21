package chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class NettyServer {

    private int port;

    // 并发转串行
    //消息队列
    private LinkedBlockingQueue<Request> rspQ =new LinkedBlockingQueue<>();

    //channelMap key为客户端的用户名
    private ConcurrentHashMap<String,ChannelHandlerContext>
            ctxMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, ChannelHandlerContext> getCtxMap() {
        return ctxMap;
    }

    public LinkedBlockingQueue<Request> getRspQ() {
        return rspQ;
    }

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        Request request = new Request();
        request.setOperType(RequestOperType.LOCAL);
        request.setMsg("服务器启动中......");
        rspQ.put(request);
        // 1 创建两个线程组bossGroup,workerGroup
        // 用于处理服务器端接收客户端连接的
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 进行网络通信的（网络读写的）
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();

//            this.nsh = new NettyServerHandler();
            b.group(bossGroup, workerGroup)
                    //启用非阻塞IO
                    .channel(NioServerSocketChannel.class)
                    //启用INFO日志级别
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 设置tcp缓冲区
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 设置发送缓冲大小
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024)
                    // 这是接收缓冲大小
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024)
                    // 保持连接
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    //配置任务处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new KryoEncoder());
                            ch.pipeline().addLast(new KryoDecoder());
                            ch.pipeline().addLast(new NettyServerHandler(rspQ,ctxMap));
                        }
                    });
            // 绑定端口，同步等待绑定成功
            ChannelFuture f = b.bind(port).sync();

            request = new Request();
            request.setOperType(RequestOperType.LOCAL);
            request.setMsg("服务器已启动");
            rspQ.put(request);
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
