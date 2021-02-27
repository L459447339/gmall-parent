package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.UserAddress;
import com.atguigu.gmall.user.UserInfo;
import com.atguigu.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/user/passport")
//@CrossOrigin
public class UserApiController {

    @Autowired
    UserService userService;

    @Autowired
    CartFeignClient cartFeignClient;

    //测试
    @RequestMapping("inner/ping")
    String ping() {
        return "pong";
    }

    //登录接口
    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo,HttpServletRequest request) {
        //到db中查询该用户的信息
        Map<String, Object> map = userService.login(userInfo);
        if (map == null) {
            return Result.fail().message("用户名或密码错误！请重新登录！！");
        } else {
            //登录成功，合并购物车临时数据
            String userId = (String) map.get("userId");
            String userTempId = request.getHeader("userTempId");
            if(!StringUtils.isEmpty(userTempId)){
                cartFeignClient.mergeCart(userId,userTempId);
            }
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

    //获取用户收获地址接口
    @RequestMapping("getUserAddress/{userId}")
    List<UserAddress> getUserAddress(@PathVariable("userId") String userId){
        List<UserAddress> userAddressList = userService.getUserAddress(userId);
        return userAddressList;
    }



}

