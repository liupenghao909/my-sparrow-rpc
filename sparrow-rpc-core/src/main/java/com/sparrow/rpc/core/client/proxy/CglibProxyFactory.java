package com.sparrow.rpc.core.client.proxy;

import com.sparrow.rpc.api.ServiceMetaInfo;
import com.sparrow.rpc.core.RpcTransport;
import com.sparrow.rpc.core.client.ProxyFactory;

/**
 * 代理工厂类
 */
public class CglibProxyFactory implements ProxyFactory {
    RpcTransport rpcTransport;

    @Override
    public <T> T createProxy(Class<T> clazz, ServiceMetaInfo metaInfo) {
        CglibRpcProxy proxy = new CglibRpcProxy(clazz, metaInfo, rpcTransport);
        return (T) proxy.getProxy();
    }

    public void setRpcTransport(RpcTransport rpcTransport) {
        this.rpcTransport = rpcTransport;
    }
}
