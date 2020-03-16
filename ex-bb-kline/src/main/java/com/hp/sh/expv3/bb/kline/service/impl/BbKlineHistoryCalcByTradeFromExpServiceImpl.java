package com.hp.sh.expv3.bb.kline.service.impl;

import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import com.hp.sh.expv3.bb.kline.service.BbKlineHistoryCalcByTradeFromExpService;
import com.hp.sh.expv3.bb.kline.service.BbRepairTradeExtService;
import com.hp.sh.expv3.bb.kline.service.BbTradeExtService;
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
 * @author BaiLiJun  on 2020/3/11
 */
@Service
public class BbKlineHistoryCalcByTradeFromExpServiceImpl implements BbKlineHistoryCalcByTradeFromExpService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${bb.kline.bbGroupIds}")
    private Set<Integer> supportBbGroupIds;

    @Value("${bb.kline.supportFrequenceString}")
    private String supportFrequenceString;

    @Value("${bb.kline.expHistoryBatchSize}")
    private Integer expHistoryBatchSize;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Autowired
    @Qualifier("bbKlineOngoingRedisUtil")
    private RedisUtil bbKlineExpHistoryRedisUtil;


    @Value("${from_exp.bbKlineDataUpdateEventPattern}")
    private String fromExpBbKlineDataUpdateEventPattern;

    @Value("${from_exp.bbKlineDataPattern}")
    private String fromExpBbKlineDataPattern;

    @Value("${from_exp.bbKlineTaskPattern}")
    private String fromExpBbKlineTaskPattern;

    @Value("${bb.kline.bbKlineFromExpCalcEnable}")
    private int bbKlineFromExpCalcEnable;

    @Autowired
    private BbRepairTradeExtService bbRepairTradeExtService;


    @Autowired
    private BbTradeExtService bbTradeExtService;

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            2,
            Runtime.getRuntime().availableProcessors() + 1,
            2L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(10000000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    @Scheduled(cron = "*/1 * * * * *")
    public void execute() {
        if (1 != bbKlineFromExpCalcEnable) { // bbKlineFromExpCalcEnable=1
            return;
        } else {
            threadPool.execute(() -> repairKlineFromExp());
        }
    }


    public void repairKlineFromExp() {

        List<BBSymbol> bbSymbols = BBKlineUtil.listSymbol(metadataRedisUtil);
        List<BBSymbol> targetBbSymbols = BBKlineUtil.filterBbSymbols(bbSymbols, supportBbGroupIds);

        for (BBSymbol bbSymbol : targetBbSymbols) {

            final String asset = bbSymbol.getAsset();
            final String symbol = bbSymbol.getSymbol();
            int freq = 1;
            {
                String taskKey = BbKlineRedisKeyUtil.buildFromExpBbKlineTaskRedisKey(fromExpBbKlineTaskPattern, asset, symbol, freq);
                String repairkey = BbKlineRedisKeyUtil.buildFromExpBbKlineDataByTradeRedisKey(fromExpBbKlineDataPattern, asset, symbol, freq);
                String notifyUpdateKey = BbKlineRedisKeyUtil.buildFromExpBbKlineUpdateEventKey(fromExpBbKlineDataUpdateEventPattern, asset, symbol, freq);

                final Set<Tuple> task = bbKlineExpHistoryRedisUtil.zpopmin(taskKey, expHistoryBatchSize);
                if (CollectionUtils.isEmpty(task)) {
                    continue;
                }
                for (Tuple tuple : task) {
                    Double score = tuple.getScore();
                    long ms = Double.valueOf(score).longValue();
                    long maxMs = TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toMinutes(ms) + 1) - 1;

                    // 时间大于等于当前时间的 忽略
                    long currentMs = Calendar.getInstance().getTimeInMillis();
                    if (ms >= currentMs) {
                        continue;
                    }

                    // 若修复数据已存在，忽略 从redis kline:from_exp:repair:BB:${asset}:${symbol}:${minute}中取
//                    final Set<String> set = bbKlineExpHistoryRedisUtil.zrangeByScore(repairkey, ms + "", maxMs + "", 0, Long.valueOf(maxMs - ms).intValue() + 1);
//
//                    if (!set.isEmpty()) {
//                        continue;
//                    }
//                    if (null != set || !set.isEmpty()) {
//                        continue;
//                    }

                    List<BbTradeVo> trades = listTradeFromRepaired(asset, symbol, ms, maxMs);

                    if (null == trades || trades.isEmpty()) {
                        trades = listTrade(asset, symbol, ms, maxMs);
                    }else {
                        BBKLine kline = buildKline(trades, asset, symbol, ms, freq);

                        logger.info("build kline data:{}", kline.toString());

                        saveKline(repairkey, kline);

                        notifyUpdate(notifyUpdateKey, ms);
                    }

                }

            }

        }
    }

    private List<BbTradeVo> listTradeFromRepaired(String asset, String symbol, long ms, long maxMs) {
        List<BbTradeVo> result=new ArrayList<>();
        List<BbRepairTradeVo> list = bbRepairTradeExtService.listRepairTrades(asset, symbol, ms, maxMs);
        if (!CollectionUtils.isEmpty(list)) {
            for (BbRepairTradeVo tradeVo : list) {
                 BbTradeVo bbTradeVo = new BbTradeVo();
                BeanUtils.copyProperties(tradeVo, bbTradeVo);
                result.add(bbTradeVo);
            }
        }

        return result;
    }

    private void notifyUpdate(String notifyUpdateKey, long ms) {
        HashMap<String, Double> scoreMembers = new HashMap<String, Double>();
        scoreMembers.put(ms + "", Long.valueOf(ms).doubleValue());
        bbKlineExpHistoryRedisUtil.zadd(notifyUpdateKey, scoreMembers);
    }

    private void saveKline(String repairkey, BBKLine kline) {
        final double score = Long.valueOf(kline.getMs()).doubleValue();
        bbKlineExpHistoryRedisUtil.zremrangeByScore(repairkey, score, score);
        HashMap<String, Double> scoreMembers = new HashMap<String, Double>();
        final String data = BBKlineUtil.kline2ArrayData(kline);
        scoreMembers.put(data, score);
        bbKlineExpHistoryRedisUtil.zadd(repairkey, scoreMembers);
    }

    /**
     * 从mysql中查询
     *
     * @param asset
     * @param symbol
     * @param ms
     * @param maxMs
     * @return
     */
    private List<BbTradeVo> listTrade(String asset, String symbol, long ms, long maxMs) {
        List<BbTradeVo> voList = bbTradeExtService.queryByTimeInterval(asset, symbol, ms, maxMs);
        //返回 对象集合以时间升序 再以id升序
        List<BbTradeVo> sortedList = voList.stream().sorted(Comparator.comparing(BbTradeVo::getTradeTime).thenComparing(BbTradeVo::getId)).collect(Collectors.toList());
        return sortedList;
    }


    private BBKLine buildKline(List<BbTradeVo> trades, String asset, String symbol, long ms, int freq) {
        BBKLine bBKLine = new BBKLine();
        bBKLine.setAsset(asset);
        bBKLine.setSymbol(symbol);
        bBKLine.setFrequence(freq);
        bBKLine.setMs(ms);
        bBKLine.setMinute(TimeUnit.MILLISECONDS.toMinutes(ms));

        BigDecimal highPrice = BigDecimal.ZERO;
        BigDecimal lowPrice = new BigDecimal(String.valueOf(Long.MAX_VALUE));
        BigDecimal openPrice = null;
        BigDecimal closePrice = null;
        BigDecimal volume = BigDecimal.ZERO;

        for (BbTradeVo trade : trades) {
            BigDecimal currentPrice = trade.getPrice();
            highPrice = highPrice.compareTo(trade.getPrice()) >= 0 ? highPrice : currentPrice;
            lowPrice = lowPrice.compareTo(trade.getPrice()) <= 0 ? lowPrice : currentPrice;
            openPrice = null == openPrice ? currentPrice : openPrice;
            closePrice = currentPrice;
            volume = volume.add(trade.getNumber());
        }

        bBKLine.setHigh(highPrice);
        bBKLine.setLow(lowPrice);
        bBKLine.setOpen(openPrice);
        bBKLine.setClose(closePrice);
        bBKLine.setVolume(volume);

        return bBKLine;
    }

}
