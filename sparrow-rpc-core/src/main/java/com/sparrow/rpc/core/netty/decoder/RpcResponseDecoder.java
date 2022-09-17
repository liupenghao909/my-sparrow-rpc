package com.sparrow.rpc.core.netty.decoder;

import com.sparrow.rpc.core.serialize.SerializeSupport;
import com.sparrow.rpc.core.serialize.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 字节流从channel出来时需要转换成对象
 *
 * @author chengwei_shen
 * @date 2022/7/14 11:25
 **/
public class RpcResponseDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int len = byteBuf.readInt();
        byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);
        Object parse = SerializeSupport.parse(bytes, SerializerType.HESSIAN.getType());
        list.add(parse);
    }
}
