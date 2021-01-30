package com.atguigu.gmall.service.impl;

import com.atguigu.gmall.aspect.GmallCache;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.constant.RedisConst;
import com.atguigu.gmall.mapper.*;
import com.atguigu.gmall.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    //显示spu图片信息
    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuImage> spuImages = spuImageMapper.selectList(wrapper);
        return spuImages;
    }

    //显示spu销售属性及属性值
    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        QueryWrapper<SpuSaleAttr> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectList(wrapper);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrs) {
            QueryWrapper<SpuSaleAttrValue> attrValueWrapper = new QueryWrapper<>();
            attrValueWrapper.eq("spu_id", spuId);
            attrValueWrapper.eq("base_sale_attr_id", spuSaleAttr.getBaseSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(attrValueWrapper);
            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValues);
        }
        return spuSaleAttrs;
    }

    //添加sku
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //添加sku商品
        skuInfoMapper.insert(skuInfo);
        Long skuInfoId = skuInfo.getId();
        //添加sku图片
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if (skuImageList != null && skuImageList.size() > 0) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfoId);
                skuImageMapper.insert(skuImage);
            }
        }
        //添加sku平台属性
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList != null && skuAttrValueList.size() > 0) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfoId);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
        //添加sku销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if (skuSaleAttrValueList != null && skuSaleAttrValueList.size() > 0) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValue.setSkuId(skuInfoId);
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
    }

    //分页查询sku
    @Override
    public IPage<SkuInfo> list(Long page, Long limit) {
        IPage<SkuInfo> ipage = new Page<>(page, limit);
        IPage<SkuInfo> infoIPage = skuInfoMapper.selectPage(ipage, null);
        return infoIPage;
    }

    //上架
    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo.getIsSale() == 0) {
            skuInfo.setIsSale(1);
        }
        skuInfoMapper.updateById(skuInfo);
        //同步搜索引擎
    }

    //下架
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if (skuInfo.getIsSale() == 1) {
            skuInfo.setIsSale(0);
        }
        skuInfoMapper.updateById(skuInfo);
        //同步搜索引擎
    }

    //获取sku信息和图片信息
    @Override
    //@GmallCache
    public SkuInfo getSkuInfo(Long skuId) {
//        SkuInfo skuInfo = null;
//        //进入的线程需要拿到分布式锁才能操作
//        String lockVal = UUID.randomUUID().toString() + ":lock";
//        Boolean lockTag = redisTemplate.opsForValue().setIfAbsent("lock", lockVal, 1, TimeUnit.MINUTES);
//        if (lockTag) {
//            //查看缓存中是否有值
//            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX);
//            //没有值就直接去数据库查
//            if (skuInfo == null) {
//                skuInfo = getSkuInfoFromDB(skuId);
//                //将查询到的值同步到redis中,需要判断不为空
//                if (skuInfo != null) {
//                    redisTemplate.opsForValue().set(RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX, skuInfo);
//                }else {
//                    redisTemplate.opsForValue().set(RedisConst.SKUKEY_PREFIX + skuId + RedisConst.SKUKEY_SUFFIX, null,1,TimeUnit.MINUTES);
//                }
//            }
//
//            //使用Lua脚本让判断和释放锁同步执行
//            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//            // 设置lua脚本返回的数据类型
//            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//            // 设置lua脚本返回类型为Long
//            redisScript.setResultType(Long.class);
//            redisScript.setScriptText(script);
//            //第一个参数是Lua脚本，判断第二个参数和第三个参数是否相等，如果相等则将lock锁删除，不相等什么也不做
//            redisTemplate.execute(redisScript, Arrays.asList("lock"), lockVal);// 执行脚本
////            判断是否为同一把锁
////            if (redisTemplate.opsForValue().get("lock").equals(lockVal)) {
////                //释放锁
////                redisTemplate.delete("lock");
////            }
//        } else {
//            //没拿到锁的对象需要进行自旋
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return getSkuInfo(skuId);
//        }
//        return skuInfo;
        RLock lock = redissonClient.getLock(UUID.randomUUID().toString()+":lock");
        SkuInfo skuInfo;
        try {
            lock.lock(5,TimeUnit.SECONDS);
            skuInfo = skuInfoMapper.selectById(skuId);
            QueryWrapper<SkuImage> imageQueryWrapper = new QueryWrapper<>();
            imageQueryWrapper.eq("sku_id", skuId);
            List<SkuImage> skuImages = skuImageMapper.selectList(imageQueryWrapper);
            skuInfo.setSkuImageList(skuImages);
        } finally {
            lock.unlock();
        }
        return skuInfo;
    }

//    //提取出到数据库查询的方法
//    public SkuInfo getSkuInfoFromDB(Long skuId) {
//        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
//        QueryWrapper<SkuImage> imageQueryWrapper = new QueryWrapper<>();
//        imageQueryWrapper.eq("sku_id", skuId);
//        List<SkuImage> skuImages = skuImageMapper.selectList(imageQueryWrapper);
//        skuInfo.setSkuImageList(skuImages);
//        return skuInfo;
//    }

    //获取价格
    @Override
    public BigDecimal getPrice(Long skuId) {
        RLock lock = redissonClient.getLock(UUID.randomUUID().toString() + "lock");
        try {
            lock.lock(5,TimeUnit.SECONDS);
            SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
            return skuInfo.getPrice();
        } finally {
            lock.unlock();
        }
    }

    //获取销售属性及对应的sku销售属性
    @Override
    //@GmallCache
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList = skuSaleAttrValueMapper.getSpuSaleAttrListCheckBySku(skuId, spuId);
        return spuSaleAttrList;
    }

    //获取根据销售属性组合获取sku_id的kv
    @Override
    //@GmallCache
    public List<Map<String, Object>> getValuesSkuJson(Long spuId) {
        List<Map<String, Object>> maps = skuSaleAttrValueMapper.getValuesSkuJson(spuId);
        return maps;
    }
}
