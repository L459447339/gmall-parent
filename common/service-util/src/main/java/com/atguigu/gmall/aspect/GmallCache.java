package com.atguigu.gmall.aspect;

import com.atguigu.gmall.constant.RedisConst;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {

    String key() default "sku";
    String type() default "str";

}
