package club.banyuan.client;

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

public class ATM {
    private String cardID;
    private int atmMoney=18000;

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
    private Request request;
    private Response response;
    private ResultCode resultCode;


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
        request=new Request(client);
        response=new Response(client);
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
        frame.setBounds(100, 100, 768, 480);
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

            JLabel lblNewLabel = new JLabel("欢迎使用半圆ATM");
            lblNewLabel.setForeground(Color.DARK_GRAY);
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 30));
            lblNewLabel.setBounds(213, 70, 337, 67);
            add(lblNewLabel);

            JLabel lblNewLabel_2 = new JLabel("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            lblNewLabel_2.setForeground(Color.MAGENTA);
            lblNewLabel_2.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
            lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel_2.setBounds(30, 128, 711, 31);
            add(lblNewLabel_2);

            JLabel lblNewLabel_1 = new JLabel("请输入卡号");
            lblNewLabel_1.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
            lblNewLabel_1.setBounds(222, 209, 118, 42);
            add(lblNewLabel_1);

            textField = new JTextField();
            textField.setBounds(337, 206, 193, 54);
            add(textField);
            textField.setColumns(10);

            JButton btnNewButton = new JButton("确定");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String s=textField.getText();
                    if(s.matches("\\d{5}")){
                        request.sendMessage("卡号验证"+","+s);
                        String msg=response.getMessage();
                        String[] sarr=msg.split(",");
                        resultCode=ResultCode.valueOf(sarr[0]);
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
            btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
            btnNewButton.setBackground(Color.LIGHT_GRAY);
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setBounds(311, 308, 96, 61);
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
            lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
            lblNewLabel.setForeground(Color.BLUE);
            lblNewLabel.setBounds(187, 122, 142, 52);
            add(lblNewLabel);

            textField = new JTextField();
            textField.setBounds(358, 134, 185, 62);
            add(textField);
            textField.setColumns(10);

            JLabel lblNewLabel_1 = new JLabel("您总共有三次机会");
            lblNewLabel_1.setForeground(Color.GRAY);
            lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel_1.setFont(new Font("Lucida Grande", Font.PLAIN, 21));
            lblNewLabel_1.setBounds(150, 167, 204, 41);
            add(lblNewLabel_1);

            JLabel lblNewLabel_2 = new JLabel("///////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
            lblNewLabel_2.setForeground(Color.PINK);
            lblNewLabel_2.setBackground(Color.LIGHT_GRAY);
            lblNewLabel_2.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
            lblNewLabel_2.setBounds(6, 30, 756, 41);
            add(lblNewLabel_2);

            JButton btnNewButton = new JButton("确定");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //把密码发给服务器（调用方法）
                    //接收到返回码判断密码是否正确(用switch-case）
                    //如果成功，转到选择业务界面
                    String s=textField.getText();
                    request.sendMessage("密码验证"+","+cardID+","+s);
                    String msg=response.getMessage();
                    String[] sarr=msg.split(",");
                    resultCode=ResultCode.valueOf(sarr[0]);
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
            btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 26));
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setBounds(328, 232, 105, 62);
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
            lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 27));
            lblNewLabel.setForeground(Color.BLUE);
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel.setBounds(55, 65, 586, 56);
            add(lblNewLabel);

            JButton btnLook = new JButton("查询");
            btnLook.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //请求服务器，得到结果
                    request.sendMessage("查询"+","+cardID);
                    String msg=response.getMessage();
                    String[] sarr=msg.split(",");
                    String yue=sarr[2];
                    lookPanel=new LookPanel(yue);
                    frame.setContentPane(lookPanel);
                    frame.validate();
                }
            });
            btnLook.setForeground(Color.ORANGE);
            btnLook.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
            btnLook.setBounds(124, 166, 101, 74);
            add(btnLook);

            JButton btnDeposit = new JButton("存款");
            btnDeposit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    depositPanel=new DepositPanel();
                    frame.setContentPane(depositPanel);
                    frame.validate();
                }
            });
            btnDeposit.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
            btnDeposit.setForeground(Color.ORANGE);
            btnDeposit.setBounds(124, 263, 101, 74);
            add(btnDeposit);

            JButton btnTransfer = new JButton("转账");
            btnTransfer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    transferPanel=new TransferPanel();
                    frame.setContentPane(transferPanel);
                    frame.validate();
                }
            });
            btnTransfer.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
            btnTransfer.setForeground(Color.ORANGE);
            btnTransfer.setBounds(473, 166, 101, 74);
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
            btnWithdraw.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
            btnWithdraw.setBounds(124, 362, 101, 74);
            add(btnWithdraw);

            JButton btnQuit = new JButton("退卡");
            btnQuit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            btnQuit.setFont(new Font("Lucida Grande", Font.PLAIN, 30));
            btnQuit.setForeground(Color.ORANGE);
            btnQuit.setBounds(473, 263, 101, 74);
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
            lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 26));
            lblNewLabel.setBounds(106, 218, 566, 53);
            add(lblNewLabel);

            JButton btnNewButton = new JButton("返回");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.setContentPane(yewuPanel);
                    frame.validate();
                }
            });
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
            btnNewButton.setBounds(328, 345, 92, 66);
            add(btnNewButton);

            JLabel lbUser = new JLabel("尊敬的用户："+cardID);
            lbUser.setForeground(Color.GRAY);
            lbUser.setFont(new Font("Lucida Grande", Font.PLAIN, 19));
            lbUser.setBounds(135, 153, 472, 46);
            add(lbUser);

            JLabel lblNewLabel_2 = new JLabel("///////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
            lblNewLabel_2.setForeground(Color.PINK);
            lblNewLabel_2.setBackground(Color.LIGHT_GRAY);
            lblNewLabel_2.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
            lblNewLabel_2.setBounds(6, 30, 756, 41);
            add(lblNewLabel_2);

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
            lblNewLabel.setForeground(Color.BLUE);
            lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 21));
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel.setBounds(147, 154, 185, 51);
            add(lblNewLabel);

            textField = new JTextField();
            textField.setBounds(333, 154, 196, 51);
            add(textField);
            textField.setColumns(10);

            JButton btnNewButton = new JButton("确定");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int money=Integer.parseInt(textField.getText());
                    if(money%100==0){
                        request.sendMessage("存款"+","+cardID+","+money);
                        String msg=response.getMessage();
                        String[] sarr=msg.split(",");
                        resultCode=ResultCode.valueOf(sarr[0]);
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
            btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 29));
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setBounds(296, 260, 102, 58);
            add(btnNewButton);

            JLabel lblNewLabel_2 = new JLabel("///////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
            lblNewLabel_2.setForeground(Color.PINK);
            lblNewLabel_2.setBackground(Color.LIGHT_GRAY);
            lblNewLabel_2.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
            lblNewLabel_2.setBounds(6, 30, 756, 41);
            add(lblNewLabel_2);

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
            lblNewLabel.setForeground(Color.BLUE);
            lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 21));
            lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
            lblNewLabel.setBounds(147, 154, 185, 51);
            add(lblNewLabel);

            textField = new JTextField();
            textField.setBounds(333, 154, 196, 51);
            add(textField);
            textField.setColumns(10);

            JButton btnNewButton = new JButton("确定");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int money=Integer.parseInt(textField.getText());
                    if(money%100==0){
                        if(money<=5000){
                            if(money<=atmMoney){
                                request.sendMessage("取款"+","+cardID+","+money);
                                String msg=response.getMessage();
                                String[] sarr=msg.split(",");
                                resultCode=ResultCode.valueOf(sarr[0]);
                                switch (resultCode){
                                    case ERROR_LESSMONEY:
                                        JOptionPane.showMessageDialog(null, "您的余额不足，请重新选择您要办的业务", "Warning Message!", 0);
                                        textField.setText("");
                                        frame.setContentPane(yewuPanel);
                                        frame.validate();
                                        break;
                                    case SUCCESS:
                                        textField.setText("");
                                        atmMoney-=money;
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
                                JOptionPane.showMessageDialog(null, "此ATM现金不足，请换一台ATM取款", "Warning Message!", 0);
                                textField.setText("");
                                frame.setContentPane(yewuPanel);
                                frame.validate();
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
            btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 29));
            btnNewButton.setForeground(Color.GREEN);
            btnNewButton.setBounds(296, 260, 102, 58);
            add(btnNewButton);

            JLabel lblNewLabel_2 = new JLabel("///////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
            lblNewLabel_2.setForeground(Color.PINK);
            lblNewLabel_2.setBackground(Color.LIGHT_GRAY);
            lblNewLabel_2.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
            lblNewLabel_2.setBounds(6, 30, 756, 41);
            add(lblNewLabel_2);

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
            lblNewLabel.setForeground(Color.BLUE);
            lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 21));
            lblNewLabel.setBounds(180, 164, 148, 48);
            add(lblNewLabel);

            textID = new JTextField();
            textID.setBounds(350, 167, 166, 48);
            add(textID);
            textID.setColumns(10);

            JLabel lblNewLabel_1 = new JLabel("请输入转账金额");
            lblNewLabel_1.setForeground(Color.BLUE);
            lblNewLabel_1.setFont(new Font("Lucida Grande", Font.PLAIN, 21));
            lblNewLabel_1.setBounds(180, 221, 148, 48);
            add(lblNewLabel_1);

            textMoney = new JTextField();
            textMoney.setBounds(350, 224, 166, 48);
            add(textMoney);
            textMoney.setColumns(10);

            JButton btnNewButton = new JButton("确定");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String s=textID.getText();
                    int money=Integer.parseInt(textMoney.getText());
                    if(s.matches("\\d{5}")){
                        if(!s.equals(cardID)){
                            request.sendMessage("转账"+","+cardID+","+money+","+s);
                            String msg=response.getMessage();
                            String[] sarr=msg.split(",");
                            resultCode=ResultCode.valueOf(sarr[0]);
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
            btnNewButton.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
            btnNewButton.setBounds(277, 311, 106, 63);
            add(btnNewButton);

            JLabel lblNewLabel_2 = new JLabel("///////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
            lblNewLabel_2.setForeground(Color.PINK);
            lblNewLabel_2.setBackground(Color.LIGHT_GRAY);
            lblNewLabel_2.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
            lblNewLabel_2.setBounds(6, 30, 756, 41);
            add(lblNewLabel_2);

        }
    }

    class isPingTiaoPanel extends JPanel {

        /**
         * Create the panel.
         */
        public isPingTiaoPanel(String s,String destID,int money,int a) {
            setLayout(null);

            try {
                Thread.sleep(600);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            JLabel success = new JLabel(s);
            success.setFont(new Font("Lucida Grande", Font.PLAIN, 21));
            success.setBounds(91, 138, 165, 44);
            add(success);

            JLabel lblNewLabel = new JLabel("是否打印凭条？");
            lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 21));
            lblNewLabel.setBounds(91, 178, 199, 52);
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
            btnYes.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
            btnYes.setBounds(233, 263, 97, 61);
            add(btnYes);

            JButton btnNo = new JButton("不打印");
            btnNo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.setContentPane(yewuPanel);
                    frame.validate();
                }
            });
            btnNo.setForeground(Color.ORANGE);
            btnNo.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
            btnNo.setBounds(424, 263, 97, 61);
            add(btnNo);

            JLabel lblNewLabel_2 = new JLabel("///////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\");
            lblNewLabel_2.setForeground(Color.PINK);
            lblNewLabel_2.setBackground(Color.LIGHT_GRAY);
            lblNewLabel_2.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
            lblNewLabel_2.setBounds(6, 30, 756, 41);
            add(lblNewLabel_2);

        }

    }

}
