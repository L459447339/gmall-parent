package com.atguigu.gmall.service;

import com.atguigu.gmall.product.BaseCategory1;
import com.atguigu.gmall.product.BaseCategory2;

import java.util.List;

public interface ManageService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(Long category1Id);
}
