package com.hp.sh.expv3.bb.grab3rdData.component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.grab3rdData.pojo.TickerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;


public class WsClient extends WebSocketListener {
    private static final Logger logger = LoggerFactory.getLogger(WsClient.class);

    private String wsurl = "wss://api.zb.live/websocket";

    private WebSocket ws;

    public static BlockingQueue<TickerData> queue = new ArrayBlockingQueue<>(10000000);

    public WsClient(String wsurl) {
        this.wsurl = wsurl;
    }

    public synchronized void connect() {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS)// 设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)// 设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)// 设置连接超时时间
                .build();

        Request request = new Request.Builder().url(wsurl).build();
        WebSocketListener socketListener = this;
        this.ws = mOkHttpClient.newWebSocket(request, socketListener);

    }

    public synchronized void close() {
        try {
            this.ws.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void send(Object msg) {
        String text = msg.toString();
        this.ws.send(text);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        logger.debug("open={}", webSocket);
//		this.ws.send("{'event':'addChannel', 'channel':'ltcbtc_ticker',}");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        logger.info("text={}", text);
        TickerData tickerData = JSON.parseObject(text, TickerData.class);
//   String key = "zb:wss:";
//        Map<String, Ticker> map = new HashMap<>();
// map.put(key, tickerData.getTicker());
//        if (tickerData.getChannel().equals("btcusdt_ticker")) {
//            key += tickerData.getChannel().split("_")[0];
//            logger.info("key={}", key);
//             RedisUtil redisUtil = grabBb3rdDataTask.getRedisUtil();
//            redisUtil.hmset(key,map);
//            metadataRedisUtil.hmset(key,map);
//    }
        queue.add(tickerData);

    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        logger.debug("t={}", t.getMessage(), t);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        System.out.println("onClosing");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        System.out.println("onClosed");
    }

    public static BlockingQueue<TickerData> getBlockingQueue() {
        if (CollectionUtils.isEmpty(queue)) {
            return null;
        }
        return queue;
    }

}
