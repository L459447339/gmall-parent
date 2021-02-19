package com.atguigu.gmall.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.weaver.JoinPointSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//redis缓存切面类
@Aspect
@Component
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Around("@annotation(com.atguigu.gmall.aspect.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point) {
        Object proceed = null;
        //获取方法的签名信息
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        //获取方法参数
        Object[] args = point.getArgs();
        //获取方法的返回值
        Class<?> returnType = methodSignature.getMethod().getReturnType();
        //获取方法名
        String methodName = methodSignature.getMethod().getName();
        //获取方法上的注解信息
        GmallCache annotation = methodSignature.getMethod().getAnnotation(GmallCache.class);
        //拼接key
        String argKey = "";
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                boolean flag = isUserObject(arg);
                if (flag) {
                    argKey = argKey + ":" + arg.getClass().getTypeName();
                } else {
                    argKey = argKey + ":" + arg;
                }
            }
        }
        //获取redis存储数据的key前缀
        String key = annotation.key();
        //获取redis存储数据类型
        String type = annotation.type();
        String currentKey = "";
        if (methodName.toLowerCase().contains("sku")) {
            currentKey = key + argKey + ":" + methodName;
        }
        //尝试获取redis中的数据
        proceed = redisTemplate.opsForValue().get(currentKey);

        if (proceed == null) {
            //线程获取分布式锁
            String lockVal = UUID.randomUUID().toString() + ":lock";
            Boolean lockFlag = redisTemplate.opsForValue().setIfAbsent("lock", lockVal);
            if (lockFlag) {
                try {
                    //执行方法，查db
                    proceed = point.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                if (proceed != null) {
                    if (type.equals("str")) {
                        redisTemplate.opsForValue().set(currentKey, proceed);
                    }

                } else {
                    redisTemplate.opsForValue().set(currentKey, null, 2, TimeUnit.MINUTES);
                }
                //后置通知
                //使用Lua脚本让判断和释放锁同步执行
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 设置lua脚本返回的数据类型
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                // 设置lua脚本返回类型为Long
                redisScript.setResultType(Long.class);
                redisScript.setScriptText(script);
                //第一个参数是Lua脚本，判断第二个参数和第三个参数是否相等，如果相等则将lock锁删除，不相等什么也不做
                redisTemplate.execute(redisScript, Arrays.asList("lock"), lockVal);// 执行脚本
            }else {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return redisTemplate.opsForValue().get(currentKey);
            }
        }
        return proceed;
    }

    //判断参数是否是自定义对象类型
    private boolean isUserObject(Object type) {
        boolean flag = false;
        String typeName = type.getClass().getTypeName();
        if (!typeName.toLowerCase().contains("long") && !typeName.toLowerCase().contains("integer") &&
                !typeName.toLowerCase().contains("bigdecimal") && !typeName.toLowerCase().contains("string")) {
            flag = true;
        }
        return flag;
    }
}
