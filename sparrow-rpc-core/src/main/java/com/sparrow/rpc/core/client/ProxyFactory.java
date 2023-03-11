package com.sparrow.rpc.core.client;

import com.sparrow.rpc.api.ServiceMetaInfo;
import com.sparrow.rpc.core.RpcTransport;

/**
 * 代理工厂
 */
public interface ProxyFactory {
    /**
     * 创建代理类
     * @param clazz
     * @param metaInfo
     * @param <T>
     * @return
     */
    <T> T createProxy(Class<T> clazz, ServiceMetaInfo metaInfo);

    /**
     * 设置rpc的传输类，也就是我们在core实现的NettyTransport
     * @param rpcTransport
     */
    void setRpcTransport(RpcTransport rpcTransport);
}
