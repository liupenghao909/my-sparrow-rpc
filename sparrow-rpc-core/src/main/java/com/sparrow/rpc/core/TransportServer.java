package com.sparrow.rpc.core;

import java.io.Closeable;

/**
 * 实现网络传输的Server 接口类
 * @author chengwei_shen
 * @date 2022/7/13 16:31
 **/
public interface TransportServer extends Closeable {
    void start() throws Exception;
    @Override
    void close();
}
