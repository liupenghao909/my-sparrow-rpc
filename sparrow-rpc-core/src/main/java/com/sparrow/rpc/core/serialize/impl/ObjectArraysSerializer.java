package com.sparrow.rpc.core.serialize.impl;

import com.sparrow.rpc.core.serialize.SerializerType;

/**
 * @author chengwei_shen
 * @date 2022/7/11 15:16
 **/
public class ObjectArraysSerializer extends ObjectSerializer {
    @Override
    public byte getType() {
        return SerializerType.TYPE_OBJECT_ARRAY.getType();
    }

    @Override
    public Class getSerializerClass() {
        return new Object[]{}.getClass();
    }
}
