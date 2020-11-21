package chat.gui;
import chat.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import javax.swing.text.View;


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
public class ChatClientUI extends javax.swing.JFrame {
	private JMenuBar jMenuBar1;
	private JMenu jMenu1;
	private JMenu jMenu3;
	private JButton jButton1;
	private JButton jButton2;
	private JButton jButton3;
	private JLabel jLabel5;
	private JLabel jLabel4;
	private JButton jButton6;
	private JTextField jTextField1;
	private JLabel jLabel3;
	private JCheckBox jCheckBox1;
	private ButtonGroup buttonGroup1;
	private JComboBox jComboBox2;
	private JLabel jLabel2;
	private JComboBox jComboBox1;
	private JLabel jLabel1;
	private JTextArea jTextArea1;
	private JButton jButton5;
	private JButton jButton4;
	private JMenu jMenu2;

	private NettyClient nettyClient = new NettyClient("127.0.0.1", 8888);

	private Request request = new Request();
	private String inputValue;


	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ChatClientUI inst = new ChatClientUI();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public ChatClientUI() {
		super();
		initGUI();
	}
	
	private  void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setTitle("聊天室客户端");
			getContentPane().setLayout(null);
			{
				jButton1 = new JButton();
				getContentPane().add(jButton1);
				jButton1.setText("\u7528\u6237\u8bbe\u7f6e");
				jButton1.setBounds(12, 17, 92, 41);
				jButton1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
//						System.out.println("jButton1.actionPerformed, event="+evt);
//						Request request = new Request();
						//TODO add your code for jButton1.actionPerformed
						inputValue = JOptionPane.showInputDialog("请输入设置的用户名，默认为狂狂");
						// 为取消了的情况赋值
						inputValue = inputValue==null?"狂狂":inputValue;
//						System.out.println(inputValue);
						request.setFrom(inputValue);
					}
				});
			}
			{
				jTextArea1 = new JTextArea();
				getContentPane().add(jTextArea1);
				jTextArea1.setBounds(7, 76, 503, 423);
			}
			{
				jButton2 = new JButton();
				getContentPane().add(jButton2);
				jButton2.setText("\u8fde\u63a5\u8bbe\u7f6e");
				jButton2.setBounds(115, 17, 92, 41);
				jButton2.addActionListener((e)->{

					//轮询消息队列，简单实现
					new Thread(()->{
						while(true){
							try {
								Response rsp = nettyClient.getRspQ().take();
								if(rsp.getOperType()==ResponseOperType.LOGIN){
									String[] nameAll = rsp.getMsg().split(",");
									jLabel4.setText("\u5728\u7ebf\u4eba\u6570:");
									jLabel5.setText(nameAll.length+"\u4eba");
									DefaultComboBoxModel model = (DefaultComboBoxModel)jComboBox1.getModel();
									model.removeAllElements();
									model.addElement("所有人");
									for (String clientName : nameAll) {
										if(clientName.equals(inputValue)){
											continue;
										}
											model.addElement(clientName);
									}
									continue;
								}
								jTextArea1.append(rsp.getMsg()+"\n");
								if(rsp.getOperType()==ResponseOperType.LIAN){
									jButton3.setEnabled(true);
								}
							} catch (InterruptedException interruptedException) {
								interruptedException.printStackTrace();
							}

						}
					}).start();

					new Thread(()->{
						//连接服务器
						try {
							nettyClient.start();
						} catch (InterruptedException interruptedException) {
							interruptedException.printStackTrace();
						}
					}).start();
					jButton2.setEnabled(false);
				});
			}
			{
				// 登录
				jButton3 = new JButton();
				getContentPane().add(jButton3);
				jButton3.setText("\u767b\u5f55");
				jButton3.setBounds(218, 17, 64, 41);
				jButton3.setEnabled(false);
				jButton3.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						if(inputValue!=null){
							request.setOperType(RequestOperType.LOGIN);
							// 客户端发了一条数据 NettyServerHandler 接收 然后再存到 消息集合
							nettyClient.getChannel().writeAndFlush(request);
							jButton3.setEnabled(false);
							jButton6.setEnabled(true);
							jButton4.setEnabled(true);
						}else {
							JOptionPane.showMessageDialog(null, "你还没有设置昵称，请选择用户设置", "提示信息", JOptionPane.ERROR_MESSAGE);;
						}
					}
				});

			}
			{
				// 注销
				jButton4 = new JButton();
				getContentPane().add(jButton4);
				jButton4.setText("\u6ce8\u9500");
				jButton4.setBounds(293, 17, 64, 41);
				jButton4.setEnabled(false);
				jButton4.addActionListener((e)->{
					request.setOperType(RequestOperType.LOGOUT);
					// 客户端发了一条数据 NettyServerHandler 接收 然后再存到 消息集合
					nettyClient.getChannel().writeAndFlush(request);
					jButton3.setEnabled(true);
					jButton4.setEnabled(false);
					jButton6.setEnabled(false);
					inputValue = null;
					jTextArea1.setText("");
				});
			}
			{
				// 退出
				jButton5 = new JButton();
				getContentPane().add(jButton5);
				jButton5.setText("\u9000\u51fa");
				jButton5.setBounds(368, 17, 64, 41);
				jButton5.addActionListener((e)->{
					System.exit(0);
				});
			}

			{
				jLabel1 = new JLabel();
				getContentPane().add(jLabel1);
				jLabel1.setText("\u53d1\u9001\u81f3:");
				jLabel1.setBounds(12, 524, 45, 20);
			}
			{
				jLabel2 = new JLabel();
				getContentPane().add(jLabel2);
				jLabel2.setText("\u8868\u60c5:");
				jLabel2.setBounds(170, 526, 35, 17);
			}
			{
				ComboBoxModel jComboBox1Model =
						new DefaultComboBoxModel(
								new String[] { "所有人" });
				jComboBox1 = new JComboBox();
				getContentPane().add(jComboBox1);
				jComboBox1.setModel(jComboBox1Model);
				jComboBox1.setBounds(62, 518, 90, 32);
			}
			{
				ComboBoxModel jComboBox2Model =
						new DefaultComboBoxModel(
								new String[] { "开心的", "愤怒的" ,"沮丧的"});
				jComboBox2 = new JComboBox();
				getContentPane().add(jComboBox2);
				jComboBox2.setModel(jComboBox2Model);
				jComboBox2.setBounds(207, 519, 92, 30);
			}
			{
				jCheckBox1 = new JCheckBox();
				getContentPane().add(jCheckBox1);
				jCheckBox1.setText("\u6084\u6084\u8bdd");
				jCheckBox1.setBounds(387, 522, 65, 25);
			}
			{
				jLabel3 = new JLabel();
				getContentPane().add(jLabel3);
				jLabel3.setText("\u53d1\u9001\u6d88\u606f:");
				jLabel3.setBounds(12, 571, 64, 17);
			}
			{
				jTextField1 = new JTextField();
				getContentPane().add(jTextField1);
				jTextField1.setBounds(81, 566, 339, 29);
			}
			{
				jButton6 = new JButton();
				getContentPane().add(jButton6);
				jButton6.setText("\u53d1\u9001");
				jButton6.setBounds(432, 564, 67, 31);
				jButton6.setEnabled(false);
				//发送按钮
				jButton6.addActionListener((e)->{
					request.setOperType(RequestOperType.CHATMSG);
					request.setMsg(jTextField1.getText());
					// 发送 的 人
					String value1 = jComboBox1.getSelectedItem().toString();
					request.setTo(value1);
					// 发送 的 表情
					String value2 = jComboBox2.getSelectedItem().toString();
					request.setExpression(value2);
					if(jCheckBox1.isSelected()){
						request.setOperType(RequestOperType.QIAO);
					}
					nettyClient.getChannel().writeAndFlush(request);
				});
			}
			{
				jLabel4 = new JLabel();
				getContentPane().add(jLabel4);
//				jLabel4.setText("\u5728\u7ebf\u4eba\u6570:");
				jLabel4.setBounds(12, 620, 64, 17);
				jLabel4.setForeground(new java.awt.Color(128,128,128));
			}
			{
				jLabel5 = new JLabel();
				getContentPane().add(jLabel5);
//				jLabel5.setText("0\u4eba");
				jLabel5.setBounds(70, 620, 43, 17);
				jLabel5.setForeground(new java.awt.Color(128,128,128));
			}
			{
				jMenuBar1 = new JMenuBar();
				setJMenuBar(jMenuBar1);
				{
					jMenu1 = new JMenu();
					jMenuBar1.add(jMenu1);
					jMenu1.setText("\u64cd\u4f5c");
				}
				{
					jMenu2 = new JMenu();
					jMenuBar1.add(jMenu2);
					jMenu2.setText("\u8bbe\u7f6e");
				}
				{
					jMenu3 = new JMenu();
					jMenuBar1.add(jMenu3);
					jMenu3.setText("\u5e2e\u52a9");
				}
			}
			pack();
			this.setSize(531, 727);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
}