package com.sparrow.rpc.core.netty.dto;

import com.sparrow.rpc.core.netty.enums.RspCode;

import java.io.Serializable;

/**
 * @author chengwei_shen
 * @date 2022/7/13 20:06
 **/
public class RpcResponse implements Serializable {
    RpcHeader header;
    int code;
    String errorMsg;
    Object data;

    public RpcHeader getHeader() {
        return header;
    }

    public void setHeader(RpcHeader header) {
        this.header = header;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static RpcResponse buildSuccessResponse() {
        RpcResponse response = new RpcResponse();
        response.setCode(RspCode.SUCCESS.getCode());
        return response;
    }
}
