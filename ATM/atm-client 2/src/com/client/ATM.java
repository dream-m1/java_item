package com.client;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import com.socket.Response;
import com.socket.Request;
import com.socket.ResultCode;

public class ATM {
    private String cardID;

    private JFrame frame;

    private MainPanel mainPanel; //主页(输入卡号界面）
    private PasswordPanel passwordPanel; //输入密码界面
    private YewuPanel yewuPanel;  //选择办理的业务界面
    private LookPanel lookPanel;  //查看余额界面
    private DepositPanel depositPanel;  //存款界面
    private WithdrawPanel withdrawPanel;  //取款界面
    private TransferPanel transferPanel;  //转账界面
    private isPingTiaoPanel ispingTiaoPanel;

    private Socket client;
    private ResultCode resultCode;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    /**
     * Create the application.
     */
    //192.168.11.248
    public ATM() {
        try {
            client=new Socket("192.168.11.248",8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initialize();
    }


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ATM window = new ATM();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.getContentPane().setLayout(null);
        mainPanel=new MainPanel();
        frame.setContentPane(mainPanel);
        frame.validate();
    }

    class MainPanel extends JPanel {
        private JTextField textField;

        /**
         * Create the panel.
         */
        public MainPanel() {
            setLayout(null);

            try {
                oos = new ObjectOutputStream(client.getOutputStream());
                ois = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            JLabel lblNewLabel = new JLabel("欢迎使用半圆ATM");
            lblNewLabel.setForeground(Color.DARK_GRAY);
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
            lblNewLabel.setBounds(119, 26, 199, 64);
            add(lblNewLabel);

            JLabel lblNewLabel_1 = new JLabel("请输入卡号");
            lblNewLabel_1.setBounds(119, 132, 65, 27);
            add(lblNewLabel_1);

            textField = new JTextField();
            textField.setBounds(203, 124, 130, 42);
            add(textField);
            textField.setColumns(10);

            JButton btnNewButton = new JButton("确认");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String s=textField.getText();
                    if(s.matches("\\d{5}")){
                        Response msg = null;
                        try {
                            oos.writeObject(new Request(s,null,null,null,null,"卡号验证"));
                            msg = (Response) ois.readObject();
                        } catch (IOException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                        resultCode=msg.getResultCode();
                        switch (resultCode){
                            case ERROR_NOTID:
                                JOptionPane.showMessageDialog(null, "该卡号不存在", "Warning Message!", 0);
                                textField.setText("");
                                frame.setContentPane(mainPanel);
                                frame.validate();
                                break;
                            case ERROR_STATE:
                                JOptionPane.showMessageDialog(null, "该卡号被冻结", "Warning Message!", 0);
                                textField.setText("");
                                frame.setContentPane(mainPanel);
                                frame.validate();
                                break;
                            case SUCCESS:
                                cardID=s;
                                textField.setText("");
                                passwordPanel=new PasswordPanel();
                                frame.setContentPane(passwordPanel);
                                frame.validate();
                                break;
                            default:
                                System.out.println("未知状态");
                                break;
                        }
                    }
                    else {
                        //弹出警告框
                        JOptionPane.showMessageDialog(null, "卡号格式不正确，请输入5位数字的卡号", "Warning Message!", 0);
                        textField.setText("");
                        frame.setContentPane(mainPanel);
                        frame.validate();
                    }
                }
            });
            btnNewButton.setBackground(Color.BLACK);
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setBounds(242, 205, 84, 42);
            add(btnNewButton);
        }
    }

    class PasswordPanel extends JPanel {
        private JTextField textField;
        private int count=3;

