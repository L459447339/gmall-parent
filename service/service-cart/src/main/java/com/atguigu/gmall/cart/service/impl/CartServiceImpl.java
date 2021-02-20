package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.cart.CartInfo;
import com.atguigu.gmall.cart.mapper.CartMapper;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    RedisTemplate redisTemplate;

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
            cartInfo.setSkuNum(add.intValue());
            cartInfo.setCartPrice(add.multiply(new BigDecimal(skuInfo.getPrice().toString())));
            cartMapper.updateById(cartInfo);
        }
        //同步缓存
        List<CartInfo> cartInfos = cartMapper.selectList(null);
        if(cartInfos!=null){
            for (CartInfo info : cartInfos) {
                redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX+info.getUserId()+RedisConst.USER_CART_KEY_SUFFIX,
                        info.getSkuId().toString(),info);
            }
        }
    }

    //查询购物车列表
    @Override
    public List<CartInfo> cartList(String userId) {
        //查询缓存
        List<CartInfo> cartInfoList = (List<CartInfo>)redisTemplate.opsForHash().values(RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX);
        if(cartInfoList==null || cartInfoList.size()==0){
            QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
            cartInfoList = cartMapper.selectList(queryWrapper);
            //将每一个CartInfo中对应的skuId查询出Pirce设置进去
            for (CartInfo cartInfo : cartInfoList) {
                Long skuId = cartInfo.getSkuId();
                SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
                BigDecimal price = skuInfo.getPrice();
                cartInfo.setSkuPrice(price);
                //同步缓存
                redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX+cartInfo.getUserId()+RedisConst.USER_CART_KEY_SUFFIX,
                        cartInfo.getSkuId().toString(),cartInfo);
            }
        }
        return cartInfoList;
    }

    //更改购物车状态
    @Override
    public void ischeckCart(Long skuId, Integer isChecked,String userId) {
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id",skuId);
        queryWrapper.eq("user_id",userId);
        CartInfo cartInfo = cartMapper.selectOne(queryWrapper);
        cartInfo.setIsChecked(isChecked);
        cartMapper.updateById(cartInfo);
        //更新缓存
        CartInfo cartCache = (CartInfo) redisTemplate.opsForHash().get(RedisConst.USER_KEY_PREFIX + cartInfo.getUserId() + RedisConst.USER_CART_KEY_SUFFIX, cartInfo.getSkuId().toString());
        cartCache.setIsChecked(isChecked);
        redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX + cartInfo.getUserId() + RedisConst.USER_CART_KEY_SUFFIX, cartInfo.getSkuId().toString(),cartCache);
    }

    //删除购物车商品
    @Override
    public void deleteCart(Long skuId, String userId) {
        QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id",skuId);
        queryWrapper.eq("user_id",userId);
        cartMapper.delete(queryWrapper);
        //删除缓存
        redisTemplate.opsForHash().delete(RedisConst.USER_KEY_PREFIX+userId+RedisConst.USER_CART_KEY_SUFFIX,skuId.toString());
    }
}
