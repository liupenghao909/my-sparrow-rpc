package com.sparrow.rpc.core.serialize.impl;

import com.sparrow.rpc.core.netty.dto.RpcRequest;
import com.sparrow.rpc.core.serialize.Serializer;
import com.sparrow.rpc.core.serialize.SerializerType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 *
 * 自定义的序列化方式
 * 这个全部收口到channel的encoder做，这样后期可以直接替换
 *
 * @author chengwei_shen
 * @date 2022/7/15 13:53
 **/
public class RpcRequestSerializer implements Serializer<RpcRequest> {
    /**
     * 四个整型长度代表RpcRequest四个属性字段的长度，方便创建byte数组来接受四个字段的数据
     * @param request
     * @return
     */
    @Override
    public int getSize(RpcRequest request) {
        return request.getNameSpace().getBytes(StandardCharsets.UTF_8).length + Integer.BYTES +
                request.getServiceName().getBytes(StandardCharsets.UTF_8).length + Integer.BYTES +
                request.getMethodName().getBytes(StandardCharsets.UTF_8).length + Integer.BYTES +
                request.getParameters().length + Integer.BYTES;
    }

    @Override
    public byte[] serialize(RpcRequest o, ByteBuffer buffer) {
        /**
         * String.getBytes(String decode) 会根据指定的decode编码返回某字符串在该编码下的byte数组
         * decode：UTF-8、ISO8859-1、unicode
         * getBytes相对的，可以通过new String(byte[], decode)的方式来还原
         */
        byte[] nameSpaceBytes = o.getNameSpace().getBytes();
        // 长度
        buffer.putInt(nameSpaceBytes.length);
        // 内容
        buffer.put(nameSpaceBytes);

        byte[] serviceBytes = o.getServiceName().getBytes();
        buffer.putInt(serviceBytes.length);
        buffer.put(serviceBytes);

        byte[] methodBytes = o.getMethodName().getBytes();
        buffer.putInt(methodBytes.length);
        buffer.put(methodBytes);

        buffer.putInt(o.getParameters().length);
        buffer.put(o.getParameters());
        return buffer.array();
    }

    @Override
    public RpcRequest parse(ByteBuffer buffer) {
        int len = buffer.getInt();
        byte[] arrBytes = new byte[len];
        buffer.get(arrBytes);
        String nameSpace = new String(arrBytes, StandardCharsets.UTF_8);

        len = buffer.getInt();
        arrBytes = new byte[len];
        buffer.get(arrBytes);
        String serviceName = new String(arrBytes, StandardCharsets.UTF_8);

        len = buffer.getInt();
        arrBytes = new byte[len];
        buffer.get(arrBytes);
        String methodName = new String(arrBytes, StandardCharsets.UTF_8);

        len = buffer.getInt();
        arrBytes = new byte[len];
        buffer.get(arrBytes);

        RpcRequest request = new RpcRequest();
        request.setNameSpace(nameSpace);
        request.setServiceName(serviceName);
        request.setMethodName(methodName);
        request.setParameters(arrBytes);
        return request;
    }


    @Override
    public byte getType() {
        return SerializerType.RPC_REQUEST.getType();
    }

    @Override
    public Class<RpcRequest> getSerializerClass() {
        return RpcRequest.class;
    }
}
