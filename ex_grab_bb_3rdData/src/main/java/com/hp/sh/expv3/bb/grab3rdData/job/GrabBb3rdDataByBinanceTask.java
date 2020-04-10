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
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
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
            new LinkedBlockingQueue<Runnable>(10000000),
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
    public void startGrabBb3rdDataByZbWss() {
        if (enableByWss != 1) {
            return;
        }
        BinanceWsClient client = new BinanceWsClient(binanceWssUrl);
        client.connect();

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
                                logger.info("binance wssKey={}", key);
                                metadataDb5RedisUtil.set(key, responseData, 60);
                            }
                        }
                    }
                }
            }
        });
    }


    @Scheduled(cron = "*/1 * * * * *")
    public void startGrabBb3rdDataByZbHttps() {
        if (enableByHttps != 1) {
            return;
        }
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().get().url(binanceHttpsUrl).build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    String string = response.body().string();
                    String expymbol = bbSymbol.getSymbol().split("_")[0] + bbSymbol.getSymbol().split("_")[1];
                    final List<Map> list = JSON.parseArray(string, Map.class);
                    for (Map map : list) {
                        String binanceSymbol = (String) map.get("symbol");
                        if (expymbol.equals(binanceSymbol)) {
                            String key = httpsRedisKey + binanceSymbol;
                            logger.info("binance httpsRedisKey={}", key);
                            metadataDb5RedisUtil.set(key, JSON.toJSONString(map), 60);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }


}


