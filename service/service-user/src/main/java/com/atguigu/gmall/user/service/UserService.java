package com.atguigu.gmall.user.service;

import com.atguigu.gmall.user.UserInfo;

import java.util.Map;

public interface UserService {
    Map<String,Object> login(UserInfo userInfo);

    Map<String, Object> verify(String token);

    void logout(String token);
}
