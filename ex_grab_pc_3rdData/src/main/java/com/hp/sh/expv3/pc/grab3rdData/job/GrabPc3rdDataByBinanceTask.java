package com.hp.sh.expv3.pc.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hp.sh.expv3.pc.grab3rdData.component.BinanceWsClient;
import com.hp.sh.expv3.pc.grab3rdData.pojo.*;
import com.hp.sh.expv3.pc.grab3rdData.service.SupportBbGroupIdsJobService;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.hp.sh.expv3.config.redis.RedisUtil;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author BaiLiJun  on 2020/4/7
 */
@Component
public class GrabPc3rdDataByBinanceTask {
    private static final Logger logger = LoggerFactory.getLogger(GrabPc3rdDataByBinanceTask.class);

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
    private String binanceHttpsRedisKey;

//    @Autowired
//    @Qualifier("originaldataDb5RedisUtil")
//    private RedisUtil originaldataDb5RedisUtil;

    @Autowired
    @Qualifier("metadataDb5RedisUtil")
    private RedisUtil metadataDb5RedisUtil;

    @Value("${grab.pc.3rdDataByBinanceWss.enable}")
    private Integer enableByWss;

    @Value("${grab.pc.3rdDataByBinanceHttps.enable}")
    private Integer enableByHttps;

    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;

    @Scheduled(cron = "*/1 * * * * *")
    public void startGrabPc3rdDataByHttps() {
        if (enableByHttps != 1) {
            return;
        }

        List<PcSymbol> pcSymbolList = supportBbGroupIdsJobService.getSymbols();
        Map<String, String> binanceRedisKeysMap = new HashMap<>();
        Map<String, String> binanceUrlMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(pcSymbolList)) {
            for (PcSymbol pcSymbol : pcSymbolList) {
                String symbol = pcSymbol.getSymbol().split("_")[0]  + pcSymbol.getSymbol().split("_")[1];
                binanceRedisKeysMap.put(symbol, symbol);
                String url =binanceHttpsUrl+symbol+"&limit=1";
                binanceUrlMap.put(symbol,url);
            }
        }
        OkHttpClient client = new OkHttpClient();
        Map<String, String> map = new HashMap<>();
        for (String symbol : binanceUrlMap.keySet()) {
            Request request = new Request.Builder().get().url(binanceUrlMap.get(symbol)).build();
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                String string = response.body().string();
                 List<BinanceResponseDataByHttps> list = JSON.parseArray(string, BinanceResponseDataByHttps.class);
                if (!CollectionUtils.isEmpty(list)) {
                    for (BinanceResponseDataByHttps binanceResponseEntity : list) {
                        long timestamp = System.currentTimeMillis();
                        String key = binanceHttpsRedisKey + symbol;
                         BigDecimal price = binanceResponseEntity.getPrice();
                         String value = price + "&" + timestamp;
                        String lastValue = metadataDb5RedisUtil.get(key);
                        if(null==lastValue||"".equals(lastValue)){
                            map.put(key, value);
                            continue;
                        }
                        String[] split = lastValue.split("&");
                        BigDecimal lastPrice = new BigDecimal(split[0]);

                        String[] currentSplit = value.split("&");
                        BigDecimal currentPrice = new BigDecimal(currentSplit[0]);
                        if (split.length == 1) {
                            //当前价格跟最后更新价格不一样时， 才进行更新操作
                            if (currentPrice.compareTo(lastPrice) != 0) {
                                map.put(key, value);
                            }
                        } else if (split.length == 2) {
                            LocalDateTime now = Instant.ofEpochMilli(Long.parseLong(currentSplit[1])).atZone(ZoneOffset.systemDefault()).toLocalDateTime();
                            //当前价格跟最后更新价格不一样时，并且当前时间在15分钟内， 才进行更新操作
                            if (currentPrice.compareTo(lastPrice) != 0 && now.plusMinutes(15).compareTo(now) >= 0) {
                                map.put(key, value);
                            }
                        }
//                        map.put(key, value);
                    }
                }
                if(map.size()==3){
                    //批量保存
                    metadataDb5RedisUtil.mset(map);
//                    originaldataDb5RedisUtil.mset(map);
                }

            } catch (Exception e) {
                logger.error("通过https请求获取binance交易所最新成交价定时任务报错！，cause()={},message={}", e.getCause(), e.getMessage());

            }
        }






    }

    //    @PostConstruct
    public void startGrabPc3rdDataByWss() {
        if (enableByWss != 1) {
            return;
        }
        List<PcSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        Map<String, String> binanceRedisKeysMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (PcSymbol pcSymbol : bbSymbolList) {
                String binanceSymbol = pcSymbol.getSymbol().split("_")[0].toLowerCase() + pcSymbol.getSymbol().split("_")[1].toLowerCase();
                String url = binanceWssUrl + binanceSymbol + "@aggTrade";
                logger.info("url ={}", url);
                BinanceWsClient client = BinanceWsClient.getBinanceWsClient(url);
                client.connect();

                String key = pcSymbol.getSymbol().split("_")[0] + pcSymbol.getSymbol().split("_")[1];
                binanceRedisKeysMap.put(key, key);

            }
        }

        Map<String, String> map = new HashMap<>(16);
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

//                if (!CollectionUtils.isEmpty(bbSymbolLists)) {
//                    for (PcSymbol pcSymbol : bbSymbolLists) {
                for (BinanceResponseData responseData : list) {
                    String binanceBbSymbol = responseData.getS();
                    if (binanceRedisKeysMap.containsKey(binanceBbSymbol)) {
                        String key = wssRedisKey + binanceBbSymbol;
                        map.put(key, responseData.getP() + "");

                    }
                }
                if (map.size() == 1) {
                    metadataDb5RedisUtil.mset(map);
//                    originaldataDb5RedisUtil.mset(map);
                    map.clear();
                }
            }
        });
    }


    /**
     * WS重连策略
     */
//    @Scheduled(cron = "*/59 * * * * *")
    public void retryConnection() {
        BinanceWsClient client = BinanceWsClient.getBinanceWsClient(binanceWssUrl);
        Boolean isClosed = client.getIsClosed();
        if (!isClosed) {
            client.close();
            startGrabPc3rdDataByWss();
        }
    }

//    @Scheduled(cron = "*/1 * * * * *")
//    public void startGrabPc3rdDataByHttps() {
//        if (enableByHttps != 1) {
//            return;
//        }
//        List<PcSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
//        if (!CollectionUtils.isEmpty(bbSymbolList)) {
//            for (PcSymbol pcSymbol : bbSymbolList) {
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder().get().url(binanceHttpsUrl).build();
//                Call call = client.newCall(request);
//                try {
//                    Response response = call.execute();
//                    String string = response.body().string();
//                    String expymbol = pcSymbol.getSymbol().split("_")[0] + pcSymbol.getSymbol().split("_")[1];
//                    final List<Map> list = JSON.parseArray(string, Map.class);
//                    for (Map map : list) {
//                        String binanceSymbol = (String) map.get("symbol");
//                        if (expymbol.equals(binanceSymbol)) {
//                            String key = httpsRedisKey + binanceSymbol;
//                            logger.info("binance httpsRedisKey={}", key);
//                            metadataDb5RedisUtil.set(key, JSON.toJSONString(map), 60);
//                        }
//                    }
//                } catch (Exception e) {
////                    e.printStackTrace();
//                    continue;
//                }
//            }
//        }
//    }


}


