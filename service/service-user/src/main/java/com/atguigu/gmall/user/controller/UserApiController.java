package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/user/passport")
@CrossOrigin
public class UserApiController {

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate redisTemplate;

    //测试
    @RequestMapping("inner/ping")
    String ping(){
        return "pong";
    }

    //登录接口
    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo){
        //到db中查询该用户的信息
        UserInfo userInfoNew = userService.login(userInfo);
        //如果查询的结果为空则登录失败，返回有值则生成Token返回给前端
        if(userInfoNew==null){
            return Result.fail().message("用户名或密码错误");
        }else {
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(RedisConst.USER_KEY_PREFIX+token,userInfoNew.getId().toString(),RedisConst.USERKEY_TIMEOUT);
            Map<String, Object> map = new HashMap<>();
            map.put("token",token);
            map.put("nickName",userInfoNew.getNickName());
            map.put("name",userInfoNew.getName());
            return Result.ok(map).message("登录成功");
        }

    }


}
