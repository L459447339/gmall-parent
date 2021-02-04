package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.SearchParam;
import com.atguigu.gmall.list.SearchResponseVo;
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

    /**
     * 跳转到首页，分类列表可以通过后台查询，也可以使用静态化展示
     */
    @RequestMapping("index")
    public String index(Model model){
//        List<JSONObject> jsonObjects = listFeignClient.getBaseCategoryList();
//        model.addAttribute("list",jsonObjects);
        return "index";
    }

    /**
     * 商品列表接口
     * SearchParam 封装了检索条件参数
     * SearchResponseVo  封装了es检索结果数据：品牌信息集合、平台属性集合、商品信息集合
     */
    @RequestMapping("list")
    public String list(Model model, SearchParam searchParam){
        SearchResponseVo searchResponseVo = listFeignClient.list(searchParam);
        model.addAttribute("goodsList",searchResponseVo.getGoodsList());
        model.addAttribute("trademarkList",searchResponseVo.getTrademarkList());
        return "list/index";
    }

}
