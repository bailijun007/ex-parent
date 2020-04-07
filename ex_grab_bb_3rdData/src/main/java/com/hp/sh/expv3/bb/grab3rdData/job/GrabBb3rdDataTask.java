package com.hp.sh.expv3.bb.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hp.sh.expv3.bb.grab3rdData.component.WsClient;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author BaiLiJun  on 2020/4/7
 */
@Component
public class GrabBb3rdDataTask {
    static final String url = "wss://api.zb.live/websocket";

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;


    @PostConstruct
    public RedisUtil getRedisUtil(){
        return metadataRedisUtil;
    }

//    @Scheduled(cron = "*/1 * * * * *")
    @PostConstruct
    public void startGrabBb3rdData(){
        WsClient client = new WsClient(url);
        client.connect();
//        client.send("{'event':'addChannel', 'channel':'ltcbtc_ticker',}");
        Map data = new TreeMap();
        data.put("event", "addChannel");
        data.put("channel", "btcusdt_ticker");
//        data.put("channel", "ltcusdt_ticker");
        client.send(JSONObject.toJSONString(data));
    }

}
