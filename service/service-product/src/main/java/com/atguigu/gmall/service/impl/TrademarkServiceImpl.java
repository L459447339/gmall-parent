package com.atguigu.gmall.service.impl;

import com.atguigu.gmall.bean.BaseTrademark;
import com.atguigu.gmall.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.service.TrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrademarkServiceImpl implements TrademarkService {

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public BaseTrademark getTrademark(Long tmId) {
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(tmId);
        return baseTrademark;
    }
}
