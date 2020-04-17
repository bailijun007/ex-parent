package com.hp.sh.expv3.bb.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hp.sh.expv3.bb.grab3rdData.component.ZbWsClient;
import com.hp.sh.expv3.bb.grab3rdData.pojo.BBSymbol;
import com.hp.sh.expv3.bb.grab3rdData.pojo.BitfinexResponseEntity;
import com.hp.sh.expv3.bb.grab3rdData.pojo.OkResponseEntity;
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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Pipeline;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author BaiLiJun  on 2020/4/7
 */
@Component
public class GrabBb3rdDataByBitfinexTask {
    private static final Logger logger = LoggerFactory.getLogger(ZbWsClient.class);

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );


    @Value("${bitfinex.https.url}")
    private String bitfinexHttpsUrl;


    @Value("${bitfinex.https.redisKey.prefix}")
    private String bitfinexHttpsRedisKey;

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

    @Value("${grab.bb.3rdDataByBitfinexHttps.enable}")
    private Integer enableByHttps;

    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;


    //    @Scheduled(cron = "*/1 * * * * *")
    @PostConstruct
    public void startGrabBb3rdDataByBitfinexHttps() {
        if (enableByHttps != 1) {
            return;
        }
        threadPool.execute(() -> {
            while (true) {
                List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
                Map<String, String> bitfinexRedisKeysMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(bbSymbolList)) {
                    for (BBSymbol bbSymbol : bbSymbolList) {
                        String symbol = bbSymbol.getSymbol().split("_")[0] + bbSymbol.getSymbol().split("_")[1];
                        bitfinexRedisKeysMap.put(symbol, symbol);
                    }
                }
                if (!CollectionUtils.isEmpty(bbSymbolList)) {
                    Map<String, String> map = new HashMap<>();
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().get().url(bitfinexHttpsUrl).build();
                    Call call = client.newCall(request);
                    try {
                        Response response = call.execute();
                        String string = response.body().string();
                        List<String> jsonArrays = JSON.parseArray(string, String.class);
                        for (String ja : jsonArrays) {
                            JSONArray jsonArray = JSON.parseArray(ja);
                            String bitfinexSymbol = jsonArray.getString(0);
//                                BigDecimal bid = jsonArray.getBigDecimal(1);
//                                BigDecimal bidSize = jsonArray.getBigDecimal(2);
//                                BigDecimal ask = jsonArray.getBigDecimal(3);
//                                BigDecimal askSize = jsonArray.getBigDecimal(4);
//                                BigDecimal dailyChange = jsonArray.getBigDecimal(5);
//                                BigDecimal dailyChangeRelative = jsonArray.getBigDecimal(6);
                            BigDecimal lastPrice = jsonArray.getBigDecimal(7);
//                                BigDecimal volume = jsonArray.getBigDecimal(8);
//                                BigDecimal high = jsonArray.getBigDecimal(9);
//                                BigDecimal low = jsonArray.getBigDecimal(10);
//                                BitfinexResponseEntity bitfinexResponseEntity = new BitfinexResponseEntity(bitfinexSymbol, bid, bidSize, ask, askSize, dailyChange, dailyChangeRelative, lastPrice, volume, high, low);
//                                String symbol = bbSymbol.getSymbol().split("_")[0] + bbSymbol.getSymbol().split("_")[1];
                            if (bitfinexSymbol.endsWith("USD")) {
                                bitfinexSymbol = bitfinexSymbol.substring(1, bitfinexSymbol.length()).concat("T");
                            }
                            if (bitfinexRedisKeysMap.containsKey(bitfinexSymbol)) {
                                String key = bitfinexHttpsRedisKey + bitfinexSymbol;
                                String value = lastPrice + "";
                                if (null != value || !"".equals(value)) {
                                    map.put(key, value);
                                }
                            }
                        }

                        //批量保存
                        metadataDb5RedisUtil.mset(map);
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        logger.error("通过https请求获取bitfinex交易所最新成交价定时任务报错！，cause()={},message={}", e.getCause(), e.getMessage());
                        continue;
                    }
                }
            }
        });

    }


}


