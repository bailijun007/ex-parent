package com.hp.sh.expv3.bb.kline.service.impl;

import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import com.hp.sh.expv3.bb.kline.service.BbKlineHistoryCoverByTradeFromExpService;
import com.hp.sh.expv3.bb.kline.service.BbRepairTradeExtService;
import com.hp.sh.expv3.bb.kline.service.SupportBbGroupIdsJobService;
import com.hp.sh.expv3.bb.kline.util.BBKlineUtil;
import com.hp.sh.expv3.bb.kline.util.BbKlineRedisKeyUtil;
import com.hp.sh.expv3.bb.kline.vo.BbRepairTradeVo;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Tuple;

import java.math.BigDecimal;
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

    @Value("${bb.kline.updateEventPattern}")
    private String updateEventPattern;

    @Value("${bb.kline}")
    private String bbKlinePattern;

    @Value("${from_exp.bbKlineDataPattern}")
    private String fromExpBbKlineDataPattern;

    @Value("${from_exp.bbKlineDataUpdateEventPattern}")
    private String fromExpBbKlineDataUpdateEventPattern;

    @Value("${bb.kline.bbKlineFromExpCoverEnable}")
    private int bbKlineFromExpCoverEnable;

    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(20000000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    @Scheduled(cron = "*/1 * * * * *")
    public void execute() {
        //bbKlineFromExpCoverEnable=1;
        if (1 != bbKlineFromExpCoverEnable) {
            return;
        } else {
            threadPool.execute(() -> updateKlineByExpHistory());
        }
    }



    public void updateKlineByExpHistory() {

//        List<BBSymbol> bbSymbols = BBKlineUtil.listSymbol(metadataRedisUtil);
//        List<BBSymbol> targetBbSymbols = BBKlineUtil.filterBbSymbols(bbSymbols, supportBbGroupIds);
        List<BBSymbol> targetBbSymbols = BBKlineUtil.listSymbols(supportBbGroupIdsJobService,supportBbGroupIds);

        for (BBSymbol bbSymbol : targetBbSymbols) {

            final String asset = bbSymbol.getAsset();
            final String symbol = bbSymbol.getSymbol();
            int freq = 1;
            //监听通知消息
            String bbKlineFromExpUpdateKey = BbKlineRedisKeyUtil.buildFromExpBbKlineUpdateEventKey(fromExpBbKlineDataUpdateEventPattern, asset, symbol, freq);

            //返回通知消息的最小分数跟最大分数
            Long[] minAndMaxMs = listListeningTask(bbKlineFromExpUpdateKey);

            if (null == minAndMaxMs) {

            } else {
                final Long minMs = minAndMaxMs[0];
                final Long maxMs = minAndMaxMs[1];
                List<BBKLine>  klines = listBbKline(asset, symbol, minMs, maxMs, freq);
                if (null == klines || klines.isEmpty()) {
                    continue;
                }
                coverData(asset, symbol, minMs, maxMs, freq, klines);
                notifyKlineUpdate(asset, symbol, minMs, maxMs, freq, klines);
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

    /**
     * 通过范围条件查询
     *
     * @param asset
     * @param symbol
     * @param minMs
     * @param maxMs
     * @param freq
     * @return
     */
    private List<BBKLine> listBbKline(String asset, String symbol, Long minMs, Long maxMs, int freq) {
        String fromExpBbKlineDataRedisKey = BbKlineRedisKeyUtil.buildFromExpBbKlineDataByTradeRedisKey(fromExpBbKlineDataPattern, asset, symbol, freq);
        List<BBKLine> list = new ArrayList<>();

        final Set<String> klines = bbKlineExpHistoryRedisUtil.zrangeByScore(fromExpBbKlineDataRedisKey, "" + minMs, "" + maxMs, 0, Long.valueOf(maxMs - minMs).intValue() + 1);

        if (!klines.isEmpty()) {
            for (String kline : klines) {
                BBKLine bbkLine1 = BBKlineUtil.convert2KlineData(kline, freq);
                bbkLine1.setAsset(asset);
                bbkLine1.setSymbol(symbol);
                list.add(bbkLine1);
            }
        }
        return list;
    }

    /**
     * 返回通知消息的最小分数跟最大分数
     *
     * @param bbKlineFromExpUpdateKey
     * @return
     */
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
                logger.info("返回通知消息的最小分数为:{},最大分数为:{}", minMs, maxMs);
                return new Long[]{minMs, maxMs};
            }
        }
        return null;
    }


}
