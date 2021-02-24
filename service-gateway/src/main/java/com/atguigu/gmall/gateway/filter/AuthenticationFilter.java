package com.atguigu.gmall.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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
        if (uri.contains("passport") || uri.contains("css") || uri.contains("js") || uri.contains("png") || uri.contains("jpg") || uri.contains("ico") || uri.contains("ttf")) {
            return chain.filter(exchange);
        }

        //黑名单
        boolean match = antPathMatcher.match("**/inner/**", uri);
        if (match) {
            Mono<Void> out = out(response, ResultCodeEnum.PERMISSION);
            return out;
        }

        //获取userTempId,添加购物车时前端页面生成的
        String userTempId = getCookieVal(request, "userTempId");
        //获取token
        String token = getCookieVal(request,"token");
        //验证token返回userId
        String userId = "";
        if(!StringUtils.isEmpty(token)){
            Map<String, Object> verifyMap = userFeignClient.verify(token);
            userId = (String) verifyMap.get("userId");
        }

        //需要登录的页面但不是黑白名单的
        if(antPathMatcher.match("**/auth/**",uri)||antPathMatcher.match("**/alipay/**",uri)){
            //验证userId是否未空，如果为空就踢回登录页面
            if(StringUtils.isEmpty(userId)){
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION,"http://passport.gmall.com/login?ReturnUrl="+uri);
                Mono<Void> mono = response.setComplete();
                return mono;
            }
        }

        //白名单
        String[] authUrlsArray = authUrls.split(",");
        for (String authUrl : authUrlsArray) {
            //判断请求url是否包含白名单地址，如果是则需要验证Token，验证成功允许访问，验证失败重定向到认证中心
            if (uri.contains(authUrl)) {
                //判断userId是否为空，为空验证失败，不为空验证成功放行
                if (StringUtils.isEmpty(userId)) {
                    //验证失败，重定向
                    HttpHeaders headers = response.getHeaders();
                    headers.set(HttpHeaders.LOCATION, "http://passport.gmall.com/login?ReturnUrl=" + uri);
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    Mono<Void> voidMono = response.setComplete();
                    return voidMono;
                }
            }
        }

        //返回userTempId
        if(!StringUtils.isEmpty(userTempId)){
            request.mutate().header("userTempId",userTempId).build();
        }

        //返回userId
        request.mutate().header("userId", userId).build();

        System.out.println(uri);
        return chain.filter(exchange);
    }


    private String getCookieVal(ServerHttpRequest request,String cookieName) {
        String result = "";
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        //一般请求获取
        if (cookies != null) {
            List<HttpCookie> tokenList = cookies.get(cookieName);
            if (tokenList != null && tokenList.size() > 0) {
                for (HttpCookie httpCookie : tokenList) {
                    result = httpCookie.getValue();
                }
            }
        }
        //异步请求获取
        if (cookies == null || cookies.size() == 0) {
            //从请求头中获取
            HttpHeaders headers = request.getHeaders();
            List<String> tokenList = headers.get(cookieName);
            if(tokenList!=null && tokenList.size()>0){
                result = tokenList.get(0);
            }
        }
        return result;
    }

    public Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bytes = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        HttpHeaders headers = response.getHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        Mono<DataBuffer> just = Mono.just(wrap);
        Mono<Void> voidMono = response.writeWith(just);
        return voidMono;
    }

}
