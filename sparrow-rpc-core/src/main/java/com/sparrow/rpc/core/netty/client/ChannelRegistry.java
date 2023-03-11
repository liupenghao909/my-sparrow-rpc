package com.sparrow.rpc.core.netty.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Channel注册中心,避免重复创建channel
 *
 * @author chengwei_shen
 * @date 2022/7/14 11:52
 **/
public class ChannelRegistry {
    // 地址和channel的映射关系
    private Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    private static ChannelRegistry INSTANCE;

    public static ChannelRegistry getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new ChannelRegistry();
            return INSTANCE;
        }
        return INSTANCE;
    }

    public void put(InetSocketAddress address, Channel channel) {
        String key = address.toString();
        channelMap.put(key, channel);
    }

    public Channel get(InetSocketAddress address) {
        String key = address.toString();
        Channel channel = channelMap.get(key);
        if (Objects.isNull(channel) || !channel.isActive()) {
            channelMap.remove(key);
            return null;
        }
        return channel;
    }

    public void removeAll(){
        Iterator<Map.Entry<String, Channel>> iterator = channelMap.entrySet().iterator();
        while (iterator.hasNext()){
            if(Objects.nonNull(iterator.next().getValue())){
                iterator.next().getValue().close();
            }
        }
    }
}
