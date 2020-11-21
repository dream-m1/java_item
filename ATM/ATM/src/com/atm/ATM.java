package com.atm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * 该类代表ATM机 客户端
 * 设计到大量的控制台输入和输出
 *  用文件存储
 *  相当于门店
 *  每个方法对应atm一个界面
 * @state 当前页面代表操作的项目  0 存款  1 取款  2 转账
 */
public class ATM {
    Scanner sc = new Scanner(System.in);
    BankSever server = new BankSever();
    // ATM机的初始化现金
    private static Integer atmMoney = 1000000;
    // 设置密码错误次数
    int count_P = 0;

    // 首页
    public void welcomeWindow(){
        System.out.println("------欢迎使用半圆土嗨ATM系统------");
        System.out.println("请输入卡号");
        String cardno = sc.next();//得到用户输入的卡号
        //校验卡号   是否存在、是否冻结
        int error = server.checkID(cardno);
        if(errorPrint(error)){
            passwordWindow(cardno);
        }else {
            welcomeWindow();
        }
    }

    //密码输入窗口
    public void passwordWindow(String id){
        if(count_P==5){
            count_P = 0;
            // 设置冻结
            server.lockId(id);
            System.out.println("您的密码输入错误次数过多，账户已被冻结");
            welcomeWindow();
        }
        System.out.println("请输入密码:");
        String password = sc.next(); // 得到用户输入的密码
        // 校验密码
        boolean b = server.checkPassWord(password,id);
        if(b){
            count_P = 0;
            operationWindow(id);
        }else {
            count_P++;
            System.out.println("密码输入错误请重新输入");
            if(5-count_P!=0) {
                System.out.println("警告：密码输错5次账号将会被冻结，你还有" + (5 - count_P) + "次机会");
            }
            passwordWindow(id);
        }
    }

    // 主功能选择窗口
    public void operationWindow(String id){
        System.out.println("请选择对应功能的编号\n"+"  1. 查询\t2. 存款\t3. 取款\t4. 转账 5. 退卡");
        int input = sc.nextInt();
        switch (input){
            case 1:
                selectWindow(id);break;
            case 2:
                depositWindow(id);break;
            case 3:
                drawWindow(id);break;
            case 4:
                transferWindow(id);break;
            case 5:
                server.quit();
                welcomeWindow();break;
            default:
                System.out.println("没有该序号的功能，请重新输入");
                operationWindow(id);
        }
    }

    // 查询窗口
    public void selectWindow(String id){
        System.out.println("您的基本信息为：");
        System.out.println(server.select(id));
        System.out.println("请选择相应的功能：\n"+"  1. 返回\t 2. 退卡");
        int input = sc.nextInt();
        switch (input){
            case 1:
                operationWindow(id);break;
            case 2:
                server.quit();
                welcomeWindow();break;
            default:
                System.out.println("没有该序号的功能，为您跳转功能页面");
                operationWindow(id);
        }
    }

    // 临时的money
    int textMoney = 0;

    // 存款窗口
    public void depositWindow(String id){
        System.out.println("请输入存款金额：");
        int money = sc.nextInt();
        int error = server.checkDeposit(money,id);
        if(errorPrint(error)){
            System.out.println("存款成功");
            atmMoney += money;   // 把钱加给ATM机
            jixu(money,0,id);
        }else {
            depositWindow(id);
        }
    }

    // 取款窗口
    public void drawWindow(String id){
        System.out.println("请输入取款金额：");
        int money = sc.nextInt();
        // 判断ATM机金额是否够用
        if(atmMoney < money) {
            System.out.println("该ATM机现金不足，请更换一台取款。");
            drawWindow(id);
        }
        int error = server.checkDraw(money,id);
        if(errorPrint(error)){
            System.out.println("取款成功");
            atmMoney -= money;  // ATM机减去取款金额
            jixu(money,1,id);
        }else {
            drawWindow(id);
        }
    }

    // 转账卡号窗口
    public void transferWindow(String id){
        System.out.println("请输入转账卡号：");
        String tra_id = sc.next();
        if(id.equals(tra_id)){
            System.out.println("该机器不支持给自己账户转账，请重新输入");
            transferWindow(id);
        }
        if(server.checkID(tra_id)==1){
            System.out.println("转账卡号不存在，请重新输入");
            transferWindow(id);
        }else {
            moneyWindow(tra_id, id);
        }
    }

    // 转账金额窗口
    public void moneyWindow(String tra_id,String id){
        System.out.println("请输入转账金额：");
        int money = sc.nextInt();
        int error = server.checkTransfer(tra_id,id,money);
        if(errorPrint(error)){
            System.out.println("转账成功");
            isPrint(id,tra_id,money,2);
        }else {
            moneyWindow(tra_id,id);
        }
    }

    // 打印凭条窗口
    // state 代表存入取出的标识
    public void isPrint(String id,String str_id,int money,int state){
        System.out.println("是否打印凭条\n  1. 是\t 2. 否");
        int input = sc.nextInt();
        if(input==2) {
            operationWindow(id);
            return;   //   ---------------
        }
        String s ="";
        // 排除转账的情况，转账时不输出
        if(state!=2){
           s +='\n'+"本次"+(state==0?"存入：":"取出：")+money+"元\n";
        }else {
            s +="\n转到卡号："+str_id+"\n转账金额："+money+"\n转账";
        }
        s += server.select(id)+"\n";
        s += "打印时间："+new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(new Date())+'\n';
        server.pingtiao(s);
        System.out.println(s);
        operationWindow(id);
    }

    // 金钱继续交易窗口
    public void jixu(int money,int state,String id){
        System.out.println("是否继续交易 1.是  2.否");
        int input = sc.nextInt();
        textMoney += money;   //  把之前转的钱也算上
        if(input==2){
            isPrint(id,null,textMoney,state);
            return;   //  ---------
        }
        if(state==0){
            depositWindow(id);
        }else {
            drawWindow(id);
        }
    }

    // 错误输出窗口
    // 不建议所有的错误都单独标注，因为分支判断也会耽误一定的效率
    public boolean errorPrint(int error){
        switch (error){
            case 0:
                return true;
            case 3:
                System.out.println("输入金额必须是100的整数，请从新输入");
                break;
            case 4:
                System.out.println("输入金额不能超过5000，请重新输入");
                break;
            case 5:
                System.out.println("账户余额不足，请重新输入");
                break;
            case 1:
                System.out.println("转账卡号不存在，请重新输入");
                break;
            case 2:
                System.out.println("该账号已被冻结");
                break;
        }
        return false;
    }
}
