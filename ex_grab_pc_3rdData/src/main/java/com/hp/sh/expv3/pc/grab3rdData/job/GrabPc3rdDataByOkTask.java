package com.hp.sh.expv3.pc.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pc.grab3rdData.component.ZbWsClient;
import com.hp.sh.expv3.pc.grab3rdData.pojo.BBSymbol;
import com.hp.sh.expv3.pc.grab3rdData.pojo.OkResponseEntity;
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
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.hp.sh.expv3.config.redis.RedisUtil;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author BaiLiJun  on 2020/4/7
 */
@Component
public class GrabPc3rdDataByOkTask {
    private static final Logger logger = LoggerFactory.getLogger(ZbWsClient.class);

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

    @Value("${grab.bb.3rdDataByOkHttps.enable}")
    private Integer enableByHttps;

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


    //    @Scheduled(cron = "*/1 * * * * *")
    @PostConstruct
    public void startGrabBb3rdDataByOkHttps() {
        if (enableByHttps != 1) {
            return;
        }
        threadPool.execute(() -> {
            while (true) {
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
                            String symbol = bbSymbol.getSymbol().split("_")[0] + "-" + bbSymbol.getSymbol().split("_")[1];
                            for (OkResponseEntity okResponseEntity : list) {
                                String okSymbol = okResponseEntity.getProduct_id();
                                if (okSymbol.equals(symbol)) {
                                    String key = okHttpsRedisKey + okSymbol;
                                    logger.info("okHttpsRedisKey={}", key);
                                    metadataDb5RedisUtil.set(key, okResponseEntity, 60);
                                }
                            }
                            TimeUnit.SECONDS.sleep(1);
                        } catch (Exception e) {
//                            e.printStackTrace();
                            continue;
                        }
                    }
                }
            }
        });

    }


}


