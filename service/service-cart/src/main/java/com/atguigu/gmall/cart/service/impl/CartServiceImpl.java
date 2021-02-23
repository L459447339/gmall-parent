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
import org.springframework.util.StringUtils;

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
    public void addCart(Long skuId,Integer skuNum,String userId,String userTempId) {
        //根据skuId远程调用获取skuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if(!StringUtils.isEmpty(userId)){
            //说明处于登录状态，直接向db中存储购物车信息
            CartInfo cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setSkuNum(skuNum);
            cartInfo.setUserId(userId);
            //根据用户id和skuId查询唯一的购物车信息，如果为null则进行添加，如果有值则修改数量
            QueryWrapper<CartInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id",userId);
            queryWrapper.eq("sku_id",skuId);
            CartInfo cartInfoResult = cartMapper.selectOne(queryWrapper);
            if(cartInfoResult==null){
                //新增操作
                cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
                cartInfo.setIsChecked(1);
                cartInfo.setSkuName(skuInfo.getSkuName());
                cartInfo.setSkuPrice(skuInfo.getPrice());
                cartInfo.setCartPrice(new BigDecimal(skuInfo.getPrice().toString()).multiply(new BigDecimal(cartInfo.getSkuNum().toString())));
                cartMapper.insert(cartInfo);
            }else {
                //修改操作
                skuNum = cartInfoResult.getSkuNum();
                Integer skuNumNew = cartInfo.getSkuNum();
                //将之前的sku商品数量和再次点击加入购物车的sku商品数量相加得到现有的
                BigDecimal add = new BigDecimal(skuNum.toString()).add(new BigDecimal(skuNumNew.toString()));
                cartInfo.setSkuNum(add.intValue());
                cartInfo.setSkuPrice(skuInfo.getPrice());
                cartInfo.setCartPrice(add.multiply(new BigDecimal(skuInfo.getPrice().toString())));
                cartInfo.setId(cartInfoResult.getId());
                cartMapper.updateById(cartInfo);
            }
            //同步缓存
            if(cartInfo!=null){
                redisTemplate.opsForHash().put(RedisConst.USER_KEY_PREFIX+cartInfo.getUserId()+RedisConst.USER_CART_KEY_SUFFIX,
                        cartInfo.getSkuId().toString(),cartInfo);
            }
        }
       if(StringUtils.isEmpty(userId) && !StringUtils.isEmpty(userTempId)){
           //说明处于未登录状态并且有临时id，将购物车临时数据存储在redis中并设置过期时间
           CartInfo cartInfoTempOld = (CartInfo) redisTemplate.opsForHash().get("userTemp:" + userTempId + ":cart", skuId + "");
           //cartInfoTempOld如果为null则添加，如果有值则追加数量
           if(cartInfoTempOld==null){
               CartInfo cartInfoTemp = new CartInfo();
               cartInfoTemp.setUserId(userTempId);
               cartInfoTemp.setSkuNum(skuNum);
               cartInfoTemp.setSkuId(skuId);
               cartInfoTemp.setIsChecked(1);
               cartInfoTemp.setSkuName(skuInfo.getSkuName());
               cartInfoTemp.setImgUrl(skuInfo.getSkuDefaultImg());
               BigDecimal skuPirce = skuInfo.getPrice();
               cartInfoTemp.setSkuPrice(skuPirce);
               BigDecimal cartPrice = skuPirce.multiply(new BigDecimal(skuNum + ""));
               cartInfoTemp.setCartPrice(cartPrice);
               redisTemplate.opsForHash().put("userTemp:"+userTempId+":cart",skuId+"",cartInfoTemp);
           }else {
               //获取原来的数量
               Integer skuNumOld = cartInfoTempOld.getSkuNum();
               //获取追加后的数量
               BigDecimal addSkuNumTemp = new BigDecimal(skuNumOld + "").add(new BigDecimal(skuNum + ""));
               cartInfoTempOld.setSkuNum(addSkuNumTemp.intValue());
               cartInfoTempOld.setSkuPrice(skuInfo.getPrice());
               //创建计算出购物车商品价格
               BigDecimal cartTmepPrice = new BigDecimal(addSkuNumTemp + "").multiply(skuInfo.getPrice());
               cartInfoTempOld.setCartPrice(cartTmepPrice);
               redisTemplate.opsForHash().put("userTemp:"+userTempId+":cart",skuId+"",cartInfoTempOld);
           }
       }
    }

    //查询购物车列表
    @Override
    public List<CartInfo> cartList(String userId,String userTempId) {
        if(!StringUtils.isEmpty(userId)){
            //处于登录状态
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
        if(StringUtils.isEmpty(userId) && !StringUtils.isEmpty(userTempId)){
            //处于临时用户未登录状态
            List<CartInfo> cartInfoTempList = redisTemplate.opsForHash().values("userTemp:" + userTempId + ":cart");
            return cartInfoTempList;
        }
        return null;
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

    //用户登录后调用此方法将临时数据合并
    //TODO

}
