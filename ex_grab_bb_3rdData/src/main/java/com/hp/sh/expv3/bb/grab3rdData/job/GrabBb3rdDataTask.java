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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
    private String url;

    @Value("${zb.wss.redisKey.prefix}")
    private String redisKey;

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
        WsClient client = new WsClient(url);
        client.connect();
        Map data = new TreeMap();
        data.put("event", "addChannel");
//        data.put("channel", "btcusdt_ticker");
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
//                        String asset = bbSymbol.getAsset().toLowerCase();
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
                Map<String, Ticker> map = new HashMap<>();
                map.put(hashKey, tickerData.getTicker());
                String key = redisKey + hashKey;
                logger.info("key={},hashKey={}", key, hashKey);
                metadataDb5RedisUtil.hmset(key, map);
            }
        });
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void startGrabBb3rdDataByHttps() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                String[] symbols = bbSymbol.getSymbol().toLowerCase().split("_");
                String channel = symbols[0] + symbols[1] + "_ticker";

                logger.info("channel={}", channel);


            }
        }

    }

}
