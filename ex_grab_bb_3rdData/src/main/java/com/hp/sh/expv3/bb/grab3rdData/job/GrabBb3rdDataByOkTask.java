package com.hp.sh.expv3.bb.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.grab3rdData.component.WsClient;
import com.hp.sh.expv3.bb.grab3rdData.pojo.BBSymbol;
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
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author BaiLiJun  on 2020/4/7
 */
@Component
public class GrabBb3rdDataByOkTask {
    private static final Logger logger = LoggerFactory.getLogger(WsClient.class);

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(10000000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );


    @Value("${ok.https.url}")
    private String okHttpsUrl;


    @Value("${ok.https.redisKey.prefix}")
    private String okHttpsRedisKey;

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


    @Scheduled(cron = "*/1 * * * * *")
    public void startGrabBb3rdDataByOkHttps() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().get().url(okHttpsUrl).build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    String string = response.body().string();
                    List<OkResponseEntity> list = JSON.parseArray(string, OkResponseEntity.class);
                    String symbol =  bbSymbol.getSymbol().split("_")[0]+"-"+bbSymbol.getSymbol().split("_")[1];
                    for (OkResponseEntity okResponseEntity : list) {
                        if (okResponseEntity.getProduct_id().equals(symbol)) {
                            String key = okHttpsRedisKey + symbol;
                            logger.info("okHttpsRedisKey={}", key);
                            metadataDb5RedisUtil.set(key, okResponseEntity, 60);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}


