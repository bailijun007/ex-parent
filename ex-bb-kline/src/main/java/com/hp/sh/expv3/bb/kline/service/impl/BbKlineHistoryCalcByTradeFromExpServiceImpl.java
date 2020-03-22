package com.hp.sh.expv3.bb.kline.service.impl;

import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import com.hp.sh.expv3.bb.kline.service.BbKlineHistoryCalcByTradeFromExpService;
import com.hp.sh.expv3.bb.kline.service.BbRepairTradeExtService;
import com.hp.sh.expv3.bb.kline.service.BbTradeExtService;
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

    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;

    @Value("${bb.kline}")
    private String bbKlinePattern;

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(20000000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    @Scheduled(cron = "*/1 * * * * *")
    @Override
    public void execute() {
        //bbKlineFromExpCalcEnable=1;
        if (1 != bbKlineFromExpCalcEnable) { // bbKlineFromExpCalcEnable=1
            return;
        } else {
            try {
//            while (true) {
                threadPool.execute(() -> repairKlineFromExp());
            } catch (Exception e) {
                logger.error("EXP平台历史计算k线发生错误，{}", e.getMessage());
            }
//            }

        }
    }


    public void repairKlineFromExp() {

//        List<BBSymbol> bbSymbols = BBKlineUtil.listSymbol(metadataRedisUtil);
//        List<BBSymbol> targetBbSymbols = BBKlineUtil.filterBbSymbols(bbSymbols, supportBbGroupIds);
        List<BBSymbol> targetBbSymbols = BBKlineUtil.listSymbols(supportBbGroupIdsJobService, supportBbGroupIds);


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

                    List<BbTradeVo> trades = listTradeFromRepaired(asset, symbol, ms, maxMs);
                    if (null == trades || trades.isEmpty()) {
                        //如果修复表中没有数据就从平台交易表中查询数据
                        trades = listTrade(asset, symbol, ms, maxMs);
                    }
                    //返回 对象集合以时间升序 再以id升序
                    List<BbTradeVo> sortedList = null;
                    if (!CollectionUtils.isEmpty(trades)) {
                        sortedList = trades.stream().sorted(Comparator.comparing(BbTradeVo::getTradeTime).thenComparing(BbTradeVo::getId)).collect(Collectors.toList());
                    }

                    if (!CollectionUtils.isEmpty(sortedList)) {
                        BBKLine kline = buildKline(sortedList, asset, symbol, ms, freq);
                        logger.info("build kline data:{}", kline.toString());
                        saveKline(repairkey, kline);
                        notifyUpdate(notifyUpdateKey, ms);
                    } else {
                        //如果最终sortedList==null，说明修复表，平台交易表中都没有数据，则直接将这条kline数据删除（如果需要修复可以走手动修复
                        // 或者第三方数据覆盖）


                        //删除数据
                        final String klineDataRedisKey = BbKlineRedisKeyUtil.buildKlineDataRedisKey(bbKlinePattern, asset, symbol, freq);
                        bbKlineExpHistoryRedisUtil.zremrangeByScore(klineDataRedisKey, ms, ms);

                    }

                }
            }
        }
    }

    private List<BbTradeVo> listTradeFromRepaired(String asset, String symbol, long ms, long maxMs) {
        List<BbTradeVo> result = new ArrayList<>();
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
    public List<BbTradeVo> listTrade(String asset, String symbol, long ms, long maxMs) {// TODO xb,一分钟内成交太多，会引入性能问题。
        /**
         * 首次查询：
         * sql: select *(具体的列，不需要所有列)
         * from table
         *  where trade_time >= ms and trade_time <= maxMs
         *  and id > ? (第一次不需要加这个条件，第二次才需要)
         *  order by id
         *  limit N
         *
         * 将本次查询的结果最后一条 BbTradeVo.id 作为参数，查询第二次
         *
         * 跳出循环条件：返回结果 < N
         */
        List<BbTradeVo> list = new ArrayList<>();
        int endLimit = 999999;
        List<BbTradeVo> voList = bbTradeExtService.queryByTimeInterval(null, asset, symbol, ms, maxMs, endLimit);
        list.addAll(voList);

        return list;
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
