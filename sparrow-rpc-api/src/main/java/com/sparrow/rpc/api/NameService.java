package com.sparrow.rpc.api;

import java.net.URI;

/**
 * NameService的目的在于，对于服务方，可以实现服务注册，可以理解成把当前服务的ip地址与服务名映射
 * 关系存放到NameService；对于客户端，可以实现服务发现，根据服务名找到服务的ip+端口，才能发送请求到对应服务
 *
 * @author chengwei_shen
 * @date 2022/7/12 20:03
 **/
public interface NameService {
    /**
     * 服务注册
     *
     * @param serviceSign
     * @param uri
     */
    void registerServer(String serviceSign, URI uri);

    /**
     * 服务下线
     */
    void unregisterServer(String serviceSign, URI uri);

    /**
     * 服务查询
     */
    URI seekService(String serviceSign);
}
