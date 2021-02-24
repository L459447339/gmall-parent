package com.atguigu.gmall.payment.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:alipay.properties")  //加载指定的配置文件
public class AlipayClientConfig {

    @Value("${alipay.service_url}")
    public String serviceUrl;
    @Value("${alipay.app_id}")
    public String appId;
    @Value("${alipay.private_key}")
    public String pricateKey;
    @Value("${alipay.format}")
    public String format;
    @Value("${alipay.charset}")
    public String charset;
    @Value("${alipay.public_key}")
    public String publicKey;
    @Value("${alipay.sign_type}")
    public String signType;

    @Bean
    AlipayClient alipayClient(){
        AlipayClient alipayClient = new DefaultAlipayClient(serviceUrl,appId,pricateKey,format,charset,publicKey,signType);
        return alipayClient;
    }
}
