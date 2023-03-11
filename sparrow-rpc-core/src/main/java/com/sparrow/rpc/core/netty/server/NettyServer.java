package com.sparrow.rpc.core.netty.server;

import com.sparrow.rpc.core.TransportServer;
import com.sparrow.rpc.core.netty.decoder.RpcCommandDecoder;
import com.sparrow.rpc.core.netty.decoder.RpcResponseEncoder;
import com.sparrow.rpc.core.netty.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Netty服务端
 * 实现网络传输的实现类，用Netty实现
 * @author chengwei_shen
 * @date 2022/7/18 17:47
 **/
public class NettyServer implements TransportServer {
    private final int port = 8888;
    private EventLoopGroup acceptEventGroup;
    private EventLoopGroup workerEventGroup;
    private Channel channel;

    @Override
    public void start() throws Exception {
        //专门处理连接的group，相当于多路复用的监听线程
        acceptEventGroup = buildEventGroup();
        //处理io事件的worker
        workerEventGroup = buildEventGroup();
        ChannelHandler channelHandler = buildChannelHandler();
        ServerBootstrap serverBootstrap = buildBoostrap(acceptEventGroup, workerEventGroup, channelHandler);
        //绑定host和端口
        bindChannel(serverBootstrap);
    }

    /**
     * 最后把我们监听的端口和bootstrap绑定到一起生成对外的这个channel就可以进行对外连接了
     * @param serverBootstrap
     * @throws InterruptedException
     */
    private void bindChannel(ServerBootstrap serverBootstrap) throws InterruptedException {
        this.channel = serverBootstrap.bind("localhost", port)
                .sync()
                .channel();
    }

    /**
     * 构建Bootstrap，Bootstrap可以理解为Netty的控制中心，它把在此之前创建的EventLoopGroup、ChannelHandler都绑定到一起。
     * 这里涉及到Netty对连接的配置，包括对TCP的配置，通过childOption进行配置
     *
     * bootstrap 创建后相当于IO模型、线程模型都已经搭建完成
     * @param acceptLoopGroup
     * @param workerEventGroup
     * @param channelHandler
     * @return
     */
    private ServerBootstrap buildBoostrap(EventLoopGroup acceptLoopGroup, EventLoopGroup workerEventGroup, ChannelHandler channelHandler) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .group(acceptLoopGroup, workerEventGroup)
                .childHandler(channelHandler)
                //是否开启Nagle算法，该算法会缓存网络数据包，相当于批处理（适合高流量，不适合实时性高场景）
                .childOption(ChannelOption.TCP_NODELAY, true)
                //是否连接保活，可以复用连接，减少建立连接开销
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        return serverBootstrap;
    }

    /**
     * 构建ChannelHandler处理channel上的每一个IO事件，对应的是ChannelPipeline将重要的几个组件串在一起
     * IdleStateHandler 用于心跳检测，前三个参数分别表示读、写、读写事件进行检测
     *     IdleStateHandler的机制是通过传入的三个参数，分别对读事件、写事件或者读写事件进行检测，如果配置了readerIdleTime是3秒。
     *     那么当前channel如果读事件没出现超过3秒，就会调用userEventTrigger方法，所以IdleStateHandler配置完了后就要在我们
     *     自定义的Handler中重写userEventTrigger方法
     *
     * RpcCommandDecoder 由于Server端是接收请求，所以需要一个对RpcCommand的反序列化步骤
     * RpcResponseEncoder 对返回的RpcResponse进行序列化
     * RpcRequestHandler 对每个接收到的请求进行处理的步骤
     *      核心组件，主要处理接收到请求后要做的事情，在写代码之前想清楚，我们拿到请求后要做的事情主要分为三步
     *      1.根据服务签名找到对应的服务
     *      2.反序列化请求参数，调用服务对应方法
     *      3.拿到结果组装成RpcResponse返回给客户端
     *      这三步逻辑都在invokeService方法中，这里还区别了一下心跳请求。代码中涉及到一个ServiceHub
     *      这个在之后介绍NameServer与服务注册的时候会讲，可以直接拷贝这个类到目录下就行
     *
     * pipeline的添加顺序和执行顺序
     *  读入数据，需要解码数据，执行顺序和注册顺序一致， 他们之间通过 ctx.fireChannelRead(msg);进行传递
     *  解码完成，逻辑处理，进行数据发送 通过 ctx.writeAndFlush()就完成从in -->out的转换
     *  out的执行顺是和注册顺序相反的，out间的传递通过ctx.writeAndFlush();函数进行传递
     * @return
     */
    private ChannelHandler buildChannelHandler() {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                channel.pipeline()
                        .addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS))
                        .addLast(new RpcCommandDecoder())
                        .addLast(new RpcResponseEncoder())
                        .addLast(new RpcRequestHandler());
            }
        };
    }

    private EventLoopGroup buildEventGroup() {
        // 由于Epoll并不是所有操作系统上都支持，所以动态决定使用select还是epoll作为selector
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
    }

    @Override
    public void close() {
        if (Objects.nonNull(acceptEventGroup)) {
            acceptEventGroup.shutdownGracefully();
        }
        if (Objects.nonNull(workerEventGroup)) {
            workerEventGroup.shutdownGracefully();
        }
        if (Objects.nonNull(channel)) {
            channel.close();
        }
    }
}
