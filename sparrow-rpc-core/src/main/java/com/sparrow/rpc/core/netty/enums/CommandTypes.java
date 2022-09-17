package com.sparrow.rpc.core.netty.enums;

/**
 * @author chengweishen
 * @date 2022/7/17 11:29
 */
public enum CommandTypes {

    RPC_REQUEST("RPC_REQUEST"),
    RPC_RESPONSE("RPC_RESPONSE"),
    RPC_HEARTBEAT_REQUEST("RPC_HEARTBEAT_REQUEST"),
    RPC_HEARTBEAT_RESPONSE("RPC_HEARTBEAT_RESPONSE");

    CommandTypes(String type) {
        this.type = type;
    }

    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
