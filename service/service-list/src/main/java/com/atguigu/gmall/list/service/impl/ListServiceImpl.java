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
import org.apache.lucene.search.join.ScoreMode;
import org.aspectj.weaver.ast.Var;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        BeanUtils.copyProperties(baseCategoryView, goods);
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
        redisTemplate.opsForValue().setIfAbsent("sku:" + skuId + ":hotScore", 0L);
        //将热度值保存到redis中并+1
        Long increment = redisTemplate.opsForValue().increment("sku:" + skuId + ":hotScore", 1L);
        if (increment % 20 == 0) {
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
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("goods");
        searchRequest.types("info");
        //封装dsl
        SearchSourceBuilder searchResource = getSearchResource(searchParam);
        searchRequest.source(searchResource);
        try {
            //执行
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析结果
            searchResponseVo = getResult(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponseVo;
    }

    //解析结果
    private SearchResponseVo getResult(SearchResponse searchResponse) {
        List<Goods> goodsList = new ArrayList<>();
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        for (SearchHit documentFields : hits1) {
            String sourceAsString = documentFields.getSourceAsString();
            Goods goods = JSON.parseObject(sourceAsString, Goods.class);
            goodsList.add(goods);
        }
        searchResponseVo.setGoodsList(goodsList);

        //品牌聚合解析
        Aggregations aggregations = searchResponse.getAggregations();
        ParsedLongTerms tmIdAggr = (ParsedLongTerms) aggregations.get("tmIdAggr");
        List<? extends Terms.Bucket> bucketsTmId = tmIdAggr.getBuckets();
        //使用流式循环将buckets的数据抽取
        List<SearchResponseTmVo> collectTmIdAggr = bucketsTmId.stream().map(bucketTmId -> {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            Long keyTmId = (Long) bucketTmId.getKey();
            searchResponseTmVo.setTmId(keyTmId);
            //第二层tmName
            ParsedStringTerms tmNameAggr = (ParsedStringTerms) bucketTmId.getAggregations().get("tmNameAggr");
            String keyTmName = (String) tmNameAggr.getBuckets().get(0).getKey();
            searchResponseTmVo.setTmName(keyTmName);
            //第二层tmLogoUrl
            ParsedStringTerms tmLogoUrlAggr = (ParsedStringTerms) bucketTmId.getAggregations().get("tmLogoUrlAggr");
            String keyTmLogoUrl = (String) tmLogoUrlAggr.getBuckets().get(0).getKey();
            searchResponseTmVo.setTmLogoUrl(keyTmLogoUrl);
            return searchResponseTmVo;
        }).collect(Collectors.toList());
        searchResponseVo.setTrademarkList(collectTmIdAggr);

        //平台属性聚合解析
        ParsedNested attrsAgg = (ParsedNested) aggregations.get("attrsAgg");
        ParsedLongTerms attrIdAgg = (ParsedLongTerms) attrsAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> bucketsAttrId = attrIdAgg.getBuckets();
        //第一层元素
        List<SearchResponseAttrVo> collectAttrs = bucketsAttrId.stream().map(bucket -> {
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            Long keyAttrId = (Long) bucket.getKey();

            //第二层元素
            ParsedStringTerms attrNameAgg = (ParsedStringTerms) bucket.getAggregations().get("attrNameAgg");
            List<? extends Terms.Bucket> bucketsAttrName = attrNameAgg.getBuckets();
            String keyAttrName = (String) bucketsAttrName.get(0).getKey();

            ParsedStringTerms attrValueAgg = (ParsedStringTerms) bucket.getAggregations().get("attrValueAgg");
            List<? extends Terms.Bucket> bucketsAttrValue = attrValueAgg.getBuckets();
            List<String> collectAttrValue = bucketsAttrValue.stream().map(bucketAttrValue -> {
                String keyValue = bucketAttrValue.getKeyAsString();
                return keyValue;
            }).collect(Collectors.toList());

            searchResponseAttrVo.setAttrValueList(collectAttrValue);
            searchResponseAttrVo.setAttrName(keyAttrName);
            searchResponseAttrVo.setAttrId(keyAttrId);
            return searchResponseAttrVo;
        }).collect(Collectors.toList());

        searchResponseVo.setAttrsList(collectAttrs);
        return searchResponseVo;
    }

    //封装dsl
    private SearchSourceBuilder getSearchResource(SearchParam searchParam) {
        String keyword = searchParam.getKeyword();
        Long category3Id = searchParam.getCategory3Id();
        String order = searchParam.getOrder();
        String[] props = searchParam.getProps();
        String trademark = searchParam.getTrademark();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //查询
        //三级分类id
        if (category3Id != null) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", 61L);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        //品牌
        //tmId:tmName格式
        if (!StringUtils.isEmpty(trademark)) {
            //截取到tmId
            String[] split = trademark.split(":");
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("tmId", split[0]);
            boolQueryBuilder.filter(matchQueryBuilder);
        }
        //关键字
        if (keyword != null) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        //平台属性
        //attrId:attrValue:attrName格式
        if (props != null && props.length > 0) {

            for (String prop : props) {
                String[] split = prop.split(":");
                Long attrId = Long.parseLong(split[0]);
                String attrValue = split[1];
                String attrName = split[2];
                MatchQueryBuilder matchQueryBuilder1 = new MatchQueryBuilder("attrs.attrId", attrId);
                MatchQueryBuilder matchQueryBuilder2 = new MatchQueryBuilder("attrs.attrValue", attrValue);
                MatchQueryBuilder matchQueryBuilder3 = new MatchQueryBuilder("attrs.attrName", attrName);
                //创建nested内存的bool对象
                BoolQueryBuilder boolQueryBuilderNested = new BoolQueryBuilder();
                boolQueryBuilderNested.must(matchQueryBuilder1);
                boolQueryBuilderNested.must(matchQueryBuilder2);
                boolQueryBuilderNested.must(matchQueryBuilder3);
                //创建nested对象
                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", boolQueryBuilderNested, ScoreMode.None);
                //将nested对象放入外部的bool对象中
                boolQueryBuilder.must(nestedQueryBuilder);
            }
        }
        //排序
        //hotScore:price
        if (order != null) {
            searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        }

        searchSourceBuilder.query(boolQueryBuilder);

        //聚合品牌信息
        //第一层聚合
        TermsAggregationBuilder tmIdAggr = AggregationBuilders.terms("tmIdAggr").field("tmId");
        //第二层聚合
        tmIdAggr.subAggregation(AggregationBuilders.terms("tmNameAggr").field("tmName"));
        tmIdAggr.subAggregation(AggregationBuilders.terms("tmLogoUrlAggr").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(tmIdAggr);

        //聚合平台属性，nested类型
        NestedAggregationBuilder aggrsAgg = AggregationBuilders.nested("attrsAgg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"));
        NestedAggregationBuilder nestedAggregationBuilder = aggrsAgg.subAggregation(attrIdAgg);
        searchSourceBuilder.aggregation(nestedAggregationBuilder);
        System.out.println(searchSourceBuilder.toString());
        return searchSourceBuilder;
    }
}
