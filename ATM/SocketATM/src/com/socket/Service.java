package com.socket;

import java.util.Map;

/**
 * 逻辑处理层
 */
public class Service {
    private JXML dataBase = new JXML();
    // 建立缓存,把文件中的数据加载到内存中
    private Map<String,Accunts> data = dataBase.getXML();

    // 校验卡号，是否被冻结
    // z 标志位，true 是自己，Flase是别的用户
    public ResultCode checkID(Request request,boolean z) {
        String id;
        if(z){
            id = request.getId();
        }else {
            id = request.getUserId();
        }
        if (!data.containsKey(id)) {
            return ResultCode.ERROR_NOTID;
        }
        if (!data.get(id).getState()) {
            return ResultCode.ERROR_STATE;
        }
        return ResultCode.SUCCESS;
    }

    // 校验密码
    public ResultCode checkPassWord(Request request){
        return data.get(request.getId()).getPassword().equals(request.getPassword()) ? ResultCode.SUCCESS : ResultCode.ERROR_PASSWORD ;
    }

    // 将账号冻结
    public void lockId(Request request){
        data.get(request.getId()).setState(false);
        dataBase.modify("state","1",request.getId());
    }

    // 存入金额校验
    public ResultCode checkDeposit(Request request,boolean z){
        String id;
        if(z){
            id = request.getId();
        }else {
            id = request.getUserId();
        }
        Accunts text = data.get(id);
        Integer cunMoney = text.getMoney()+Integer.parseInt(request.getMoney());
        text.setMoney(cunMoney);
        // 写入文件
        dataBase.modify("money",cunMoney+"",id);
        return ResultCode.SUCCESS;
    }

    // 取款金额校验
    public ResultCode checkDraw(Request request){
        Accunts text = data.get(request.getId());
        if(text.getMoney()>Integer.parseInt(request.getMoney())){
            Integer quMoney = text.getMoney()-Integer.parseInt(request.getMoney());
            text.setMoney(quMoney);
            // 写入文件
            dataBase.modify("money",quMoney+"",request.getId());
        }else {
            return ResultCode.ERROR_LESSMONEY;  // 账户余额不够
        }
        return ResultCode.SUCCESS;
    }

    // 转账
    public ResultCode checkTransfer(Request request){
        // 判断对方卡号
        ResultCode error = checkID(request,false);
        if(!(error==ResultCode.SUCCESS)){
            return error;
        }
        // 自己取款
        error = checkDraw(request);
        if(error==ResultCode.SUCCESS){
            // 被转用户存款
            checkDeposit(request,false);
        }else {
            return error;
        }
        return ResultCode.SUCCESS;
    }

    // 查询余额
    public Accunts balance(Request request){
        return data.get(request.getId());
    }

}
