package com.hp.sh.expv3.bb.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hp.sh.expv3.bb.grab3rdData.component.ZbWsClient;
import com.hp.sh.expv3.bb.grab3rdData.pojo.BBSymbol;
import com.hp.sh.expv3.bb.grab3rdData.pojo.OkResponseEntity;
import com.hp.sh.expv3.bb.grab3rdData.pojo.ZbTickerData;
import com.hp.sh.expv3.bb.grab3rdData.pojo.ZbResponseEntity;
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
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.commands.JedisCommands;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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
            new LinkedBlockingQueue<Runnable>(1024),
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
    @Qualifier("originaldataDb5RedisUtil")
    private RedisUtil originaldataDb5RedisUtil;

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
        ZbWsClient client = ZbWsClient.getZbWsClient(zbWssUrl);
        client.connect();
        Map data = new TreeMap();
        data.put("event", "addChannel");
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        Map<String, String> zbRedisKeysMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                String[] symbols = bbSymbol.getSymbol().toLowerCase().split("_");
                String key = symbols[0] + symbols[1];
                zbRedisKeysMap.put(key, key);
                String channel = key + "_ticker";
                data.put("channel", channel);
                client.send(JSONObject.toJSONString(data));
            }
        }

        Map<String, String> map = new HashMap<>(64);

        threadPool.execute(() -> {
            while (true) {
                BlockingQueue<ZbResponseEntity> queue = ZbWsClient.getBlockingQueue();
                if (CollectionUtils.isEmpty(queue)) {
                    continue;
                }

                ZbResponseEntity tickerData = queue.poll();
                String hashKey = tickerData.getChannel().split("_")[0];
                String key = wssRedisKey + hashKey;
                ZbTickerData ticker = tickerData.getTicker();
                if (null != ticker&&zbRedisKeysMap.containsKey(hashKey)) {
                    String value = ticker.getLast() + "";
                    map.put(key, value);
                    if (map.size() == 8) {
                        originaldataDb5RedisUtil.mset(map);
                        metadataDb5RedisUtil.mset(map);
                        map.clear();
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
        Map<String, String> zbRedisKeysMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                String symbol = bbSymbol.getSymbol().toLowerCase();
                String key = symbol.split("_")[0] + symbol.split("_")[1];
                zbRedisKeysMap.put(key, key);
            }
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(zbHttpsUrl).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String string = response.body().string();
            JSONObject jsonObject = JSON.parseObject(string);

            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                Object entryValue = entry.getValue();
                ZbTickerData zbTickerData = JSON.parseObject(entryValue.toString(), ZbTickerData.class);
                if (zbRedisKeysMap.containsKey(entry.getKey())) {
                    String key = httpsRedisKey + entry.getKey();
                    String value = zbTickerData.getLast() + "";
                    if (null != value || !"".equals(value)) {
                        map.put(key, value);
                    }
                }
            }

            //批量保存
            metadataDb5RedisUtil.mset(map);
            originaldataDb5RedisUtil.mset(map);
        } catch (Exception e) {
//            e.printStackTrace();
        }


    }


    @Scheduled(cron = "*/59 * * * * *")
    public void retryConnection() {
        ZbWsClient client = ZbWsClient.getZbWsClient(zbWssUrl);
        Boolean isClosed = client.getIsClosed();
        if (!isClosed) {
            client.close();
            startGrabBb3rdDataByZbWss();
        }
    }


}


