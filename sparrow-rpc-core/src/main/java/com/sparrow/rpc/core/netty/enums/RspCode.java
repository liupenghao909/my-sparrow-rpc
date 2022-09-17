package com.sparrow.rpc.core.netty.enums;

/**
 * 错误枚举类
 *
 * @author chengwei_shen
 * @date 2022/7/13 20:12
 **/
public enum RspCode {
    SUCCESS(1, "SUCCESS"),
    UNKNOWN_SERVICE(-1, "Unknown Service"),
    ERROR(-2, "ERROR");


    RspCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    int code;
    String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
