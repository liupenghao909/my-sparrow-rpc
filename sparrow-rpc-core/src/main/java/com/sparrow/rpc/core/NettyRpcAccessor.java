package com.sparrow.rpc.core;

import com.sparrow.rpc.api.NameService;
import com.sparrow.rpc.api.RpcAccessor;
import com.sparrow.rpc.api.ServiceMetaInfo;
import com.sparrow.rpc.api.spi.SpiSupport;
import com.sparrow.rpc.core.client.ProxyFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

/**
 * RPC访问类
 * Accessor的主要功能在于对外提供统一的API去访问RPC核心相关的方法，控制权限收口，
 * 然后像SPI的配置机制也可以统一放在该模块下使用
 *
 * 为了将NameServer以及其他Rpc内部功能性方法与具体使用者解耦，我们创建一个RpcAccessor接口，
 * 把Rpc服务相关的方法收敛到一起
 *
 * @author chengwei_shen
 * @date 2022/7/13 15:59
 **/
@Slf4j
public class NettyRpcAccessor implements RpcAccessor {
    private final int port = 8888;

    @Override
    public <T> T getRemoteService(ServiceMetaInfo metaInfo, Class<T> clazz) throws InterruptedException, TimeoutException {
        //查询服务地址
        NameService nameService = SpiSupport.load(NameService.class);
        String serviceSign = metaInfo.getServiceSign();
        URI uri = nameService.seekService(serviceSign);
        //与服务创建Transport
        TransportClient transportClient = SpiSupport.load(TransportClient.class);
        InetSocketAddress address = new InetSocketAddress(uri.getHost(), uri.getPort());
        RpcTransport transport = transportClient.createTransport(address, 3000);
        //创建代理类
        ProxyFactory proxyFactory = SpiSupport.load(ProxyFactory.class);
        proxyFactory.setRpcTransport(transport);
        return proxyFactory.createProxy(clazz, metaInfo);
    }

    /**
     * 服务注册
     * @param serviceSign 服务签名
     * @param service     服务实例
     * @param clazz       服务接口类
     * @param <T>
     * @return
     */
    @Override
    public <T> URI addRpcService(String serviceSign, T service, Class<T> clazz) {
        //服务实例注册
        ServiceHub.getInstance().addService(serviceSign, service);
        //服务URI注册 服务类
        NameService nameService = SpiSupport.load(NameService.class);
        // 获取本地uri
        URI localUri = getLocalUri();
        if (Objects.isNull(localUri)) {
            throw new IllegalStateException("Get local uri fail");
        }
        // 服务URI注册
        nameService.registerServer(serviceSign, localUri);
        return null;
    }

    @Override
    public Closeable start() throws Exception {
        TransportServer server = SpiSupport.load(TransportServer.class);
        server.start();
        log.info("---------Start Successfully--------");
        return server;
    }

    @Override
    public void close() {
        TransportServer server = SpiSupport.load(TransportServer.class);
        server.close();
    }

    /**
     * 获取本地uri
     *
     * @return 本地uri
     */
    private URI getLocalUri() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String hostAddress = "localhost";
            return URI.create("rpc://" + hostAddress + ":" + port);
        } catch (Exception e) {
            log.warn("Get local uri fail", e);
        }
        return null;
    }
}
