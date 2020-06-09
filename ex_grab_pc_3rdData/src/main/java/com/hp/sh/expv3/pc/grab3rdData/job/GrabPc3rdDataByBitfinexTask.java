package com.hp.sh.expv3.pc.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hp.sh.expv3.pc.grab3rdData.pojo.BBSymbol;
import com.hp.sh.expv3.pc.grab3rdData.pojo.BitfinexResponseEntity;
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
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author BaiLiJun  on 2020/4/7
 */
@Component
public class GrabPc3rdDataByBitfinexTask {
    private static final Logger logger = LoggerFactory.getLogger(GrabPc3rdDataByBitfinexTask.class);

    @Value("${bitfinex.https.url}")
    private String bitfinexHttpsUrl;


    @Value("${bitfinex.https.redisKey.prefix}")
    private String bitfinexHttpsRedisKey;


    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Autowired
    @Qualifier("metadataDb5RedisUtil")
    private RedisUtil metadataDb5RedisUtil;

//    @Autowired
//    @Qualifier("originaldataDb5RedisUtil")
//    private RedisUtil originaldataDb5RedisUtil;

    @Value("${grab.pc.3rdDataByBitfinexHttps.enable}")
    private Integer enableByHttps;

    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;


    @Scheduled(cron = "*/1 * * * * *")
    public void startGrabPc3rdDataByBitfinexHttps() {
        if (enableByHttps != 1) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        List<PcSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            OkHttpClient client = new OkHttpClient();
            for (PcSymbol pcSymbol : bbSymbolList) {
                String asset = pcSymbol.getAsset();
                String url = bitfinexHttpsUrl + asset + "USD/hist?limit=1";
                Request request = new Request.Builder().get().url(url).build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    String string = response.body().string();
                    List<String> jsonArrays = JSON.parseArray(string, String.class);
                    for (String ja : jsonArrays) {
                        JSONArray jsonArray = JSON.parseArray(ja);
                        BigDecimal lastPrice = jsonArray.getBigDecimal(3);
                        long timestamp = System.currentTimeMillis();
                        String key = bitfinexHttpsRedisKey + asset+"USDT";
                         String value = lastPrice + "&" + timestamp;
                        String lastValue = metadataDb5RedisUtil.get(key);
                        if(null==lastValue||"".equals(lastValue)){
                            map.put(key, value);
                            continue;
                        }
                        String[] split = lastValue.split("&");
                        BigDecimal lastPriceValue = new BigDecimal(split[0]);

                        String[] currentSplit = value.split("&");
                        BigDecimal currentPrice = new BigDecimal(currentSplit[0]);
                        if (split.length == 1) {
                            //当前价格跟最后更新价格不一样时， 才进行更新操作
                            if (currentPrice.compareTo(lastPriceValue) != 0) {
                                map.put(key, value);
                            }
                        } else if (split.length == 2) {
                            LocalDateTime now = Instant.ofEpochMilli(Long.parseLong(currentSplit[1])).atZone(ZoneOffset.systemDefault()).toLocalDateTime();
                            //当前价格跟最后更新价格不一样时，并且当前时间在15分钟内， 才进行更新操作
                            if (currentPrice.compareTo(lastPriceValue) != 0 && now.plusMinutes(15).compareTo(now) >= 0) {
                                map.put(key, value);
                            }
                        }
//                        map.put(key, value);
                    }

                    if(map.size()==1){
                        //批量保存
                        metadataDb5RedisUtil.mset(map);
//                        originaldataDb5RedisUtil.mset(map);
                    }

                } catch (Exception e) {
                    continue;
                }
            }
        }
    }


}


