package com.atguigu.gmall.service;


import com.atguigu.gmall.bean.*;

import java.util.List;

public interface CategoryService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2(Long category1Id);

    List<BaseCategory3> getCategory3(Long category2Id);


}
