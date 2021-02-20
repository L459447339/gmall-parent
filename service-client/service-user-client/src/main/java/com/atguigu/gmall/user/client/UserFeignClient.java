package com.atguigu.gmall.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "service-user")
public interface UserFeignClient {

    @RequestMapping("api/user/passport/inner/ping")
    String ping();
}
