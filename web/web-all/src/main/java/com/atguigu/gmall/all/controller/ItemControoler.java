package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.item.client.ItemFenignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemControoler {

    @Autowired
    private ItemFenignClient itemFenignClient;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId, Model model){
        Map<String,Object> map = itemFenignClient.getItem(skuId);
        model.addAllAttributes(map);
        return "item/index";
    }

}
