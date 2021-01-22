package com.atguigu.gmall.service.impl;


import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.mapper.SpuInfoMapper;
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

    //分页查询spu
    @Override
    public IPage<SpuInfo> getSpuInfo(Long page, Long limit, Long category3Id) {
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id",category3Id);
        IPage<SpuInfo> page1 = new Page<>(page,limit);

        IPage<SpuInfo> iPage = spuInfoMapper.selectPage(page1, queryWrapper);
        return iPage;
    }
}
