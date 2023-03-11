package com.sparrow.rpc.core.netty.dto;

/**
 * 传输的RPC请求
 *
 * @author chengwei_shen
 * @date 2022/7/14 20:39
 **/
public class RpcCommand {
    RpcHeader header;
    /**
     * 通过自定义序列化方式序列化好的RpcRequest信息
     */
    byte[] data;

    public RpcHeader getHeader() {
        return header;
    }

    public void setHeader(RpcHeader header) {
        this.header = header;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
