package com.atguigu.gmall.service.impl;

import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.mapper.*;
import com.atguigu.gmall.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SpuImage> spuImages = spuImageMapper.selectList(wrapper);
        return spuImages;
    }

    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        QueryWrapper<SpuSaleAttr> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectList(wrapper);
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
        if(skuImageList!=null && skuImageList.size()>0){
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfoId);
                skuImageMapper.insert(skuImage);
            }
        }
        //添加sku销售属性
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(skuAttrValueList!=null && skuAttrValueList.size()>0){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfoId);
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
        //添加sku销售属性值
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(skuSaleAttrValueList!=null && skuSaleAttrValueList.size()>0){
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
        IPage<SkuInfo> ipage = new Page<>(page,limit);
        IPage<SkuInfo> infoIPage = skuInfoMapper.selectPage(ipage, null);
        return infoIPage;
    }

    //上架
    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(skuInfo.getIsSale()==0){
            skuInfo.setIsSale(1);
        }
        skuInfoMapper.updateById(skuInfo);
    }

    //下架
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(skuInfo.getIsSale()==1){
            skuInfo.setIsSale(0);
        }
        skuInfoMapper.updateById(skuInfo);
    }
}
