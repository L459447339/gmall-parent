package com.atguigu.gmall.service.impl;


import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.mapper.*;
import com.atguigu.gmall.service.SpuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    //分页查询spu
    @Override
    public IPage<SpuInfo> getSpuInfo(Long page, Long limit, Long category3Id) {
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id",category3Id);
        IPage<SpuInfo> page1 = new Page<>(page,limit);

        IPage<SpuInfo> iPage = spuInfoMapper.selectPage(page1, queryWrapper);
        return iPage;
    }

    //查询所有销售属性
    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrs = baseSaleAttrMapper.selectList(null);
        return baseSaleAttrs;
    }

    //查询所有品牌属性
    @Override
    public List<BaseTrademark> getTrademarkList() {
        List<BaseTrademark> baseTrademarks = baseTrademarkMapper.selectList(null);
        return baseTrademarks;
    }

    //添加spu
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //添加spu基本信息
        spuInfoMapper.insert(spuInfo);
        Long spuInfoId = spuInfo.getId();
        //添加spu商品图片
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if(spuImageList!=null && spuImageList.size()>0){
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfoId);
                spuImageMapper.insert(spuImage);
            }
        }
        //添加spu销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(spuSaleAttrList!=null && spuSaleAttrList.size()>0){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfoId);
                spuSaleAttrMapper.insert(spuSaleAttr);
                //添加spu销售属性值
                Long spuSaleAttrId = spuSaleAttr.getId();
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(spuSaleAttrValueList!=null && spuSaleAttrValueList.size()>0){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfoId);
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);

                    }
                }
            }
        }
    }
}
