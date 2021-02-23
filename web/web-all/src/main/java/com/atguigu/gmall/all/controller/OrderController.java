package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.cart.CartInfo;
import com.atguigu.gmall.cart.client.CartFeignClient;
import com.atguigu.gmall.order.OrderDetail;
import com.atguigu.gmall.user.UserAddress;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    CartFeignClient cartFeignClient;

    @RequestMapping("myOrder.html")
    public String myOrder(HttpServletRequest request){
        //获取userId
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");
        System.out.println(userId);
        System.out.println(userTempId);
        return "order/myOrder";
    }

    //跳转结算页面
    @RequestMapping("trade.html")
    public String trade(HttpServletRequest request,Model model){
        //获取用户id
        String userId = request.getHeader("userId");
        //获取用户收获地址等信息
        List<UserAddress> userAddressList = userFeignClient.getUserAddress(userId);
        model.addAttribute("userAddressList",userAddressList);
        //获取商品清单等信息
        List<CartInfo> cartInfoList = cartFeignClient.cartListInner(userId);
        //将购物车中选中状态的商品转化到orderDetail中
        List<OrderDetail> orderDetailList = null;
        if(cartInfoList!=null && cartInfoList.size()>0){
            orderDetailList = new ArrayList<>();
            for (CartInfo cartInfo : cartInfoList) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setOrderPrice(cartInfo.getSkuPrice());
                orderDetailList.add(orderDetail);
            }
            model.addAttribute("detailArrayList",orderDetailList);
        }
        //结算总金额和总数量
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal totalNum = new BigDecimal("0");
        if(orderDetailList!=null && orderDetailList.size()>0){
            for (OrderDetail orderDetail : orderDetailList) {
                BigDecimal orderPrice = orderDetail.getOrderPrice();
                Integer skuNum = orderDetail.getSkuNum();
                totalAmount = totalAmount.add(orderPrice.multiply(new BigDecimal(skuNum+"")));
                totalNum = totalNum.add(new BigDecimal(skuNum+""));
            }
            model.addAttribute("totalAmount",totalAmount);
            model.addAttribute("totalNum",totalNum.intValue());
        }
        return "order/trade.html";
    }

    //跳转到支付页面
    @RequestMapping("pay.html")
    public String pay(String orderId,Model model){
        model.addAttribute("orderId",orderId);
        return "payment/pay";
    }

}
