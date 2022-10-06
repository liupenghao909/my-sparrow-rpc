package com.sparrow.rpc.core.client;

import com.sparrow.rpc.api.ServiceMetaInfo;
import com.sparrow.rpc.core.RpcTransport;

public interface ProxyFactory {
    <T> T createProxy(Class<T> clazz, ServiceMetaInfo metaInfo);

    void setRpcTransport(RpcTransport rpcTransport);
}
