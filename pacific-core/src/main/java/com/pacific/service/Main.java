package com.pacific.service;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.net.InetAddress;
import java.util.Map;

/**
 * Created by Fe on 16/5/27.
 */
public class Main {

    public static final String CLUSTER_NAME = "elasticsearch";
    private static final String IP = "127.0.0.1";
    private static final int PORT = 9300;  //端口

    private static Settings settings = Settings
            .settingsBuilder()
            .put("cluster.name",CLUSTER_NAME)
            .put("client.transport.sniff", true)
            .build();

    public static void main(String args[]) throws Exception {
        Client client = TransportClient.builder().settings(settings).
                build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(IP), PORT));

        String index="aaa";
        String type="logs";
        SearchResponse response= client.prepareSearch(index)//设置要查询的索引(index)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setTypes(type)//设置type, 这个在建立索引的时候同时设置了, 或者可以使用head工具查看
                .setQuery(QueryBuilders.matchQuery("level", "ERROR")) //在这里"message"是要查询的field,"Accept"是要查询的内容
                .setFrom(0)
                .setSize(10000)
                .setExplain(true)
                .execute()
                .actionGet();
        for(SearchHit hit:response.getHits()){
            Map<String, Object> map = hit.getSource();

            String val = (String)map.get("@timestamp");


            System.out.println(hit.getId() + "### :" + hit.getSourceAsString());
        }


    }
}
