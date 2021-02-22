package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("api/user/passport")
//@CrossOrigin
public class UserApiController {

    @Autowired
    UserService userService;

    //测试
    @RequestMapping("inner/ping")
    String ping() {
        return "pong";
    }

    //登录接口
    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo) {
        //到db中查询该用户的信息
        Map<String, Object> map = userService.login(userInfo);
        if (map == null) {
            return Result.fail().message("用户名或密码错误！请重新登录！！");
        } else {
            return Result.ok(map).message("登录成功");
        }
    }

    //验证接口
    @RequestMapping("verify/{token}")
    Map<String, Object> verify(@PathVariable("token") String token){
        Map<String, Object> map = userService.verify(token);
        return map;
    }

    //退出登录接口
    @RequestMapping("logout")
    public Result logout(HttpServletRequest request){
        String token = request.getHeader("token");
        userService.logout(token);
        return Result.ok();
    }

    //注册接口
    @RequestMapping("register")
    public Result register(){
        //TODO
        return null;
    }


//    @RequestMapping("trade")
//    void trade(HttpServletRequest request){
//        String userId = request.getHeader("userId");
//        String userTempId = request.getHeader("userTempId");
//        System.out.println(userId);
//        System.out.println(userTempId);
//    }

}

