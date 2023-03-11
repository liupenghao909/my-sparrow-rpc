package com.sparrow.rpc.core;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeoutException;

/**
 * 负责创建RpcTransport目的也是为了利用SPI可拔插，
 * 客户端可以通过TransportClient创建想要的RpcTransport
 * @author chengwei_shen
 * @date 2022/7/13 16:31
 **/
public interface TransportClient extends Closeable {
    /**
     * 创建Transport通道对外使用
     *
     * @param address
     * @param connectionTimeout
     * @return
     */
    RpcTransport createTransport(InetSocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException;
}
