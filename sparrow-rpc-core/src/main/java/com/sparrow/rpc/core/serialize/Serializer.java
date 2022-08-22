package com.sparrow.rpc.core.serialize;

import java.nio.ByteBuffer;

/**
 * @author chengwei_shen
 * @date 2022/7/14 14:48
 **/
public interface Serializer<T> {
    /**
     * 获取T的长度
     *
     * @return
     */
    int getSize(T t);

    /**
     * 序列化
     *
     * @param o
     * @return
     */
    byte[] serialize(T o, ByteBuffer buffer);

    /**
     * 反序列化
     *
     * @return
     */
    T parse(ByteBuffer buffer);

    /**
     * 序列器类型
     */
    byte getType();

    Class<T> getSerializerClass();
}