        /**
         * Create the panel.
         */
        public PasswordPanel() {
            setLayout(null);

            JLabel lblNewLabel = new JLabel("请输入密码");
            lblNewLabel.setBounds(128, 95, 87, 36);
            add(lblNewLabel);

            textField = new JTextField();
            textField.setBounds(211, 95, 130, 36);
            add(textField);
            textField.setColumns(10);

            JButton btnNewButton = new JButton("确定");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //把密码发给服务器（调用方法）
                    //接收到返回码判断密码是否正确(用switch-case）
                    //如果成功，转到选择业务界面
                    String s=textField.getText();
                    Response msg = null;
                    try {
                        oos.writeObject(new Request(cardID,s,null,null,null,"密码验证"));
                        msg = (Response) ois.readObject();
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    resultCode=msg.getResultCode();
                    switch (resultCode){
                        case ERROR_PASSWORD:
                            count--;
                            if(count==0){
                                JOptionPane.showMessageDialog(null, "三次密码输入错误，该卡号被冻结", "Warning Message!", 0);
                                textField.setText("");
                                frame.setContentPane(mainPanel);
                                frame.validate();
                            }
                            else {
                                JOptionPane.showMessageDialog(null, "密码输入错误,您还有"+count+"次机会", "Warning Message!", 0);
                                textField.setText("");
                                frame.setContentPane(passwordPanel);
                                frame.validate();
                            }
                            break;
                        case SUCCESS:
                            textField.setText("");
                            yewuPanel=new YewuPanel();
                            frame.setContentPane(yewuPanel);
                            frame.validate();
                            break;
                        default:
                            System.out.println("未知状态");
                            break;
                    }
                }
            });
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setBounds(275, 165, 67, 41);
            add(btnNewButton);

        }
    }

    class YewuPanel extends JPanel {

        /**
         * Create the panel.
         */
        public YewuPanel() {
            setLayout(null);

            JLabel lblNewLabel = new JLabel("请选择您要办理的业务");
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel.setBounds(108, 34, 232, 36);
            add(lblNewLabel);

            JButton btnLook = new JButton("查询");
            btnLook.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //请求服务器，得到结果
//                    request.sendMessage("查询"+","+cardID);
                    Response msg =null;
                    try {
                        oos.writeObject(new Request(cardID,null,null,null,null,"查询"));
                        msg = (Response) ois.readObject();
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
//                    server.sendMessage(new Request(cardID,null,null,null,null,"查询"));

//                    String msg=response.getMessage();
//                    Response msg = server.getMessage();
//                    String[] sarr=msg.split(",");
//                    String yue=sarr[2];
//                    lookPanel=new LookPanel(yue);
                    lookPanel=new LookPanel(msg.getMoney());
                    frame.setContentPane(lookPanel);
                    frame.validate();
                }
            });
            btnLook.setForeground(Color.ORANGE);
            btnLook.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
            btnLook.setBounds(74, 93, 78, 46);
            add(btnLook);

            JButton btnDeposit = new JButton("存款");
            btnDeposit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    depositPanel=new DepositPanel();
                    frame.setContentPane(depositPanel);
                    frame.validate();
                }
            });
            btnDeposit.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
            btnDeposit.setForeground(Color.ORANGE);
            btnDeposit.setBounds(184, 93, 78, 46);
            add(btnDeposit);

            JButton btnTransfer = new JButton("转账");
            btnTransfer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    transferPanel=new TransferPanel();
                    frame.setContentPane(transferPanel);
                    frame.validate();
                }
            });
            btnTransfer.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
            btnTransfer.setForeground(Color.ORANGE);
            btnTransfer.setBounds(74, 151, 78, 46);
            add(btnTransfer);

            JButton btnWithdraw = new JButton("取款");
            btnWithdraw.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    withdrawPanel=new WithdrawPanel();
                    frame.setContentPane(withdrawPanel);
                    frame.validate();
                }
            });
            btnWithdraw.setForeground(Color.ORANGE);
            btnWithdraw.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
            btnWithdraw.setBounds(293, 93, 78, 46);
            add(btnWithdraw);

            JButton btnQuit = new JButton("退卡");
            btnQuit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.setContentPane(mainPanel);
                    frame.validate();
