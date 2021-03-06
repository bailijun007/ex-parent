package com.hp.sh.expv3.bb.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.grab3rdData.component.ZbWsClient;
import com.hp.sh.expv3.bb.grab3rdData.pojo.BBSymbol;
import com.hp.sh.expv3.bb.grab3rdData.pojo.OkResponseEntity;
import com.hp.sh.expv3.bb.grab3rdData.pojo.ZbTickerData;
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
public class GrabBb3rdDataByOkTask {
    private static final Logger logger = LoggerFactory.getLogger(ZbWsClient.class);

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );


    @Value("${ok.https.url}")
    private String okHttpsUrl;

    @Value("${grab.bb.3rdDataByOkHttps.enable}")
    private Integer enableByHttps;

    @Value("${ok.https.redisKey.prefix}")
    private String okHttpsRedisKey;

    @Value("${bb.trade.symbols}")
    private String symbols;


//    @Autowired
//    @Qualifier("originaldataDb5RedisUtil")
//    private RedisUtil originaldataDb5RedisUtil;

    @Autowired
    @Qualifier("metadataDb5RedisUtil")
    private RedisUtil metadataDb5RedisUtil;


    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;


    //    @Scheduled(cron = "*/1 * * * * *")
    @PostConstruct
    public void startGrabBb3rdDataByOkHttps() {
        if (enableByHttps != 1) {
            return;
        }
        threadPool.execute(() -> {
            while (true) {
                List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
                Map<String, String> okRedisKeysMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(bbSymbolList)) {
                    for (BBSymbol bbSymbol : bbSymbolList) {
                        String symbol = bbSymbol.getSymbol().split("_")[0] + "-" + bbSymbol.getSymbol().split("_")[1];
                        okRedisKeysMap.put(symbol, symbol);
                    }
                }
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().get().url(okHttpsUrl).build();
                Call call = client.newCall(request);
                try {
                    TimeUnit.SECONDS.sleep(1);
                    Response response = call.execute();
                    String string = response.body().string();
                    List<OkResponseEntity> list = JSON.parseArray(string, OkResponseEntity.class);

                    Map<String, String> map = new HashMap<>();
                    if (!CollectionUtils.isEmpty(list)) {
                        for (OkResponseEntity okResponseEntity : list) {
                            String okSymbol = okResponseEntity.getProduct_id();
                            if (okRedisKeysMap.containsKey(okSymbol)) {
                                String key = okHttpsRedisKey + okSymbol;
                                long timestamp = System.currentTimeMillis();
                                String value = okResponseEntity.getLast() + "&" + timestamp;
                                if (null != value || !"".equals(value)) {
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
//                                    map.put(key, value);
                                }
                            }
                        }
                        //批量保存
                        metadataDb5RedisUtil.mset(map);
//                        originaldataDb5RedisUtil.mset(map);

                    }
                } catch (Exception e) {
                    logger.error("通过https请求获取ok交易所最新成交价定时任务报错！，cause()={},message={}", e.getCause(), e.getMessage());
                    continue;
                }
            }
        });

    }


}


