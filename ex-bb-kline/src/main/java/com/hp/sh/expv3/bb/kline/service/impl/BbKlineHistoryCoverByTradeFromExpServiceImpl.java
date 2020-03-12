package com.hp.sh.expv3.bb.kline.service.impl;

import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.service.BbKlineHistoryCoverByTradeFromExpService;
import com.hp.sh.expv3.bb.kline.util.BBKlineUtil;
import com.hp.sh.expv3.bb.kline.util.BbKlineRedisKeyUtil;
import com.hp.sh.expv3.bb.kline.util.StringReplaceUtil;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Tuple;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/12
 */
@Service
public class BbKlineHistoryCoverByTradeFromExpServiceImpl implements BbKlineHistoryCoverByTradeFromExpService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${bb.kline.bbGroupIds}")
    private Set<Integer> supportBbGroupIds;

    @Value("${bb.kline.expHistoryBatchSize}")
    private Integer expHistoryBatchSize;

    @Value("${bb.kline.supportFrequenceString}")
    private String supportFrequenceString;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Autowired
    @Qualifier("bbKlineOngoingRedisUtil")
    private RedisUtil bbKlineExpHistoryRedisUtil;

    @Value("${bb.kline.notifyUpdate}")
    private String bbKlineFromExpUpdateEvent;

    @Value("${bb.kline.updateEventPattern}")
    private String updateEventPattern;

    @Value("${bb.kline}")
    private String bbKlinePattern;


    @Value("${bb.kline.expHistory.enable}")
    private int bbKlineExpHistoryEnable;


    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            2,
            Runtime.getRuntime().availableProcessors() + 1,
            2L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Scheduled(cron = "*/1 * * * * *")
    public void execute(){
        if (1 != bbKlineExpHistoryEnable) {
            return;
        }else {
            threadPool.execute(()->updateKlineByExpHistory());
        }
    }

    public void updateKlineByExpHistory() {

        if (1 != bbKlineExpHistoryEnable) {
            return;
        }

        List<BBSymbol> bbSymbols = listSymbol();
        List<BBSymbol> targetBbSymbols = filterBbSymbols(bbSymbols);

        for (BBSymbol bbSymbol : targetBbSymbols) {

            final String asset = bbSymbol.getAsset();
            final String symbol = bbSymbol.getSymbol();
            int freq = 1;
            //监听通知消息
            String bbKlineFromExpUpdateKey = bbKlineFromExpUpdateKey(asset, symbol, freq);
            Long[] minAndMaxMs = listListeningTask(bbKlineFromExpUpdateKey);
            if (null == minAndMaxMs) {

            } else {
                List<BBKLine> klines = listBbKline(asset, symbol, minAndMaxMs[0], minAndMaxMs[1], freq);
                if (null == klines || klines.isEmpty()) {
                    continue;
                }
                coverData(asset, symbol, minAndMaxMs[0], minAndMaxMs[1], freq, klines);
                notifyKlineUpdate(asset, symbol, minAndMaxMs[0], minAndMaxMs[1], freq, klines);
            }
        }
    }

    

    /**
     * 覆盖
     *
     * @param asset
     * @param symbol
     * @param klines
     * @param freq
     */
    private void coverData(String asset, String symbol, Long minMs, Long maxMs, int freq, List<BBKLine> klines) {
        final String klineDataRedisKey = BbKlineRedisKeyUtil.buildKlineDataRedisKey(bbKlinePattern, asset, symbol, freq);
        //删除老数据
        bbKlineExpHistoryRedisUtil.zremrangeByScore(klineDataRedisKey, minMs, maxMs);
        //新增新数据
        HashMap<String, Double> scoreMembers = new HashMap<String, Double>();
        for (BBKLine kline : klines) {
            final String data = BBKlineUtil.kline2ArrayData(kline);
            scoreMembers.put(data, Long.valueOf(kline.getMs()).doubleValue());
        }
        bbKlineExpHistoryRedisUtil.zadd(klineDataRedisKey, scoreMembers);
    }

    /**
     * 批量更新
     *
     * @param asset
     * @param symbol
     * @param klines
     * @param freq
     */
    private void notifyKlineUpdate(String asset, String symbol, Long minMs, Long maxMs, int freq, List<BBKLine> klines) {
        String key = BbKlineRedisKeyUtil.buildKlineUpdateEventRedisKey(updateEventPattern, asset, symbol, freq);
        HashMap<String, Double> scoreMembers = new HashMap<String, Double>();
        for (BBKLine kline : klines) {
            final String member = BbKlineRedisKeyUtil.buildUpdateRedisMember(asset, symbol, freq, kline.getMs());
            scoreMembers.put(member, Long.valueOf(kline.getMs()).doubleValue());
        }
        bbKlineExpHistoryRedisUtil.zadd(key, scoreMembers);
    }

    private List<BBKLine> listBbKline(String asset, String symbol, Long minMs, Long maxMs, int freq) {
        String thirdDataKey = buildExpHistoryDataKey(asset, symbol, freq);
        final Set<String> klines = bbKlineExpHistoryRedisUtil.zrangeByScore(thirdDataKey, "" + minMs, "" + maxMs, 0, Long.valueOf(maxMs - minMs).intValue() + 1);
        List<BBKLine> list = new ArrayList<>();
        // 按照时间minute升序
        if (!klines.isEmpty()) {
            for (String kline : klines) {
                BBKLine bbkLine1 = BBKlineUtil.convert2KlineData(kline, freq);
                list.add(bbkLine1);
            }
        }
        return list;
    }

    private String buildExpHistoryDataKey(String asset, String symbol, int freq) {
        return BbKlineRedisKeyUtil.buildThirdDataRedisKey(bbKlinePattern, asset, symbol, freq);
    }

    private Long[] listListeningTask(String bbKlineFromExpUpdateKey) {
        final Set<Tuple> triggers = bbKlineExpHistoryRedisUtil.zpopmin(bbKlineFromExpUpdateKey, expHistoryBatchSize);
        if (null == triggers || CollectionUtils.isEmpty(triggers)) {
        } else {
            Long maxMs = null, minMs = null;
            for (Tuple trigger : triggers) {
                final long score = Double.valueOf(trigger.getScore()).longValue();
                minMs = (null == minMs) ? score : Long.min(minMs, score);
                maxMs = (null == maxMs) ? score : Long.max(maxMs, score);
            }
            if (maxMs == null || minMs == null) {
                return null;
            } else {
                return new Long[]{minMs, maxMs};
            }
        }
        return null;
    }


    private String bbKlineFromExpUpdateKey(String asset, String symbol, int freq) {
        String bbKlineFromExpUpdateKey = StringReplaceUtil.replace(bbKlineFromExpUpdateEvent, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", "1");
            }
        });
        return bbKlineFromExpUpdateKey;
    }

    private List<BBSymbol> listSymbol() {
        final Map<String, BBSymbol> key2Value = metadataRedisUtil.hgetAll(BbKLineKey.BB_SYMBOL, BBSymbol.class);
        List<BBSymbol> list = key2Value.values().stream().collect(Collectors.toList());
        return list;
    }

    private List<BBSymbol> filterBbSymbols(List<BBSymbol> bbSymbols) {
        return bbSymbols.stream()
                .filter(symbol -> supportBbGroupIds.contains(symbol.getBbGroupId()))
                .collect(Collectors.toList());
    }

}
