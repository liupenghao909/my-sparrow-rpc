package com.sparrow.rpc.api;


import com.sparrow.rpc.api.dto.User;

/**
 * @author chengwei_shen
 * @date 2022/7/20 10:55
 **/
public interface UserService {
    User getUserByName(String name);

    default ServiceMetaInfo getServiceMeta() {
        ServiceMetaInfo metaInfo = new ServiceMetaInfo();
        metaInfo.setNameSpace("Sparrow");
        metaInfo.setServiceName("UserService");
        return metaInfo;
    }

    default String getServiceSign() {
        return getServiceMeta().getNameSpace() + ":" + getServiceMeta().getServiceName();
    }
}
