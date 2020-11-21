package com.socket;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * 记录日志信息
 */
public class BankServer {
    private static Service service = new Service();
    private JFrame frame;
    // 往日志写入文字
    private static String journal;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    BankServer window = new BankServer();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Server server = new Server();
    }

    public BankServer() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 663, 583);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("银行日志打印框");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("American Typewriter", Font.BOLD, 26));
        lblNewLabel.setForeground(Color.BLUE);
        lblNewLabel.setBounds(150, 21, 365, 51);
        frame.getContentPane().add(lblNewLabel);

        JTextArea textArea = new JTextArea();
        textArea.setBackground(new Color(255, 255, 255));
        textArea.setForeground(Color.BLACK);
        textArea.setFont(new Font("Andale Mono", Font.PLAIN, 22));
        textArea.setLineWrap(true);
        new Thread(()->{
            String s = "服务器已经启动\n";
            while(true){
                if(journal!=null){
                    s+=journal;
                    journal="";
                }
                textArea.setText(s);
            }
        }).start();
        frame.getContentPane().add(textArea);

        JScrollPane jsp = new JScrollPane(textArea);
        //设置矩形大小.参数依次为(矩形左上角横坐标x,矩形左上角纵坐标y，矩形长度，矩形宽度)
        jsp.setBounds(31, 92, 600, 434);
        //默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
        jsp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //把滚动条添加到容器里面
        frame.getContentPane().add(jsp);
    }


    static class Server{
        public Server() {
            try {
                ServerSocket server = new ServerSocket(8888);
                System.out.println("服务器已经启动");
                //服务端唯一的一个输入界面
                Scanner input = new Scanner(System.in);

                ExecutorService pool = new ThreadPoolExecutor(
                        10,
                        10,
                        10,
                        TimeUnit.SECONDS,
                        new SynchronousQueue<>(),
                        new ThreadFactory() {
                            @Override
                            public Thread newThread(Runnable r) {
                                Thread th = new Thread(r);
//                                th.setName("客户端线程");
                                return th;
                            }
                        }
                );

                while (true) {
                    java.net.Socket socket = server.accept();//等待状态
                    //对应的每个客户端
                    pool.execute(()->{
                        System.out.println("客户端已经连接上");
                        journal = Thread.currentThread().getName()+"客户端已经连接上\n";
                        try {
                            Scanner in = new Scanner(socket.getInputStream());//向自己输入
                            PrintWriter out = new PrintWriter(
                                    socket.getOutputStream(), true);
                            while (true) {
                                // 获取客户端的读入
                                String msg = in.nextLine();//IO阻塞
//                                System.out.println("接收的值："+msg);
                                Response text = requestObtain(msg);
                                String fa = text.getResultCode()+","+text.getId()+","+text.getMoney()+","+text.getState();
//                                System.out.println("发送的是："+fa);
                                out.println(fa);
                                error(text.getResultCode());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    });
                }

            } catch(IOException e){
                e.printStackTrace();
            }

        }
    }

    // 密码次数技术
    private static Integer count = 0;
    // 本地拷贝
    private static ThreadLocal<Integer> tl = new ThreadLocal<Integer>(){
        @Override
        protected Integer initialValue() {
            return count;
        }
    };

    public static Response requestObtain(String msg){
        String[] obj = msg.split(",");
        ResultCode code = null;
        switch (obj[0]){
            case "卡号验证":
                code = service.checkID(new Request(obj[1],null,null,null,null,null),true);
                journal = Thread.currentThread().getName()+"请求验证卡号，";
                break;
            case "密码验证":
                code = service.checkPassWord(new Request(obj[1],obj[2],null,null,null,null));
//                System.out.println(code);
                journal = Thread.currentThread().getName()+"请求密码验证，";
                if(code==ResultCode.ERROR_PASSWORD){
//                    count++;
                    tl.set(tl.get()+1);
                }else {
//                    count=0;
                    tl.set(0);
                }
                if(tl.get()==3){
                    System.out.println("进来了");
                    service.lockId(new Request(obj[1],obj[2],null,null,null,null));
                    journal = "密码输入次数达到三次，账户已被冻结";
//                    count=0;
                    tl.set(0);
                }
                break;
            case "存款":
                code = service.checkDeposit(new Request(obj[1],null,obj[2],null,null,null),true);
                journal = Thread.currentThread().getName()+ "请求存款操作，";
                break;
            case "取款":
                code = service.checkDraw(new Request(obj[1],null,obj[2],null,null,null));
                journal = Thread.currentThread().getName()+"请求取款操作，";
                break;
            case "转账":
                code = service.checkTransfer(new Request(obj[1],null,obj[2],null,obj[3],null));
                journal = Thread.currentThread().getName()+"请求转账操作，";
                break;
            case "查询":
                 Accunts text = service.balance(new Request(obj[1],null,null,null,null,null));
                journal = Thread.currentThread().getName()+"请求查询操作，";
                return new Response(text.getId(),text.getMoney()+"",text.getState()?"0":"1",ResultCode.SUCCESS);
        }
        return new Response(code);
    }

    public static void error(ResultCode code){
        switch (code){
            case SUCCESS:
                journal += "请求成功\n";
                break;
            case ERROR_LESSMONEY:
                journal += "请求失败，该账户余额不足\n";
                break;
            case ERROR_NOTID:
                journal += "请求失败，该账户卡号不存在\n";
                break;
            case ERROR_STATE:
                journal += "请求失败，该账户已被冻结\n";
                break;
            case ERROR_PASSWORD:
                journal += "请求失败，该账户的密码输入错误\n";
        }
    }
}