package com.atguigu.gmall.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(value = "service-user")
public interface UserFeignClient {

    @RequestMapping("api/user/passport/inner/ping")
    String ping();

    @RequestMapping("api/user/passport/verify/{token}")
    Map<String, Object> verify(@PathVariable("token") String token);

//    @RequestMapping("api/user/passport/trade")
//    void trade();
}
