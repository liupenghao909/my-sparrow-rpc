package com.sparrow.rpc.core.netty.client;

import com.sparrow.rpc.api.spi.Singleton;
import com.sparrow.rpc.core.RpcTransport;
import com.sparrow.rpc.core.TransportClient;
import com.sparrow.rpc.core.netty.decoder.RpcCommandEncoder;
import com.sparrow.rpc.core.netty.decoder.RpcResponseDecoder;
import com.sparrow.rpc.core.netty.handler.RpcResponseHandler;
import com.sparrow.rpc.core.transport.NettyTransport;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Netty客户端
 * @author chengwei_shen
 * @date 2022/7/14 10:50
 **/
@Singleton
@Slf4j
public class NettyClient implements TransportClient {
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;

    public NettyClient() {
        this.eventLoopGroup = buildEventGroup();
        this.bootstrap = buildBoostrap(this.eventLoopGroup, buildChannelHandler());
    }

    @Override
    public RpcTransport createTransport(InetSocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        return new NettyTransport(createChannel(address, connectionTimeout));
    }

    /**
     * 创建channel
     * @param address 地址
     * @param connectionTimeout 连接超时时间
     * @return
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public Channel createChannel(InetSocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException {
        if (Objects.isNull(address)) {
            throw new IllegalArgumentException("Address can not be null");
        }
        // 根据地址从channel注册中心获取channel，重复利用
        Channel channel = ChannelRegistry.getInstance().get(address);
        if (Objects.nonNull(channel)) {
            return channel;
        }
        /*
        ChannelFuture
            Netty中对于Channel的IO都是异步的，都会返回ChannelFuture对象并可以通过addListener方法给
            这个IO操作注册一个ChannelFutureListener用于事件IO回调
         */
        ChannelFuture channelFuture = bootstrap.connect(address);
        if (!channelFuture.await(connectionTimeout)) {
            throw new TimeoutException();
        }
        channel = channelFuture.channel();
        if (Objects.isNull(channel) || !channel.isActive()) {
            throw new IllegalStateException("Channel unHealthy");
        }
        ChannelRegistry.getInstance().put(address, channel);
        log.info("NettyClient connect to [{}] successfully", address);
        return channel;
    }

    private Bootstrap buildBoostrap(EventLoopGroup ioEventGroup, ChannelHandler channelHandler) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .group(ioEventGroup)
                .handler(channelHandler)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        return bootstrap;
    }

    private ChannelHandler buildChannelHandler() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline()
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new RpcResponseDecoder())
                        .addLast(new RpcCommandEncoder())
                        .addLast(new RpcResponseHandler());
            }
        };
    }

    private EventLoopGroup buildEventGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
    }

    @Override
    public void close() throws IOException {
        ChannelRegistry.getInstance().removeAll();
        eventLoopGroup.shutdownGracefully();
    }
}
