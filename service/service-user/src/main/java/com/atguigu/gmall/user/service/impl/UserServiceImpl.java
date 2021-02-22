package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    RedisTemplate redisTemplate;

    //根据用户名和密码查询
    @Override
    public Map<String,Object> login(UserInfo userInfo) {
        Map<String,Object> map = new HashMap<>();
        String loginName = userInfo.getLoginName();
        String passwd = userInfo.getPasswd();
        //加密后的密码
        String passwdNew = MD5.encrypt(passwd);
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("login_name",loginName);
        queryWrapper.eq("passwd",passwdNew);
        UserInfo userNew = userInfoMapper.selectOne(queryWrapper);
        if(userNew!=null){
            //生成Token
            String token = UUID.randomUUID().toString();
            //将token作为key，userId作为value存储在redis中并设置过期时间
            redisTemplate.opsForValue().set(RedisConst.USER_KEY_PREFIX+token,userNew.getId(),RedisConst.USERKEY_TIMEOUT);
            map.put("token",token);
            map.put("nickName",userNew.getNickName());
            map.put("name",userNew.getName());
            return map;
        }
        return null;
    }
}
