package com.hp.sh.expv3.bb.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hp.sh.expv3.bb.grab3rdData.component.WsClient;
import com.hp.sh.expv3.bb.grab3rdData.pojo.BBSymbol;
import com.hp.sh.expv3.bb.grab3rdData.pojo.Ticker;
import com.hp.sh.expv3.bb.grab3rdData.pojo.TickerData;
import com.hp.sh.expv3.bb.grab3rdData.service.SupportBbGroupIdsJobService;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;

/**
 * @author BaiLiJun  on 2020/4/7
 */
@Component
public class GrabBb3rdDataTask {
    private static final Logger logger = LoggerFactory.getLogger(WsClient.class);

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(10000000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    @Value("${zb.wss.url}")
    private String wssUrl;

    @Value("${zb.https.url}")
    private String httpsUrl;

    @Value("${zb.wss.redisKey.prefix}")
    private String wssRedisKey;

    @Value("${zb.https.redisKey.prefix}")
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


    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;


        @PostConstruct
    public void startGrabBb3rdDataByCss() {
        WsClient client = new WsClient(wssUrl);
        client.connect();
        Map data = new TreeMap();
        data.put("event", "addChannel");
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                String[] symbols = bbSymbol.getSymbol().toLowerCase().split("_");
                String channel = symbols[0] + symbols[1] + "_ticker";
                logger.info("channel={}", channel);
                data.put("channel", channel);
                client.send(JSONObject.toJSONString(data));
            }
        }

        threadPool.execute(() -> {
            while (true) {
                BlockingQueue<TickerData> queue = WsClient.getBlockingQueue();
                if (CollectionUtils.isEmpty(queue)) {
                    continue;
                }
                TickerData tickerData = queue.poll();
                String hashKey = tickerData.getChannel().split("_")[0];
//                Map<String, Ticker> map = new HashMap<>();
//                map.put(hashKey, tickerData.getTicker());
                String key = wssRedisKey + hashKey;
//                logger.info("wssKey={},hashKey={}", key, hashKey);
//                metadataDb5RedisUtil.hmset(key, map);
                logger.info("wssKey={}", key);
                Ticker ticker = tickerData.getTicker();
                if (null != ticker) {
                    metadataDb5RedisUtil.set(key, ticker, 60);
                }
            }
        });
    }


        @Scheduled(cron = "*/1 * * * * *")
    public void startGrabBb3rdDataByHttps() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                RestTemplate restTemplate = new RestTemplate();
                String symbol = bbSymbol.getSymbol().toLowerCase();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                HttpEntity<String> entity = new HttpEntity<String>(headers);
                String url = httpsUrl + symbol;
                logger.info("https url={}", url);
                ResponseEntity<TickerData> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, TickerData.class);
                TickerData tickerData = responseEntity.getBody();
                String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
                String key = httpsRedisKey + hashKey;
//                Map<String, Ticker> map = new HashMap<>();
//                map.put(hashKey, tickerData.getTicker());
//                logger.info("httpsKey={},hashKey={}", key, hashKey);
//                metadataDb5RedisUtil.hmset(key, map);
                logger.info("httpsKey={}", key);
                Ticker ticker = tickerData.getTicker();
                if (null != ticker) {
                    metadataDb5RedisUtil.set(key, ticker, 60);
                }
            }
        }
    }


    @Scheduled(cron = "*/1 * * * * *")
    public void merge() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                String symbol = bbSymbol.getSymbol().toLowerCase();
                String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
                String httpsKey = httpsRedisKey + hashKey;
                String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
                Ticker httpsTicker = JSON.parseObject(strHttpsTicker, Ticker.class);

                String wssKey = httpsRedisKey + hashKey;
                String strWssTicker = metadataDb5RedisUtil.get(wssKey);
                Ticker wssTicker = JSON.parseObject(strWssTicker, Ticker.class);
                if (null == httpsTicker && null == wssTicker) {
                    continue;
                }
                BigDecimal httpLast = BigDecimal.ZERO;
                BigDecimal wssLast = BigDecimal.ZERO;
                if (null != httpsTicker) {
                    httpLast = httpsTicker.getLast();
                } else if (null != httpsTicker) {
                    wssLast = wssTicker.getLast();
                }

                BigDecimal avgLastPrice = httpLast.add(wssLast).divide(new BigDecimal(2), 4, RoundingMode.DOWN);
                logger.info("{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
                String key = "bb:" + hashKey + "lastPrice";
                HashMap<String, BigDecimal> map = new HashMap<>();
                map.put(hashKey, avgLastPrice);
                metadataDb5RedisUtil.hmset(key, map);
            }
        }
    }


}


