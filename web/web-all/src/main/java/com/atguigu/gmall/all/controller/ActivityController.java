package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.activity.OrderRecode;
import com.atguigu.gmall.activity.client.ActivityFeignClient;
import com.atguigu.gmall.activity.SeckillGoods;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.order.OrderDetail;
import com.atguigu.gmall.product.client.ProductFeignClient;
import com.atguigu.gmall.user.UserAddress;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ActivityController {

    @Autowired
    ActivityFeignClient activityFeignClient;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    ProductFeignClient productFeignClient;

    @RequestMapping("seckill.html")
    public String seckillIndex(Model model){
        List<SeckillGoods> seckillGoodsList = activityFeignClient.getSeckillGoodsList();
        model.addAttribute("list",seckillGoodsList);
        return "seckill/index";
    }

    @RequestMapping("seckill/{skuId}.html")
    public String seckillItem(@PathVariable("skuId") Long skuId,Model model){
        SeckillGoods seckillGoods = activityFeignClient.getSeckillGoods(skuId);
        model.addAttribute("item",seckillGoods);
        return "seckill/item";
    }

    //验证抢购码，跳转queue排队页面
    @RequestMapping("seckill/queue.html")
    public String seckill(Model model,@RequestParam("skuId") Long skuId, @RequestParam("skuIdStr") String skuIdStr, HttpServletRequest request){
        String userId = request.getHeader("userId");
        String seckillCode = MD5.encrypt(skuId + userId);
        if(skuIdStr.equals(seckillCode)){
            //queue.html页面需要这两个参数
            model.addAttribute("skuId",skuId);
            model.addAttribute("skuIdStr",skuIdStr);
            return "seckill/queue";
        }else {
            model.addAttribute("message","请求不合法!");
            return "seckill/fail";
        }
    }

    //页面需要参数 userAddressList detailArrayList  totalNum totalAmount
    @RequestMapping("seckill/trade.html")
    public String trade(Model model,HttpServletRequest request){
        String userId = request.getHeader("userId");
        OrderRecode orderRecode = activityFeignClient.getOrderRecode(userId);
        List<UserAddress> userAddressList = userFeignClient.getUserAddress(userId);
        model.addAttribute("userAddressList",userAddressList);
        SkuInfo skuInfo = productFeignClient.getSkuInfo(orderRecode.getSeckillGoods().getSkuId());
        List<OrderDetail> detailArrayList = new ArrayList<>();
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setImgUrl(skuInfo.getSkuDefaultImg());
        orderDetail.setOrderPrice(skuInfo.getPrice());
        orderDetail.setSkuNum(1);
        orderDetail.setSkuName(skuInfo.getSkuName());
        orderDetail.setSkuId(skuInfo.getId());
        detailArrayList.add(orderDetail);
        model.addAttribute("detailArrayList",detailArrayList);
        model.addAttribute("totalNum",1);
        model.addAttribute("totalAmount",skuInfo.getPrice());
        return "seckill/trade";
    }
}
