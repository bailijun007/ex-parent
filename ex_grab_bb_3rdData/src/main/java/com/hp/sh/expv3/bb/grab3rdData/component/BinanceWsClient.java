package com.hp.sh.expv3.bb.grab3rdData.component;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.grab3rdData.pojo.BinanceResponseEntity;
import com.hp.sh.expv3.bb.grab3rdData.pojo.ZbResponseEntity;
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

    private Boolean isClosed = true;

    private static volatile BinanceWsClient binanceWsClient = null;

    private WebSocket ws;

    public static final BlockingQueue<BinanceResponseEntity> queue = new ArrayBlockingQueue<BinanceResponseEntity>(1024000);

    private BinanceWsClient(String wsurl) {
        this.wsurl = wsurl;
    }

    public static BinanceWsClient getBinanceWsClient(String wsurl) {
        if (null == binanceWsClient) {
            synchronized (BinanceWsClient.class) {
                if (null == binanceWsClient) {
                    binanceWsClient = new BinanceWsClient(wsurl);
                }
            }
        }
        return binanceWsClient;
    }

    public synchronized void connect() {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)//允许失败重试
                .readTimeout(10, TimeUnit.SECONDS)// 设置读取超时时间
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
        logger.error("t={}", t.getMessage(), t);
        logger.error("连接发生了异常,异常原因：{},getCause ={},getMessage={}", t, t.getCause(), t.getMessage());
        this.isClosed = false;
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        logger.error("断开服务器连接,状态码 code={},断开原因 reason={}", code, reason);
        this.isClosed=false;
        this.ws.close(code, reason);
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

    public Boolean getIsClosed() {
        return isClosed;
    }
}
