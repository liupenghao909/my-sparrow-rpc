package com.sparrow.rpc.client;

import com.sparrow.rpc.api.RpcAccessor;
import com.sparrow.rpc.api.ServiceMetaInfo;
import com.sparrow.rpc.api.UserService;
import com.sparrow.rpc.api.dto.User;
import com.sparrow.rpc.api.spi.SpiSupport;
import com.sparrow.rpc.namesrv.JsonUtil;

/**
 * @author chengwei_shen
 * @date 2022/7/20 15:58
 **/
public class Client {
    public static void main(String[] args) throws Exception {
        try (RpcAccessor rpcAccessor = SpiSupport.load(RpcAccessor.class)) {
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo("Sparrow", "UserService");
            UserService userService = rpcAccessor.getRemoteService(serviceMetaInfo, UserService.class);
            User mason = userService.getUserByName("Mason");
            System.out.printf(JsonUtil.writeValue(mason));
            Thread.sleep(1000);
        }
    }
}
