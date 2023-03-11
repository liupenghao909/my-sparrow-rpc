package com.sparrow.rpc.core.netty.enums;

/**
 * @author chengweishen
 * @date 2022/7/17 11:29
 */
public enum CommandTypes {

    RPC_REQUEST("RPC_REQUEST","RPC请求"),
    RPC_RESPONSE("RPC_RESPONSE", "RPC响应"),
    RPC_HEARTBEAT_REQUEST("RPC_HEARTBEAT_REQUEST", "RPC心跳检测请求"),
    RPC_HEARTBEAT_RESPONSE("RPC_HEARTBEAT_RESPONSE", "RPC心跳检测响应");

    CommandTypes(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    /**
     * 类型
     */
    String type;
    /**
     * 类型描述
     */
    String desc;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
