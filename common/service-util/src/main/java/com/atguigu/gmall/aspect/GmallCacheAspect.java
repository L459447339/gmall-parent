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
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//redis缓存切面类
@Aspect
@Component
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Around("@annotation(com.atguigu.gmall.aspect.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
        Object proceed = null;
        //获取方法的签名信息
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        //获取方法参数
        Object[] args = point.getArgs();
        //获取方法的返回值
        Class<?> returnType = methodSignature.getMethod().getReturnType();
        //获取方法名
        String methodName = methodSignature.getMethod().getName();
        //获取注解信息
        GmallCache annotation = methodSignature.getMethod().getAnnotation(GmallCache.class);
        //拼接key
        String argKey = "";
        if(args!=null && args.length>0){
            for (Object arg : args) {
                argKey = argKey +":"+ arg;
            }
        }
        String key = annotation.key();
        String type = annotation.type();
        String currentKey = "";
        //线程获取分布式锁
        String lockVal = UUID.randomUUID().toString() + ":lock";
        Boolean lockFlag = redisTemplate.opsForValue().setIfAbsent("lock", lockVal);
        if(lockFlag){
            if("sku".equalsIgnoreCase(key)){
                currentKey = key + argKey + ":" + methodName;
                if("str".equalsIgnoreCase(type)){
                    proceed = redisTemplate.opsForValue().get(currentKey);
                }
            }
            //执行方法
            if(proceed==null){
                try {
                    proceed = point.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                if(proceed!=null){
                    redisTemplate.opsForValue().set(currentKey,proceed);
                }else {
                    redisTemplate.opsForValue().set(currentKey,null,2, TimeUnit.MINUTES);
                }
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
                TimeUnit.MINUTES.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cacheAroundAdvice(point);
        }
        return proceed;
    }
}
