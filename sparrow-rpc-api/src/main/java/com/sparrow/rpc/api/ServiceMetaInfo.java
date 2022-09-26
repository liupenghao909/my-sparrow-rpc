package com.sparrow.rpc.api;

/**
 * @author chengweishen
 * @date 2022/7/16 23:24
 */
public class ServiceMetaInfo {
    String nameSpace;
    String serviceName;

    public ServiceMetaInfo() {
    }

    public ServiceMetaInfo(String nameSpace, String serviceName) {
        this.nameSpace = nameSpace;
        this.serviceName = serviceName;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getServiceSign() {
        return nameSpace + ":" + serviceName;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}

