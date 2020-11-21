package chat;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext ctx;

    // 消息
    private LinkedBlockingQueue<Request> rspQ;

    // 用户
    private ConcurrentHashMap<String,ChannelHandlerContext>
            ctxMap ;

    public NettyServerHandler(LinkedBlockingQueue<Request> rspQ,
                              ConcurrentHashMap<String,ChannelHandlerContext>
                                      ctxMap) {
        this.rspQ = rspQ;
        this.ctxMap = ctxMap;
    }

    public LinkedBlockingQueue<Request> getRspQ() {
        return rspQ;
    }


    public ConcurrentHashMap<String, ChannelHandlerContext> getCtxMap() {
        return ctxMap;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    // 记录用户的名字
    public Set<String> set;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        //此处只是群发消息的简单实现，后期需要修改key为客户端用户名
//        ctxMap.put((ctxMap.size()+1)+"",ctx);

        Request request = new Request();
        request.setOperType(RequestOperType.LOCAL);
        request.setMsg("某客户端已连接");
        rspQ.put(request);
//        System.out.println(rspQ);
    }

    /**
     * 核心方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
     //简单实现，后期需要大量修改
      try {
          Request request = (Request) msg;
          Response response;
          System.out.println(request.getOperType());
          System.out.println(request.toString());
          switch (request.getOperType()){
              // 登录
              case LOGIN:
                  // 取所有名字
                  set = ctxMap.keySet();
                  // 用户登录
                  ctxMap.put(request.getFrom(),ctx);
                  request.setOperType(RequestOperType.LOGIN);
                  request.setMsg("用户 "+request.getFrom()+" 上线");
                  response = new Response(ResponseOperType.LOCAL,"您以登录成功，和大家打个招呼吧");
                  ctxMap.get(request.getFrom()).writeAndFlush(response);
                  // 发送登录变更
                  // 采集所有用户名信息
                  String str = "";
                  for (String s:set) {
                      str += s+",";
                  }
                  response = new Response(ResponseOperType.LOGIN,str);
                  for (String s:set) {
                      ctxMap.get(s).writeAndFlush(response);
                  }
                  break;
                  // 设置注销
              case LOGOUT:
                  request.setOperType(RequestOperType.LOGOUT);
                  request.setMsg("用户 "+request.getFrom()+" 下线");
                  response = new Response(ResponseOperType.CHATMSG,"用户 "+request.getFrom()+" 以下线");
                  ctxMap.remove(request.getFrom());
                  set = ctxMap.keySet();
                  for (String user:set) {
                      ctxMap.get(user).writeAndFlush(response);
                  }
                  break;
                  // 设置通信
              case CHATMSG:
//                  set = ctxMap.keySet();
                  // 记录自己说的
                  String ziji = request.getMsg();
                  request.setMsg(request.getFrom()+" "+request.getExpression()+" 对 "+request.getTo()+" 说："+request.getMsg());
                  for (String user:set) {
                      if(user.equals(request.getFrom())){
                          response = new Response(ResponseOperType.CHATMSG, ziji );
                      }else {
                          response = new Response(ResponseOperType.CHATMSG, request.getMsg());
                      }
                      ctxMap.get(user).writeAndFlush(response);
                  }
                  break;
              case QIAO:
                  response = new Response(ResponseOperType.CHATMSG,request.getMsg());
                  ctxMap.get(request.getFrom()).writeAndFlush(response);
                  response = new Response(ResponseOperType.CHATMSG,request.getFrom()+" 悄悄的 对你说："+request.getMsg());
                  ctxMap.get(request.getTo()).writeAndFlush(response);
              default:
                  break;
          }
          rspQ.put(request);
//          Response response = new Response(ResponseOperType.CHATMSG,"已收到你的消息");
//          ChannelFuture f = ctx.writeAndFlush(response);
      }finally{
          //释放Buffer里的资源
          ReferenceCountUtil.release(msg);

      }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
