package com.sparrow.rpc.core.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.sparrow.rpc.core.serialize.SerializeException;
import com.sparrow.rpc.core.serialize.Serializer;
import com.sparrow.rpc.core.serialize.SerializerType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * @author chengwei_shen
 * @date 2022/7/19 17:10
 **/
public class HessianSerializer implements Serializer<Object> {

    @Override
    public int getSize(Object o) {
        return 0;
    }

    @Override
    public byte[] serialize(Object o, ByteBuffer buffer) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Hessian2Output out = new Hessian2Output(outputStream);
            out.writeObject(o);
            out.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new SerializeException("serializer error");
        }
    }

    @Override
    public Object parse(ByteBuffer buffer) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.array());
            Hessian2Input input = new Hessian2Input(inputStream);
            Object o = input.readObject();
            return o;
        } catch (Exception e) {
            throw new SerializeException("parse object error");
        }
    }

    @Override
    public byte getType() {
        return SerializerType.HESSIAN.getType();
    }

    @Override
    public Class<Object> getSerializerClass() {
        return Object.class;
    }
}
