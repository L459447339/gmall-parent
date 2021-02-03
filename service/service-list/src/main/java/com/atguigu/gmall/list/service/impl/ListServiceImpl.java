package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.bean.BaseCategoryView;
import com.atguigu.gmall.bean.BaseTrademark;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.list.*;
import com.atguigu.gmall.list.repository.GoodsRepository;
import com.atguigu.gmall.list.service.ListService;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.aspectj.weaver.ast.Var;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ListServiceImpl implements ListService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> jsonObjects = productFeignClient.getBaseCategoryList();
        return jsonObjects;
    }

    //上架同步es
    @Override
    public void onSale(Long skuId) {
        Goods goods = new Goods();
        //获取sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        //获取品牌信息
        BaseTrademark baseTrademark = productFeignClient.getTrademark(skuInfo.getTmId());
        //获取商品分类信息
        BaseCategoryView baseCategoryView = productFeignClient.getBaseCategoryView(skuInfo.getCategory3Id());
        //获取平台属性集合对象
        List<SearchAttr> searchAttrList = productFeignClient.getSearchAttrList(skuId);
        //封装Goods结果集
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());
        goods.setTmId(baseTrademark.getId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        BeanUtils.copyProperties(baseCategoryView,goods);
        goods.setId(skuId);
        goods.setHotScore(0L);
        goods.setAttrs(searchAttrList);
        //将Goods添加到es索引中
        goodsRepository.save(goods);
    }

    //下架同步es
    @Override
    public void cancelSale(Long skuId) {
        //根据skuId删除es中Goods索引的数据
        goodsRepository.deleteById(skuId);
    }

    @Override
    public void incrHotScore(Long skuId) {
        redisTemplate.opsForValue().setIfAbsent("sku:" + skuId + ":hotScore",0L);
        //将热度值保存到redis中并+1
        Long increment = redisTemplate.opsForValue().increment("sku:" + skuId + ":hotScore", 1L);
        if(increment % 20==0){
            //获取到当前sku的热度值
            Optional<Goods> goodsOptional = goodsRepository.findById(skuId);
            Goods goods = goodsOptional.get();
            goods.setHotScore(increment);
            //每增加20次就往es中更新一次数据
            goodsRepository.save(goods);
        }
    }

    @Override
    public SearchResponseVo list(SearchParam searchParam) {
        String keyword = searchParam.getKeyword();
        Long category3Id = searchParam.getCategory3Id();
        String order = searchParam.getOrder();
        String[] props = searchParam.getProps();
        String trademark = searchParam.getTrademark();
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        if(category3Id!=null){
            //封装dsl
            SearchRequest category3IdSearchRequest = getCategory3IdSearchRequest(category3Id);
            try {
                //执行
                SearchResponse search = restHighLevelClient.search(category3IdSearchRequest, RequestOptions.DEFAULT);
                //解析结果
                searchResponseVo = getResult(search);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return searchResponseVo;
    }
    //解析结果
    private SearchResponseVo getResult(SearchResponse search) {
        List<Goods> goodsList = new ArrayList<>();
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = search.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            String sourceAsString = documentFields.getSourceAsString();
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);
        }
        searchResponseVo.setGoodsList(goodsList);
        //品牌聚合解析
        Aggregations aggregations = search.getAggregations();
        ParsedLongTerms tmIdAggr = (ParsedLongTerms)aggregations.get("tmIdAggr");
        List<? extends Terms.Bucket> bucketsTmId = tmIdAggr.getBuckets();
        //使用流式循环将buckets的数据抽取
        List<SearchResponseTmVo> collectTmIdAggr = bucketsTmId.stream().map(bucketTmId->{
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            Long keyTmId = (Long) bucketTmId.getKey();
            searchResponseTmVo.setTmId(keyTmId);
            //第二层tmName
            ParsedStringTerms tmNameAggr = (ParsedStringTerms)bucketTmId.getAggregations().get("tmNameAggr");
            List<SearchResponseTmVo> collectTmNameAggr = tmNameAggr.getBuckets().stream().map(bucketTmName->{
                String keyTmName = (String) bucketTmName.getKey();
                searchResponseTmVo.setTmName(keyTmName);
                return searchResponseTmVo;
            }).collect(Collectors.toList());
            //第二层tmLogoUrl
            ParsedStringTerms tmLogoUrlAggr = (ParsedStringTerms)bucketTmId.getAggregations().get("tmLogoUrlAggr");
            List<SearchResponseTmVo> collectTmLogoUrl = tmLogoUrlAggr.getBuckets().stream().map(bucketTmLogo->{
                String keyTmLogoUrl = (String) bucketTmLogo.getKey();
                searchResponseTmVo.setTmLogoUrl(keyTmLogoUrl);
                return searchResponseTmVo;
            }).collect(Collectors.toList());
            return searchResponseTmVo;
        }).collect(Collectors.toList());


        searchResponseVo.setTrademarkList(collectTmIdAggr);
        return searchResponseVo;
    }
    //封装dsl
    private SearchRequest getCategory3IdSearchRequest(Long category3Id){
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("goods");
        searchRequest.types("info");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("category3Id", 61L);
        searchSourceBuilder.query(matchQueryBuilder);
        //聚合品牌信息
        //第一层聚合
        TermsAggregationBuilder tmIdAggr = AggregationBuilders.terms("tmIdAggr").field("tmId");
        //第二层聚合
        TermsAggregationBuilder tmNameAggr = AggregationBuilders.terms("tmNameAggr").field("tmName");
        TermsAggregationBuilder tmLogUrlAggr = AggregationBuilders.terms("tmLogoUrlAggr").field("tmLogoUrl");
        //将第二层dsl语句放到第一层里
        tmIdAggr.subAggregation(tmNameAggr);
        tmIdAggr.subAggregation(tmLogUrlAggr);
        searchSourceBuilder.aggregation(tmIdAggr);
        searchRequest.source(searchSourceBuilder);
        //聚合平台属性信息
        //nested类型聚合



        return searchRequest;
    }
}
