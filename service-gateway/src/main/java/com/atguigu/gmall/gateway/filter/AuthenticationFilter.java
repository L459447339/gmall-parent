package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.result.Result;
import com.atguigu.common.result.ResultCodeEnum;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class AuthenticationFilter implements GlobalFilter {

    @Autowired
    UserFeignClient userFeignClient;

    //匹配规则工具类
    AntPathMatcher antPathMatcher = new AntPathMatcher();

    //获取白名单的字符串地址
    @Value("${authUrls.url}")
    public String authUrls;

    //鉴权
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String uri = request.getURI().toString();
        //静态资源放行
        if(uri.contains("passport") || uri.contains("css")||uri.contains("js")||uri.contains("png")||uri.contains("jpg")||uri.contains("ico")||uri.contains("ttf")){
            return chain.filter(exchange);
        }
        //黑名单
        boolean match = antPathMatcher.match("**/inner/**", uri);
        if(match){
            Mono<Void> out = out(response, ResultCodeEnum.PERMISSION);
            return out;
        }
        //白名单
        String[] authUrlsArray = authUrls.split(",");
        for (String authUrl : authUrlsArray) {
            //判断请求url是否包含白名单地址，如果是则需要验证Token，验证成功允许访问，验证失败重定向到认证中心
            if(uri.contains(authUrl)){
                //验证Token..TODO
                //验证失败，重定向
                HttpHeaders headers = response.getHeaders();
                headers.set(HttpHeaders.LOCATION,"http://passport.gmall.com/login?ReturnUrl="+uri);
                response.setStatusCode(HttpStatus.SEE_OTHER);
                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }
        }

        String str = userFeignClient.ping();
        System.out.println(str);

        System.out.println(uri);
        return chain.filter(exchange);
    }

    public Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum){
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bytes = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        HttpHeaders headers = response.getHeaders();
        headers.add("Content-Type","application/json;charset=UTF-8");
        Mono<DataBuffer> just = Mono.just(wrap);
        Mono<Void> voidMono = response.writeWith(just);
        return voidMono;
    }

}
