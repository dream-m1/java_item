package com.socket;

public enum ResultCode {
    SUCCESS,// 成功
    ERROR_PASSWORD,//密码错误
    ERROR_STATE,//冻结状态错误
    ERROR_LESSMONEY, //余额不足
    ERROR_NOTID //没有卡号
}