/*                    try {
                        client.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }*/
                    // System.exit(0);
                }
            });
            btnQuit.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
            btnQuit.setForeground(Color.ORANGE);
            btnQuit.setBounds(184, 151, 78, 46);
            add(btnQuit);

        }
    }

    class LookPanel extends JPanel {

        /**
         * Create the panel.
         */
        public LookPanel(String yue) {
            setLayout(null);
            String s="您当前的余额为："+yue+"元";
            JLabel lblNewLabel = new JLabel(s);
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
            lblNewLabel.setBounds(38, 55, 381, 107);
            add(lblNewLabel);

            JButton btnNewButton = new JButton("返回");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.setContentPane(yewuPanel);
                    frame.validate();
                }
            });
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setBounds(279, 194, 73, 41);
            add(btnNewButton);

        }
    }

    class DepositPanel extends JPanel {
        private JTextField textField;

        /**
         * Create the panel.
         */
        public DepositPanel() {
            setLayout(null);

            JLabel lblNewLabel = new JLabel("请输入存款金额");
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel.setBounds(88, 108, 116, 36);
            add(lblNewLabel);

            textField = new JTextField();
            textField.setBounds(202, 108, 130, 36);
            add(textField);
            textField.setColumns(10);

            JButton btnNewButton = new JButton("确定");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int money=Integer.parseInt(textField.getText());
                    if(money%100==0){
//                        request.sendMessage("存款"+","+cardID+","+money);
                        Response msg = null;
                        try {
                            oos.writeObject(new Request(cardID,null,money+"",null,null,"存款"));
                            msg = (Response)ois.readObject();
                        } catch (IOException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
//                        server.sendMessage(new Request(cardID,null,money+"",null,null,"存款"));

//                        String msg=response.getMessage();
//                        Response msg = server.getMessage();
//                        String[] sarr=msg.split(",");
//                        resultCode=ResultCode.valueOf(sarr[0]);
                        resultCode = msg.getResultCode();
                        switch (resultCode){
                            case SUCCESS:
                                textField.setText("");
                                ispingTiaoPanel=new isPingTiaoPanel("存款成功",null,money,0);
                                frame.setContentPane(ispingTiaoPanel);
                                frame.validate();
                                break;
                            default:
                                System.out.println("未知状态");
                                break;
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "输入的金额必须是100的整数倍", "Warning Message!", 0);
                        textField.setText("");
                        frame.setContentPane(depositPanel);
                        frame.validate();
                    }
                }
            });
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setBounds(262, 172, 67, 42);
            add(btnNewButton);

        }
    }

    class WithdrawPanel extends JPanel {
        private JTextField textField;

        /**
         * Create the panel.
         */
        public WithdrawPanel() {

            setLayout(null);

            JLabel lblNewLabel = new JLabel("请输入取款金额");
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel.setBounds(88, 108, 116, 36);
            add(lblNewLabel);

            textField = new JTextField();
            textField.setBounds(202, 108, 130, 36);
            add(textField);
            textField.setColumns(10);

            JButton btnNewButton = new JButton("确定");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int money=Integer.parseInt(textField.getText());
                    if(money%100==0){
                        if(money<=5000){
//                            request.sendMessage("取款"+","+cardID+","+money);
                            Response msg = null;
                            try {
                                oos.writeObject(new Request(cardID,null,money+"",null,null,"取款"));
                                msg = (Response)ois.readObject();
                            } catch (IOException | ClassNotFoundException ex) {
                                ex.printStackTrace();
                            }

//                            server.sendMessage(new Request(cardID,null,money+"",null,null,"取款"));
//                            String msg=response.getMessage();
//                            String[] sarr=msg.split(",");
//                            resultCode=ResultCode.valueOf(sarr[0]);
//                            Response msg = server.getMessage();
                            resultCode = msg.getResultCode();
                            switch (resultCode){
                                case ERROR_LESSMONEY:
                                    JOptionPane.showMessageDialog(null, "您的余额不足，请重新选择您要办的业务", "Warning Message!", 0);
                                    textField.setText("");
                                    frame.setContentPane(yewuPanel);
                                    frame.validate();
                                    break;
                                case SUCCESS:
                                    textField.setText("");
                                    ispingTiaoPanel=new isPingTiaoPanel("取款成功",null,money,1);
                                    frame.setContentPane(ispingTiaoPanel);
                                    frame.validate();
                                    break;
                                default:
                                    System.out.println("未知状态");
                                    break;
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "取款金额不能大于5000元", "Warning Message!", 0);
                            textField.setText("");
                            frame.setContentPane(withdrawPanel);
                            frame.validate();
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "输入的金额必须是100的整数倍", "Warning Message!", 0);
                        textField.setText("");
                        frame.setContentPane(withdrawPanel);
                        frame.validate();
                    }
                }
            });
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setBounds(262, 172, 67, 42);
            add(btnNewButton);

        }
    }

    class TransferPanel extends JPanel {
        private JTextField textID;
        private JTextField textMoney;

        /**
         * Create the panel.
         */
        public TransferPanel() {
            setLayout(null);

            JLabel lblNewLabel = new JLabel("请输入对方卡号");
            lblNewLabel.setBounds(89, 107, 100, 30);
            add(lblNewLabel);

            textID = new JTextField();
            textID.setBounds(191, 103, 130, 38);
            add(textID);
            textID.setColumns(10);

            JLabel lblNewLabel_1 = new JLabel("请输入转账金额");
            lblNewLabel_1.setBounds(89, 157, 100, 30);
            add(lblNewLabel_1);

            textMoney = new JTextField();
            textMoney.setBounds(191, 153, 130, 38);
            add(textMoney);
            textMoney.setColumns(10);

            JButton btnNewButton = new JButton("确认");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String s=textID.getText();
                    int money=Integer.parseInt(textMoney.getText());
                    if(s.matches("\\d{5}")){
                        if(!s.equals(cardID)){
//                            request.sendMessage("转账"+","+cardID+","+money+","+s);
                            Response msg = null;
                            try {
                                oos.writeObject(new Request(cardID,null,money+"",null,s,"转账"));
                                msg = (Response)ois.readObject();
                            } catch (IOException | ClassNotFoundException ex) {
                                ex.printStackTrace();
                            }

//                            server.sendMessage(new Request(cardID,null,money+"",null,s,"转账"));
//                            String msg=response.getMessage();
//                            String[] sarr=msg.split(",");
//                            resultCode=ResultCode.valueOf(sarr[0]);
//                            Response msg = server.getMessage();
                            resultCode = msg.getResultCode();
                            switch (resultCode){
                                case ERROR_NOTID:
                                    JOptionPane.showMessageDialog(null, "对方卡号不存在，请重新选择您要办的业务", "Warning Message!", 0);
                                    textID.setText("");
                                    textMoney.setText("");
                                    frame.setContentPane(yewuPanel);
                                    frame.validate();
                                    break;
                                case ERROR_STATE:
                                    JOptionPane.showMessageDialog(null, "对方卡号被冻结，请重新选择您要办的业务", "Warning Message!", 0);
                                    textID.setText("");
                                    textMoney.setText("");
                                    frame.setContentPane(yewuPanel);
                                    frame.validate();
                                    break;
                                case ERROR_LESSMONEY:
                                    JOptionPane.showMessageDialog(null, "您的余额不足，请重新选择您要办的业务", "Warning Message!", 0);
                                    textID.setText("");
                                    textMoney.setText("");;
                                    frame.setContentPane(yewuPanel);
                                    frame.validate();
                                    break;
                                case SUCCESS:
                                    //成功
                                    textID.setText("");
                                    textMoney.setText("");
                                    ispingTiaoPanel=new isPingTiaoPanel("转账成功",s,money,2);
                                    frame.setContentPane(ispingTiaoPanel);
                                    frame.validate();
                                    break;
                                default:
                                    System.out.println("未知状态");
                                    break;
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "不能转账给自己", "Warning Message!", 0);
                            textID.setText("");
                            textMoney.setText("");
                            frame.setContentPane(transferPanel);
                            frame.validate();
                        }
                    }
                    else{
                        //弹出警告框
                        JOptionPane.showMessageDialog(null, "卡号格式不正确，请输入5位数字的卡号", "Warning Message!", 0);
                        textID.setText("");
                        textMoney.setText("");
                        frame.setContentPane(transferPanel);
                        frame.validate();
                    }
                }
            });
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setBounds(135, 212, 112, 38);
            add(btnNewButton);

        }
    }

    class isPingTiaoPanel extends JPanel {

        /**
         * Create the panel.
         */
        public isPingTiaoPanel(String s,String destID,int money,int a) {
            setLayout(null);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            JLabel success = new JLabel(s);
            success.setBounds(91, 62, 127, 40);
            add(success);

            JLabel lblNewLabel = new JLabel("是否打印凭条？");
            lblNewLabel.setBounds(91, 104, 162, 32);
            add(lblNewLabel);

            JButton btnYes = new JButton("打印");
            btnYes.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //生成pingtiao.txt文件
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter("pingtiao.txt"))) {
                        Date d=new Date();
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String s= sdf.format(d);
                        //存款
                        if(a==0){
                            bw.write(s+" 您的卡号："+cardID+" 办理了存款业务，存款金额为："+
                                    money);
                        }
                        //取款
                        else if(a==1){
                            bw.write(s+" 您的卡号："+cardID+" 办理了取款业务，取款金额为："+
                                    money);
                        }
                        //转账
                        else {
                            bw.write(s+" 您的卡号："+cardID+" 办理了转账业务，对方账号为："+destID+"，转账金额为："+
                                    money);
                        }
                        bw.flush();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    frame.setContentPane(yewuPanel);
                    frame.validate();
                }
            });
            btnYes.setForeground(Color.ORANGE);
            btnYes.setBounds(126, 148, 71, 40);
            add(btnYes);

            JButton btnNo = new JButton("不打印");
            btnNo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.setContentPane(yewuPanel);
                    frame.validate();
                }
            });
            btnNo.setForeground(Color.ORANGE);
            btnNo.setBounds(221, 148, 71, 40);
            add(btnNo);

        }

    }

}
