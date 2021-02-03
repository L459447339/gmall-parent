package com.atguigu.gmall.list.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.result.Result;
import com.atguigu.gmall.list.SearchParam;
import com.atguigu.gmall.list.SearchResponseVo;
import com.atguigu.gmall.list.service.ListService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/list")
public class ListApiControoler {

    @Autowired
    private ListService listService;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @RequestMapping("test")
    public Result test() throws IOException {
        //检索请求对象
        SearchRequest searchRequest = new SearchRequest();
        //指定检索哪个索引
        searchRequest.indices("goods");
        searchRequest.types("info");
        //封装dsl语句对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("id", 2);
        sourceBuilder.query(matchQueryBuilder);
        //将dsl语句对象整合到请求对象中
        searchRequest.source(sourceBuilder);

//        //返回结果对象
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        //获取命中的条数
        long totalHits = hits.getTotalHits();
        System.out.println(totalHits);

        return Result.ok();
    }

//    @RequestMapping("createIndex")
//    public Result createIndex(){
//        //创建索引，参数是索引的实体类字节码
//        elasticsearchRestTemplate.createIndex(Goods.class);
//        //设置索引结构
//        elasticsearchRestTemplate.putMapping(Goods.class);
//        return Result.ok();
//    }
    //首页商品分类信息
    @RequestMapping("index")
    public List<JSONObject> getBaseCategoryList(){
        List<JSONObject> jsonObjects = listService.getBaseCategoryList();
        return jsonObjects;
    }

    //上架
    @RequestMapping("onSale/{skuId}")
    public void onSale(@PathVariable("skuId") Long skuId){
        listService.onSale(skuId);
    }

    //下架
    @RequestMapping("cancelSale/{skuId}")
    public void cancelSale(@PathVariable("skuId") Long skuId){
        listService.cancelSale(skuId);
    }

    @RequestMapping("incrHotScore/{skuId}")
    public void incrHotScore(@PathVariable("skuId") Long skuId){
        listService.incrHotScore(skuId);
    }

    @RequestMapping("list")
    public SearchResponseVo list(@RequestBody SearchParam searchParam){
        return listService.list(searchParam);
    }


}
