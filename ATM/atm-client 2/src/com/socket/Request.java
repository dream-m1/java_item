package com.socket;

import java.io.Serializable;

/**
 * 接收请求的对象
 */
public class Request implements Serializable {
    private String id;
    private String password;
    private String money;
    private String state;
    private String userId;
    private String stateCorde;

    public String getStateCorde() {
        return stateCorde;
    }

    public void setStateCorde(String stateCorde) {
        this.stateCorde = stateCorde;
    }

    public Request(String id, String password, String money, String state, String userId, String stateCorde) {
        this.id = id;
        this.password = password;
        this.money = money;
        this.state = state;
        this.userId = userId;
        this.stateCorde = stateCorde;
    }

    public Request() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", money='" + money + '\'' +
                ", state='" + state + '\'' +
                ", userId='" + userId + '\'' +
                ", stateCorde='" + stateCorde + '\'' +
                '}';
    }
}
