package com.sparrow.rpc.core.transport;

import com.sparrow.rpc.core.netty.dto.RpcResponse;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于存放尚未收到响应的请求
 * 单例
 *
 * @author chengwei_shen
 * @date 2022/7/13 20:36
 **/
public class PendingRequests {
    private static final Map<String, CompletableFuture<RpcResponse>> PENDING_REQUESTS = new ConcurrentHashMap<>();
    private static PendingRequests INSTANCE;

    public static PendingRequests getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new PendingRequests();
            return INSTANCE;
        }
        return INSTANCE;
    }

    public void put(String traceId, CompletableFuture<RpcResponse> future) {
        PENDING_REQUESTS.put(traceId, future);
    }

    public CompletableFuture<RpcResponse> remove(String traceId) {
        CompletableFuture<RpcResponse> future = PENDING_REQUESTS.remove(traceId);
        return future;
    }
}
