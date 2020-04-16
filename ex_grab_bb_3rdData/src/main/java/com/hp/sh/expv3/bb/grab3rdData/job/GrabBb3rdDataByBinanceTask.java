package com.hp.sh.expv3.bb.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hp.sh.expv3.bb.grab3rdData.component.BinanceWsClient;
import com.hp.sh.expv3.bb.grab3rdData.component.ZbWsClient;
import com.hp.sh.expv3.bb.grab3rdData.pojo.*;
import com.hp.sh.expv3.bb.grab3rdData.service.SupportBbGroupIdsJobService;
import com.hp.sh.expv3.config.redis.RedisUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Pipeline;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author BaiLiJun  on 2020/4/7
 */
@Component
public class GrabBb3rdDataByBinanceTask {
    private static final Logger logger = LoggerFactory.getLogger(GrabBb3rdDataByBinanceTask.class);

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    @Value("${binance.wss.url}")
    private String binanceWssUrl;

    @Value("${binance.https.url}")
    private String binanceHttpsUrl;

    @Value("${binance.wss.redisKey.prefix}")
    private String wssRedisKey;

    @Value("${binance.https.redisKey.prefix}")
    private String httpsRedisKey;

    @Value("${bb.trade.symbols}")
    private String symbols;

    @Value("${bb.trade.bbGroupIds}")
    private Integer bbGroupId;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Autowired
    @Qualifier("metadataDb5RedisUtil")
    private RedisUtil metadataDb5RedisUtil;

    @Value("${grab.bb.3rdDataByBinanceWss.enable}")
    private Integer enableByWss;

    @Value("${grab.bb.3rdDataByBinanceHttps.enable}")
    private Integer enableByHttps;

    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;


    @PostConstruct
    public void startGrabBb3rdDataByWss() {
        if (enableByWss != 1) {
            return;
        }
        BinanceWsClient client = BinanceWsClient.getBinanceWsClient(binanceWssUrl);
        client.connect();

        Map<String, String> map = new HashMap<>(64);
        threadPool.execute(() -> {
            while (true) {
                BlockingQueue<BinanceResponseEntity> queue = BinanceWsClient.getBlockingQueue();
                if (CollectionUtils.isEmpty(queue)) {
                    continue;
                }
                BinanceResponseEntity tickerData = queue.poll();
                List<BinanceResponseData> list = tickerData.getData();
                if (CollectionUtils.isEmpty(list)) {
                    continue;
                }
                List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
                if (!CollectionUtils.isEmpty(bbSymbolList)) {
                    for (BBSymbol bbSymbol : bbSymbolList) {
                        for (BinanceResponseData responseData : list) {
                            String expBbSymbol = bbSymbol.getSymbol().split("_")[0] + bbSymbol.getSymbol().split("_")[1];
                            String binanceBbSymbol = responseData.getS();
                            if (expBbSymbol.equals(binanceBbSymbol)) {
                                String key = wssRedisKey + binanceBbSymbol;
                                map.put(key,responseData.getC()+"");
                                if(map.size()==4){
                                    metadataDb5RedisUtil.mset(map);
                                    map.clear();
                                }
//                                metadataDb5RedisUtil.set(key, responseData, 900);
//                                String s = metadataDb5RedisUtil.get(key);
//                                BinanceResponseData binanceResponseData = JSON.parseObject(s, BinanceResponseData.class);
//                                if (null == binanceResponseData) {
//                                    metadataDb5RedisUtil.set(key, responseData, 900);
//                                } else if (null != binanceResponseData && binanceResponseData.getC().compareTo(responseData.getC()) != 0) {
//                                    metadataDb5RedisUtil.set(key, responseData, 900);
//                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Scheduled(cron = "*/59 * * * * *")
    public void retryConnection() {
        BinanceWsClient client = BinanceWsClient.getBinanceWsClient(binanceWssUrl);
        Boolean isClosed = client.getIsClosed();
        if (!isClosed) {
            client.close();
            startGrabBb3rdDataByWss();
        }
    }


    @Scheduled(cron = "*/1 * * * * *")
    public void startGrabBb3rdDataByHttps() {
        if (enableByHttps != 1) {
            return;
        }
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        Map<String, String> binanceRedisKeysMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                String key = bbSymbol.getSymbol().split("_")[0] + bbSymbol.getSymbol().split("_")[1];
                binanceRedisKeysMap.put(key, key);
            }
        }
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            Map<String, String> map = new HashMap<>();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().get().url(binanceHttpsUrl).build();
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                String string = response.body().string();

                final List<Map> list = JSON.parseArray(string, Map.class);
                for (Map data : list) {
                    String binanceSymbol = (String) data.get("symbol");
                    if (binanceRedisKeysMap.containsKey(binanceSymbol)) {
                        String key = httpsRedisKey + binanceSymbol;
                        String value = (String) data.get("price");
                        if (null != value || !"".equals(value)) {
                            map.put(key, value);
                        }
                    }
                }
                //批量保存
                metadataDb5RedisUtil.mset(map);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("通过https请求获取binance交易所最新成交价定时任务报错！，cause()={},message={}", e.getCause(), e.getMessage());
            }
        }


    }


}


