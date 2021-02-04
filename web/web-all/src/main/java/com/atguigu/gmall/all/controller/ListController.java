package com.atguigu.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.SearchParam;
import com.atguigu.gmall.list.SearchResponseVo;
import com.atguigu.gmall.list.client.ListFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
    @RequestMapping({"list.html","search.html"})
    public String list(Model model, SearchParam searchParam){
        SearchResponseVo searchResponseVo = listFeignClient.list(searchParam);
        model.addAttribute("goodsList",searchResponseVo.getGoodsList());
        model.addAttribute("trademarkList",searchResponseVo.getTrademarkList());
        model.addAttribute("attrsList",searchResponseVo.getAttrsList());
        String urlParam = getUrlParam(searchParam);
        model.addAttribute("urlParam",urlParam);

        return "list/index";
    }

    private String getUrlParam(SearchParam searchParam) {
        String urlParam = "list.html?";
        String keyword = searchParam.getKeyword();
        Long category3Id = searchParam.getCategory3Id();
        String order = searchParam.getOrder();
        String[] props = searchParam.getProps();
        String trademark = searchParam.getTrademark();

        //关键字
        if(!StringUtils.isEmpty(keyword)){
            urlParam = urlParam + "keyword=" + keyword;
        }
        //三级分类id
        if(category3Id!=null){
            urlParam = urlParam + "category3Id=" + category3Id;
        }
        //商品 tmId:tmName
        if(!StringUtils.isEmpty(trademark)){
            urlParam = urlParam + "&trademark=" + trademark;
        }
        //平台属性  attrId:attrValue:attrName
        if(props!=null && props.length>0){
            for (String prop : props) {
                urlParam = urlParam + "&pros=" + prop;
            }
        }
        return urlParam;
    }

}
