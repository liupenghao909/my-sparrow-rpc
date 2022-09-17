package com.sparrow.rpc.core;

import com.sparrow.rpc.core.netty.dto.RpcCommand;
import com.sparrow.rpc.core.netty.dto.RpcResponse;

import java.util.concurrent.CompletableFuture;

/**
 * @author chengwei_shen
 * @date 2022/7/13 17:18
 **/
public interface RpcTransport {
    /**
     * 发送请求
     *
     * @param request 具体请求体
     * @return response
     */
    CompletableFuture<RpcResponse> send(RpcCommand request);
}
