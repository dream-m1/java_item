package chat.gui;

import chat.*;
import io.netty.channel.ChannelHandlerContext;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javax.swing.WindowConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ChatServerUI extends javax.swing.JFrame {
    private JMenuBar jMenuBar1;
    private JMenu jMenu1;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton5;
    private JTextField jTextField1;
    private JLabel jLabel2;
    private JComboBox jComboBox1;
    private JLabel jLabel1;
    private JTextArea jTextArea1;
    private JButton jButton4;
    private JMenu jMenu2;

    private NettyServer nettyServer = new NettyServer(8888);

//    private NettyServerHandler nsh;
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChatServerUI inst = new ChatServerUI();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
    }

    public ChatServerUI() {
        super();
        initGUI();
    }

    private void initGUI() {
        try {
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("聊天室服务器");
            getContentPane().setLayout(null);
            {
                jButton1 = new JButton();
                getContentPane().add(jButton1);
                jButton1.setText("\u7aef\u53e3\u8bbe\u7f6e");
                jButton1.setBounds(18, 20, 95, 41);
                jButton1.setFont(new java.awt.Font("Microsoft YaHei UI",0,12));
            }
            {
                jButton2 = new JButton();
                getContentPane().add(jButton2);
                //启动服务
                jButton2.setText("\u542f\u52a8\u670d\u52a1");
                jButton2.setBounds(126, 21, 89, 40);
                jButton2.addActionListener((e)->{
                    //轮询消息队列 简单实现
                    new Thread(()->{
                        while(true){
                            try {
                                Request request = nettyServer.getRspQ().take();
//                                if(request.getOperType()==RequestOperType.CHATMSG){
//                                    jTextArea1.append(request.getFrom()+" "+request.getExpression()+" 对 "+request.getTo()+" 说："+request.getMsg()+"\n");
//                                }else{
                                    jTextArea1.append(request.getMsg()+"\n");
//                                }
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                    }).start();

                    //启动服务器
                    new Thread(()->{
                        try {
                            nettyServer.start();
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                            jTextArea1.append("服务器启动异常\n");
                        }
                    }).start();
                });
            }
            {
                jButton3 = new JButton();
                getContentPane().add(jButton3);
                jButton3.setText("\u505c\u6b62\u670d\u52a1");
                jButton3.setBounds(232, 19, 89, 43);
            }
            {
                jButton4 = new JButton();
                getContentPane().add(jButton4);
                jButton4.setText("\u9000\u51fa");
                jButton4.setBounds(332, 20, 88, 42);
            }
            {
                jTextArea1 = new JTextArea();
                getContentPane().add(jTextArea1);
                jTextArea1.setBounds(7, 77, 483, 429);
                jTextArea1.setEnabled(false);


            }
            {
                jLabel1 = new JLabel();
                getContentPane().add(jLabel1);
                jLabel1.setText("\u53d1\u9001\u81f3:");
                jLabel1.setBounds(18, 525, 60, 30);
            }
            {
                ComboBoxModel jComboBox1Model =
                        new DefaultComboBoxModel(
                                new String[] { "所有人"});
                jComboBox1 = new JComboBox();
                getContentPane().add(jComboBox1);
                jComboBox1.setModel(jComboBox1Model);
                jComboBox1.setBounds(73, 522, 92, 36);
            }
            {
                jLabel2 = new JLabel();
                getContentPane().add(jLabel2);
                jLabel2.setText("\u53d1\u9001\u6d88\u606f:");
                jLabel2.setBounds(12, 573, 55, 32);
            }
            {
                jTextField1 = new JTextField();
                getContentPane().add(jTextField1);
                jTextField1.setBounds(73, 577, 329, 27);
            }
            {
                //发送按钮
                jButton5 = new JButton();
                getContentPane().add(jButton5);
                jButton5.setText("\u53d1\u9001");
                jButton5.setBounds(414, 579, 70, 26);
                jButton5.addActionListener((e)->{
                    // 群发
                    ConcurrentHashMap<String, ChannelHandlerContext>
                            ctxMap = nettyServer.getCtxMap();
                    for (ChannelHandlerContext ctx:
                            ctxMap.values()) {
                        ctx.writeAndFlush(new Response(ResponseOperType.CHATMSG,
                                jTextField1.getText()));
                    }
                });
            }
            {
                jMenuBar1 = new JMenuBar();
                setJMenuBar(jMenuBar1);
                {
                    jMenu1 = new JMenu();
                    jMenuBar1.add(jMenu1);
                    jMenu1.setText("\u670d\u52a1");
                }
                {
                    jMenu2 = new JMenu();
                    jMenuBar1.add(jMenu2);
                    jMenu2.setText("\u5e2e\u52a9");
                }
            }
            pack();
            this.setSize(511, 709);
        } catch (Exception e) {
            //add your error handling code here
            e.printStackTrace();
        }
    }

}
