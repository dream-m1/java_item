package com.socket;

import java.io.Serializable;

/**
 * 返回请求对应响应的对象信息
 */
public class Response implements Serializable {
    private String id;
    private String money;
    private String state;
    private ResultCode resultCode;

    public Response(String id, String money, String state, ResultCode resultCode) {
        this.id = id;
        this.money = money;
        this.state = state;
        this.resultCode = resultCode;
    }

    public Response(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id='" + id + '\'' +
                ", money='" + money + '\'' +
                ", state='" + state + '\'' +
                ", resultCode=" + resultCode +
                '}';
    }
}
