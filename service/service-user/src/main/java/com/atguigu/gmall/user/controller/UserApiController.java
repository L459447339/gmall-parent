package com.atguigu.gmall.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user/passport")
public class UserApiController {

    @RequestMapping("inner/ping")
    String ping(){
        return "pong";
    }
}
