package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.cart.CartInfo;
import com.atguigu.gmall.cart.mapper.CartMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductFeignClient productFeignClient;

    @Override
    public void addCart(CartInfo cartInfo) {
        Long skuId = cartInfo.getSkuId();
        String userId = cartInfo.getUserId();
        //根据用户id和skuId查询唯一的购物车信息，如果为null则进行添加，如果有值则修改数量
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("sku_id",skuId);
        CartInfo cartInfoResult = cartMapper.selectOne(queryWrapper);
        //根据skuId远程调用获取skuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if(cartInfoResult==null){
            //新增操作
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setIsChecked(1);
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setCartPrice(new BigDecimal(skuInfo.getPrice().toString()).multiply(new BigDecimal(cartInfo.getSkuNum().toString())));
            cartMapper.insert(cartInfo);
        }else {
            //修改操作
            Integer skuNum = cartInfoResult.getSkuNum();
            Integer skuNumNew = cartInfo.getSkuNum();
            //将之前的sku商品数量和再次点击加入购物车的sku商品数量相加得到现有的
            BigDecimal add = new BigDecimal(skuNum.toString()).add(new BigDecimal(skuNumNew.toString()));
            cartInfoResult.setSkuNum(add.intValue());
            cartInfoResult.setCartPrice(add.multiply(new BigDecimal(skuInfo.getPrice().toString())));
            cartMapper.updateById(cartInfoResult);
        }
    }
}
