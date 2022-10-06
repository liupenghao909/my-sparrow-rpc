package com.sparrow.rpc.server;

import com.sparrow.rpc.api.UserService;
import com.sparrow.rpc.api.dto.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chengwei_shen
 * @date 2022/7/20 10:56
 **/
public class UserServiceImpl implements UserService {
    private static Map<String, User> userStore;

    static {
        cacheUser();
    }

    @Override
    public User getUserByName(String name) {
        return userStore.getOrDefault(name, new User());
    }

    private static void cacheUser() {
        userStore = new HashMap<>();
        User user1 = new User("Mason", 21, (byte) 1);
        User user2 = new User("Mike", 22, (byte) 1);
        User user3 = new User("Jane", 21, (byte) 0);
        userStore.put("Mason", user1);
        userStore.put("Mike", user2);
        userStore.put("Jane", user3);
    }
}
