package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<CartInfo> {
}
