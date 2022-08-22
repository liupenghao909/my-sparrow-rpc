package com.sparrow.rpc.core.serialize;

/**
 * 序列器类型枚举类
 *
 * @author chengwei_shen
 * @date 2022/7/15 14:42
 **/
public enum SerializerType {
    RPC_REQUEST(1),
    TYPE_OBJECT(2),
    TYPE_OBJECT_ARRAY(3),
    HESSIAN(4);

    SerializerType(int type) {
        this.type = type;
    }

    int type;

    public byte getType() {
        return (byte) type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
