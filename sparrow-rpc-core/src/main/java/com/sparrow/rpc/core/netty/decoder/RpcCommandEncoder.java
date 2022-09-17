package com.sparrow.rpc.core.netty.decoder;

import com.sparrow.rpc.core.netty.dto.RpcCommand;
import com.sparrow.rpc.core.netty.dto.RpcHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author chengwei_shen
 * @date 2022/7/14 11:12
 **/
public class RpcCommandEncoder extends MessageToByteEncoder<RpcCommand> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcCommand rpcCommand, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(rpcCommand.getHeader().getSize());
        serializeHeader(rpcCommand.getHeader(), byteBuf);
        byteBuf.writeInt(rpcCommand.getData().length);
        byteBuf.writeBytes(rpcCommand.getData());
    }

    private void serializeHeader(RpcHeader header, ByteBuf buffer) {
        byte[] versionBytes = header.getVersion().getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(versionBytes.length);
        buffer.writeBytes(versionBytes);

        byte[] traceIdBytes = header.getTraceId().getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(traceIdBytes.length);
        buffer.writeBytes(traceIdBytes);

        byte[] typeBytes = header.getType().getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(typeBytes.length);
        buffer.writeBytes(typeBytes);
    }
}
