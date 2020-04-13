package com.hp.sh.expv3.pc.grab3rdData.component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pc.grab3rdData.pojo.BinanceResponseEntity;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class BinanceWsClient extends WebSocketListener {
    private static final Logger logger = LoggerFactory.getLogger(BinanceWsClient.class);

    private String wsurl;

    private WebSocket ws;

    public static BlockingQueue<BinanceResponseEntity> queue = new ArrayBlockingQueue<BinanceResponseEntity>(10000000);

    public BinanceWsClient(String wsurl) {
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
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        logger.info("text={}", text);

        BinanceResponseEntity tickerData = JSON.parseObject(text, BinanceResponseEntity.class);
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

    public static BlockingQueue<BinanceResponseEntity> getBlockingQueue() {
        if (CollectionUtils.isEmpty(queue)) {
            return null;
        }
        return queue;
    }

}
