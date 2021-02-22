package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/user/passport")
//@CrossOrigin
public class UserApiController {

    @Autowired
    UserService userService;

    //测试
    @RequestMapping("inner/ping")
    String ping(){
        return "pong";
    }

    //登录接口
    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo){
        //到db中查询该用户的信息
        Map<String,Object> map = userService.login(userInfo);
        if(map==null){
            return Result.fail().message("用户名或密码错误！请重新登录！！");
        }else {
            return Result.ok(map).message("登录成功");
          }
        }
    }

