package com.sparrow.rpc.core.netty.handler;

import com.sparrow.rpc.core.netty.dto.RpcCommand;
import com.sparrow.rpc.core.netty.dto.RpcHeader;
import com.sparrow.rpc.core.netty.dto.RpcResponse;
import com.sparrow.rpc.core.netty.enums.CommandTypes;
import com.sparrow.rpc.core.transport.PendingRequests;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author chengwei_shen
 * @date 2022/7/18 19:12
 **/
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        String type = rpcResponse.getHeader().getType();
        // 如果是心跳检测消息
        if (CommandTypes.RPC_HEARTBEAT_RESPONSE.getType().equals(type)) {
            log.info("HeartBeat [{}]", rpcResponse.getData());
        } else if (CommandTypes.RPC_RESPONSE.getType().equals(type)) {
            // 从PendingRequests中根据traceID获取尚未收到响应的请求
            CompletableFuture<RpcResponse> rspFuture = PendingRequests.getInstance().remove(rpcResponse.getHeader().getTraceId());
            //通过complete将reponse返回给对应future的get阻塞线程
            rspFuture.complete(rpcResponse);
        }
    }

    /**
     * 客户端心跳机制定时上报
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 如果是写空闲往channel中发送心跳上报的请求
            if (event.state() == IdleState.WRITER_IDLE) {
                log.info("Idle send to [{}]", ctx.channel().remoteAddress());
                RpcCommand rpcCommand = new RpcCommand();
                RpcHeader header = new RpcHeader();
                header.setType(CommandTypes.RPC_HEARTBEAT_REQUEST.getType());
                header.setVersion("v1.0");
                header.setTraceId(UUID.randomUUID().toString());
                rpcCommand.setHeader(header);
                rpcCommand.setData("Ping".getBytes(StandardCharsets.UTF_8));
                ctx.channel().writeAndFlush(rpcCommand).addListener(channelListenFuture -> {
                    if (!channelListenFuture.isSuccess()) {
                        log.warn("Send request error");
                    }
                });
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * Called when an exception occurs in processing a client message
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
