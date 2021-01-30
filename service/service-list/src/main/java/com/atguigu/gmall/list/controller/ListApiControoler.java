package com.atguigu.gmall.list.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/list")
public class ListApiControoler {

    @Autowired
    private ListService listService;

    @RequestMapping("index")
    public List<JSONObject> getBaseCategoryList(){
        List<JSONObject> jsonObjects = listService.getBaseCategoryList();
        return jsonObjects;
    }


}
