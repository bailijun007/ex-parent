package com.hp.sh.expv3.pc.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pc.grab3rdData.pojo.BBSymbol;
import com.hp.sh.expv3.pc.grab3rdData.pojo.BinanceResponseData;
import com.hp.sh.expv3.pc.grab3rdData.pojo.OkResponseEntity;
import com.hp.sh.expv3.pc.grab3rdData.pojo.PcSymbol;
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
    private static final Logger logger = LoggerFactory.getLogger(GrabPc3rdDataByOkTask.class);

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

    @Value("${grab.pc.3rdDataByOkHttps.enable}")
    private Integer enableByHttps;

    @Value("${ok.https.redisKey.prefix}")
    private String okHttpsRedisKey;

    @Autowired
    @Qualifier("metadataDb5RedisUtil")
    private RedisUtil metadataDb5RedisUtil;

    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;


    @PostConstruct
    public void startGrabPc3rdDataByOkHttps() {
        if (enableByHttps != 1) {
            return;
        }
        threadPool.execute(() -> {
            while (true) {
                List<PcSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().get().url(okHttpsUrl).build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    String string = response.body().string();
                    List<OkResponseEntity> list = JSON.parseArray(string, OkResponseEntity.class);
                    if (!CollectionUtils.isEmpty(bbSymbolList)) {
                        for (PcSymbol pcSymbol : bbSymbolList) {
                            String symbol = pcSymbol.getSymbol().split("_")[0] + "-" + pcSymbol.getSymbol().split("_")[1];
                            for (OkResponseEntity okResponseEntity : list) {
                                String okSymbol = okResponseEntity.getInstrument_id();
                                okSymbol = okSymbol.split("-")[0] + "-" + okSymbol.split("-")[1];
                                if (okSymbol.endsWith("USD")) {
                                    okSymbol = okSymbol + "T";
                                }
                                if (okSymbol.equals(symbol)) {
                                    String key = okHttpsRedisKey + okSymbol;
                                    logger.info("okHttpsRedisKey={}", key);
//                                    metadataDb5RedisUtil.set(key, okResponseEntity, 60);
                                    String s = metadataDb5RedisUtil.get(key);
                                    OkResponseEntity okResponseData = JSON.parseObject(s, OkResponseEntity.class);
                                    if (null == okResponseData) {
                                        metadataDb5RedisUtil.set(key, okResponseEntity, 900);
                                    }else if (null != okResponseData && okResponseData.getLast().compareTo(okResponseEntity.getLast()) != 0) {
                                        metadataDb5RedisUtil.set(key, okResponseEntity, 900);
                                    }
                                }
                            }
                            TimeUnit.SECONDS.sleep(1);
                        }
                    }
                } catch (Exception e) {
                    logger.error("通过https请求获取ok交易所最新成交价定时任务报错！，cause()={},message={}",e.getCause(),e.getMessage());

//                            e.printStackTrace();
                    continue;
                }
            }
        });

    }


}


