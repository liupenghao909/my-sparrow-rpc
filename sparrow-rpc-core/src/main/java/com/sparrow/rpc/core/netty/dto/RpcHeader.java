package com.sparrow.rpc.core.netty.dto;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * @author chengwei_shen
 * @date 2022/7/13 20:06
 **/
public class RpcHeader implements Serializable {
    /**
     * 版本号
     */
    String version;
    /**
     * traceId
     */
    String traceId;
    /**
     * 类型
     */
    String type;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return Integer.BYTES + version.getBytes(StandardCharsets.UTF_8).length + Integer.BYTES + traceId.getBytes(StandardCharsets.UTF_8).length + Integer.BYTES + type.getBytes(StandardCharsets.UTF_8).length;
    }
}
