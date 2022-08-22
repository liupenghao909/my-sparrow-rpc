package com.sparrow.rpc.core.netty.dto;

/**
 * 封装客户端RPC请求信息
 *
 * @author chengwei_shen
 * @date 2022/7/13 20:06
 **/
public class RpcRequest {
    private String nameSpace;
    private String serviceName;
    private String methodName;
    /**
     * 方法的参数，这边直接用byte接收，这样避免在序列化时重复序列化这部分数据
     */
    private byte[] parameters;

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public byte[] getParameters() {
        return parameters;
    }

    public void setParameters(byte[] parameters) {
        this.parameters = parameters;
    }

    /**
     * 构造服务签名
     *
     * @return
     */
    public String buildServiceSign() {
        return nameSpace + ":" + serviceName;
    }
}
