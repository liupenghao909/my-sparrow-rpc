package com.sparrow.rpc.api.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * SPI工具类，提供接口发现服务功能
 *
 * @author chengwei_shen
 * @date 2022/7/13 15:20
 **/
public class SpiSupport {
    private final static Map<String, Object> singletonServices = new HashMap<>();

    public synchronized static <S> S load(Class<S> service) {
        return StreamSupport.
                stream(ServiceLoader.load(service).spliterator(), false)
                .map(SpiSupport::singletonFilter)
                .findFirst().orElseThrow(RuntimeException::new);
    }

    public synchronized static <S> Collection<S> loadAll(Class<S> service) {
        return StreamSupport.
                stream(ServiceLoader.load(service).spliterator(), false)
                .map(SpiSupport::singletonFilter).collect(Collectors.toList());
    }

    private static <S> S singletonFilter(S service) {
        if (service.getClass().isAnnotationPresent(Singleton.class)) {
            String className = service.getClass().getCanonicalName();
            Object singletonInstance = singletonServices.putIfAbsent(className, service);
            return singletonInstance == null ? service : (S) singletonInstance;
        } else {
            return service;
        }
    }
}
