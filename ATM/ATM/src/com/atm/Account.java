package com.atm;

public class Account {
    private String id;
    private String password;
    private Integer money;
    private Boolean state;

    public Account(String id, String password, Integer money, Boolean state) {
        this.id = id;
        this.password = password;
        this.money = money;
        this.state = state;
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

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return  "卡号：" + id +
                "\n余额为：" + money + " RMB" +
                "\n状态：" + ( state ?"未冻结":"已冻结" );
    }
}
