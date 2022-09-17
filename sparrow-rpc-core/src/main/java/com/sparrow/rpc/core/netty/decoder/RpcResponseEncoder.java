package com.sparrow.rpc.core.netty.decoder;

import com.sparrow.rpc.core.netty.dto.RpcResponse;
import com.sparrow.rpc.core.serialize.SerializeSupport;
import com.sparrow.rpc.core.serialize.SerializerType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author chengweishen
 * @date 2022/9/17 13:23
 */
public class RpcResponseEncoder extends MessageToByteEncoder<RpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse, ByteBuf byteBuf) throws Exception {
        byte[] bytes = SerializeSupport.serialize(rpcResponse, SerializerType.HESSIAN.getType());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
