package com.hp.sh.expv3.bb.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hp.sh.expv3.bb.grab3rdData.component.ZbWsClient;
import com.hp.sh.expv3.bb.grab3rdData.pojo.BBSymbol;
import com.hp.sh.expv3.bb.grab3rdData.pojo.ZbTickerData;
import com.hp.sh.expv3.bb.grab3rdData.pojo.ZbResponseEntity;
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
public class GrabBb3rdDataByZbTask {
    private static final Logger logger = LoggerFactory.getLogger(GrabBb3rdDataByZbTask.class);

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(10000000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    @Value("${zb.wss.url}")
    private String zbWssUrl;

    @Value("${zb.https.url}")
    private String zbHttpsUrl;

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

    @Value("${grab.bb.3rdDataByZbWss.enable}")
    private Integer enableByWss;

    @Value("${grab.bb.3rdDataByZbHttps.enable}")
    private Integer enableByHttps;

    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;


    @PostConstruct
    public void startGrabBb3rdDataByZbWss() {
        if (enableByWss != 1) {
            return;
        }
        ZbWsClient client = new ZbWsClient(zbWssUrl);
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
                BlockingQueue<ZbResponseEntity> queue = ZbWsClient.getBlockingQueue();
                if (CollectionUtils.isEmpty(queue)) {
                    continue;
                }
                ZbResponseEntity tickerData = queue.poll();
                String hashKey = tickerData.getChannel().split("_")[0];
                String key = wssRedisKey + hashKey;
                logger.info("wssKey={}", key);
                ZbTickerData ticker = tickerData.getTicker();
                if (null != ticker) {
                    metadataDb5RedisUtil.set(key, ticker, 60);
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
                RestTemplate restTemplate = new RestTemplate();
                String symbol = bbSymbol.getSymbol().toLowerCase();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                HttpEntity<String> entity = new HttpEntity<String>(headers);
                String url = zbHttpsUrl + symbol;
                logger.info("https url={}", url);
                ResponseEntity<ZbResponseEntity> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, ZbResponseEntity.class);
                ZbResponseEntity tickerData = responseEntity.getBody();
                String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
                String key = httpsRedisKey + hashKey;
                logger.info("httpsKey={}", key);
                ZbTickerData ticker = tickerData.getTicker();
                if (null != ticker) {
                    metadataDb5RedisUtil.set(key, ticker, 60);
                }
            }
        }
    }





}


