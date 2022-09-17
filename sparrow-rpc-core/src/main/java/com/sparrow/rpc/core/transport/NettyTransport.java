package com.sparrow.rpc.core.transport;

import com.sparrow.rpc.core.RpcTransport;
import com.sparrow.rpc.core.netty.dto.RpcCommand;
import com.sparrow.rpc.core.netty.dto.RpcResponse;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author chengwei_shen
 * @date 2022/7/13 20:21
 **/
@Slf4j
public class NettyTransport implements RpcTransport {
    private Channel channel;
    private PendingRequests pendingRequests;

    public NettyTransport(Channel channel) {
        this.channel = channel;
        this.pendingRequests = PendingRequests.getInstance();
    }

    @Override
    public CompletableFuture<RpcResponse> send(RpcCommand request) {
        if (!channel.isActive()) {
            throw new IllegalStateException("Unhealthy channel state");
        }
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        try {
            pendingRequests.put(request.getHeader().getTraceId(), future);
            channel.writeAndFlush(request).addListener(channelListenFuture -> {
                if (!channelListenFuture.isSuccess()) {
                    log.warn("Send request error");
                    future.completeExceptionally(channelListenFuture.cause());
                    channel.close();
                }
            });
        } catch (Exception e) {
            log.warn("Send message error");
            future.completeExceptionally(e);
            pendingRequests.remove(request.getHeader().getTraceId());
        }
        return future;
    }
}
