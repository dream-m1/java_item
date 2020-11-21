package club.banyuan.client;

public enum ResultCode {
    ERROR_NOTID, //没有卡号
    ERROR_STATE,//冻结状态错误
    ERROR_PASSWORD,//密码错误
    ERROR_LESSMONEY, //余额不足
    SUCCESS// 成功
}

