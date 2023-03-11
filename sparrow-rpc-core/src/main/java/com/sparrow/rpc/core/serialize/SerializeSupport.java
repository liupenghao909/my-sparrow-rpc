package com.sparrow.rpc.core.serialize;


import com.sparrow.rpc.api.spi.SpiSupport;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 为了序列化有一个统一出口，我们实现一个SerializeSupport支持类，对外提供一致序列化方式
 * @author chengwei_shen
 * @date 2022/7/15 14:39
 **/
public class SerializeSupport {
    /**
     * 序列化器类型和序列化器的映射关系
     */
    private static Map<Byte, Serializer<?>> typeSerializerMap = new ConcurrentHashMap<>();
    /**
     * 被序列化对象类型和序列化器的映射关系
     */
    private static Map<Class<?>, Serializer<?>> classSerializerMap = new ConcurrentHashMap<>();

    static {
        //加载所有序列器
        Collection<Serializer> serializers = SpiSupport.loadAll(Serializer.class);
        serializers.forEach(serializer -> {
            typeSerializerMap.put(serializer.getType(), serializer);
            classSerializerMap.put(serializer.getSerializerClass(), serializer);
        });
    }

    /**
     * 默认序列化
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T t) {
        Serializer<T> serializer = (Serializer<T>) classSerializerMap.get(t.getClass());
        if (Objects.isNull(serializer)) {
            throw new IllegalArgumentException(String.format("Cannot find correct serializer for class:%s", t.getClass()));
        }
        // +1 目的：在序列后的结果 字节数组的第一位放一个Byte类型的值表示序列化类型
        ByteBuffer buffer = ByteBuffer.allocate(serializer.getSize(t) + 1);
        //先放上类型
        buffer.put(serializer.getType());
        // 返回序列化后的内容
        return serializer.serialize(t, buffer);
    }

    /**
     * 指定type序列化
     *
     * @param t
     * @param type
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T t, Byte type) {
        Serializer<T> serializer = (Serializer<T>) typeSerializerMap.get(type);
        if (Objects.isNull(serializer)) {
            throw new IllegalArgumentException(String.format("Cannot find correct serializer for class:%s", t.getClass()));
        }
        ByteBuffer buffer = ByteBuffer.allocate(serializer.getSize(t) + 1);
        //先放上类型
        buffer.put(serializer.getType());
        return serializer.serialize(t, buffer);
    }

    /**
     * 默认反序列化方式
     * @param bytes
     * @return
     * @param <E>
     */
    public static <E> E parse(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        //先取出序列化类型
        byte type = buffer.get();
        // 根据序列化类型找到序列化器
        Serializer<?> serializer = typeSerializerMap.get(type);
        if (Objects.isNull(serializer)) {
            throw new IllegalArgumentException(String.format("Unknown type:%s", type));
        }
        Object rs = serializer.parse(buffer);
        return (E) rs;
    }

    /**
     * 指定type解析
     *
     * @param bytes
     * @param type
     * @param <E>
     * @return
     */
    public static <E> E parse(byte[] bytes, Byte type) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Serializer<?> serializer = typeSerializerMap.get(type);
        if (Objects.isNull(serializer)) {
            throw new IllegalArgumentException(String.format("Unknown type:%s", type));
        }
        Object rs = serializer.parse(buffer);
        return (E) rs;
    }
}
