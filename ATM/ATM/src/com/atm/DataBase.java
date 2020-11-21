package com.atm;

import java.io.*;
import java.util.*;

/**
 * 代表数据的基本操作、插入的基本操作
 * 不涉及业务逻辑
 * 相当于采购部门
 * 该类用于操作accounts文件
 */
public class DataBase {
    private Map<String,Account> map = new HashMap<>();

    // 从问价中获取所有用户信息
    public Map getAcccounts(){
        try(BufferedReader br = new BufferedReader(new FileReader("account.txt"))){
            String s;
            while ((s=br.readLine())!=null){
                String[] str = s.split(",");
                map.put(str[0],new Account(str[0],str[1],Integer.valueOf(str[2]),
                        (Integer.parseInt(str[3])==0?true:false)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    // 在用户结束任务时写入文件
    public void setAccounts(Map map){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("account.txt"))){
            Collection<Account> values  = map.values();
            for (Account account:values) {
                bw.write(""+account.getId()+','+account.getPassword()+','+
                        account.getMoney()+','+(account.getState()==true?0:1));
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将凭条内容写进文件
    public void setPing(String str){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("pingtiao.txt",true))){
            bw.write(str);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}