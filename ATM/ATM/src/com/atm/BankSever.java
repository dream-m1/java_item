package com.atm;

import java.util.*;

/**
 * 该列代表远程银行服务器
 * 处理校验、查询等等业务逻辑
 * 数据的加工
 * 相当于生产部门
 */
public class BankSever {
    private DataBase dataBase = new DataBase();
    // 建立缓存,把文件中的数据加载到内存中
    private Map<String,Account> data = dataBase.getAcccounts();
//    private static String ID = null;

    // 校验卡号，是否被冻结
    public int checkID(String id){
        if(!data.containsKey(id)){
            return 1;
        }
        if(!data.get(id).getState()){
            return 2;
        }
//        ID = id;
        return 0;
    }

    // 校验密码
    public boolean checkPassWord(String password,String id){
        return data.get(id).getPassword().equals(password) ? true : false ;
    }

    // 将账号冻结
    public void lockId(String id){
        data.get(id).setState(false);
    }

    // 获取客户基本信息
    public String select(String id){
        return data.get(id).toString();
    }

    // 存入金额校验
    public int checkDeposit(int money,String id){
        // 判断输入格式
        int format = checkMoney(money);
        if(!(format==0)){
            return format;
        }
        Account text = data.get(id);
        text.setMoney(text.getMoney()+money);
        // 写入文件
//        dataBase.setAccounts(data);
        return 0;
    }

    // 取款金额校验
    public int checkDraw(int money,String id){
        // 判断输入格式
        int format = checkMoney(money);
        if(!(format==0)){
            return format;
        }
        Account text = data.get(id);
        if(text.getMoney()>money){
            text.setMoney(text.getMoney()-money);
            // 写入文件
//            dataBase.setAccounts(data);
        }else {
            return 5;  // 账户余额不够
        }
        return 0;
    }

    // 转账
    public int checkTransfer(String tra_id,String id,int money){
        int error = checkDraw(money,id);
        if(error==0){
            Account text = data.get(tra_id);
            text.setMoney(text.getMoney()+money);
            // 写入文件
//            dataBase.setAccounts(data);
        }else {
            return error;
        }
        return 0;
    }

    // 生成凭条文件
    public void pingtiao(String str){
        dataBase.setPing(str);
    }

    // 退卡
    public void quit(){
        dataBase.setAccounts(data);
    }

    // 判断金额输入是否违规
    public int checkMoney(int money){
        // 金额是100的倍数
        if(!((money%100)==0&&money>0)){
            return 3;
        }
        if(money>5000){
            return 4;
        }
        return 0;
    }

//    public void dinshi(Date date){
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    SimpleDateFormat df = new SimpleDateFormat("ss");
//                    String open_data = df.format(date);
//                    String next_data = df.format(new Date());
//                    if(Integer.valueOf(next_data)==Integer.valueOf(open_data)+30){
//                        System.out.println("操作已过时");
//                    }
//                    System.out.println("操作已过时");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        },0, 1000*30);
//    }

}
