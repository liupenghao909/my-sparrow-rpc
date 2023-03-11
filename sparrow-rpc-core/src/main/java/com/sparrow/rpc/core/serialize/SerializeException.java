package com.sparrow.rpc.core.serialize;

/**
 * 自定义序列化异常类
 * 便于定位异常类型
 * @author chengwei_shen
 * @date 2022/7/19 19:50
 **/
public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }
}
