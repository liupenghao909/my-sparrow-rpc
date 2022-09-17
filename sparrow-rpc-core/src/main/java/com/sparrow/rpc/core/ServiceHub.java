package com.sparrow.rpc.core;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务实例注册中心
 *
 * @author chengweishen
 * @date 2022/7/17 21:31
 */
public class ServiceHub {
    private Map<String, Object> serviceMp = new ConcurrentHashMap<>();
    private static ServiceHub INSTANCE;

    public static ServiceHub getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new ServiceHub();
        }
        return INSTANCE;
    }

    public Object getService(String serviceSing) {
        return serviceMp.get(serviceSing);
    }

    public void addService(String serviceSign, Object service) {
        serviceMp.putIfAbsent(serviceSign, service);
    }

    public Object removeService(String serviceSign) {
        return serviceMp.remove(serviceSign);
    }
}
