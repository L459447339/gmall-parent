package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.client.ListFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class ListController {

    @Autowired
    private ListFeignClient listFeignClient;

    @RequestMapping("index")
    public String index(Model model){
        List<JSONObject> jsonObjects = listFeignClient.getBaseCategoryList();
        model.addAttribute("list",jsonObjects);
        return "index/index";
    }

}
