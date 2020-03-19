package com.hp.sh.expv3.bb.kline.service.impl;

import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.service.BbKlineOngoingMergeService;
import com.hp.sh.expv3.bb.kline.service.SupportBbGroupIdsJobService;
import com.hp.sh.expv3.bb.kline.util.BBKlineUtil;
import com.hp.sh.expv3.bb.kline.util.BbKlineRedisKeyUtil;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Tuple;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/10
 */
@Service
public class BbKlineOngoingMergeServiceImpl implements BbKlineOngoingMergeService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${bb.kline.bbGroupIds}")
    private Set<Integer> supportBbGroupIds;

    @Value("${bb.kline.triggerBatchSize}")
    private Integer triggerBatchSize;

    @Value("${bb.kline.supportFrequenceString}")
    private String supportFrequenceString;

    @Value("${bb.kline}")
    private String bbKlinePattern;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Autowired
    @Qualifier("bbKlineOngoingRedisUtil")
    private RedisUtil bbKlineOngoingRedisUtil;

    @Value("${bb.kline.updateEventPattern}")
    private String updateEventPattern;

    @Value("${bb.kline.ongoingMerge.enable}")
    private int ongoingMergeEnable;

    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;

    @PostConstruct
    private void init() {
        final String[] freqs = supportFrequenceString.split(",");
        for (String freq : freqs) {
            supportFrequence.add(Integer.valueOf(freq));
        }
    }

    private List<Integer> supportFrequence = new CopyOnWriteArrayList<>();


    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
             1,
            1, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(20000000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    @Scheduled(cron = "*/1 * * * * *")
    public void satrt() {
        //  ongoingMergeEnable=1
        if (1 != ongoingMergeEnable) {
            return;
        } else {
            threadPool.execute(() -> mergeKlineData());
        }
    }


    @Override
    public void mergeKlineData() {
        List<BBSymbol> targetBbSymbols = BBKlineUtil.listSymbols(supportBbGroupIdsJobService, supportBbGroupIds);

//        List<BBSymbol> bbSymbols = BBKlineUtil.listSymbol(metadataRedisUtil);
//        List<BBSymbol> targetBbSymbols = BBKlineUtil.filterBbSymbols(bbSymbols,supportBbGroupIds);


        // 谁由谁触发
        TreeMap<Integer, Integer> tar2TriggerFrequence = buildTargetFrequence2TriggerFrequence(supportFrequence);
        // 谁触发谁
        Map<Integer, TreeSet<Integer>> trigger2TarFrequence = buildTrigger2TarFrequence(tar2TriggerFrequence);

        for (BBSymbol bbSymbol : targetBbSymbols) {
            final String asset = bbSymbol.getAsset();
            final String symbol = bbSymbol.getSymbol();
            for (Integer triggerFreq : supportFrequence) {
                String triggerRedisKey = BbKlineRedisKeyUtil.buildKlineUpdateEventRedisKey(updateEventPattern, asset, symbol, triggerFreq);
//                String triggerRedisKey = buildKlineSaveRedisKey(asset, symbol, triggerFreq);
                final Set<Tuple> triggers = bbKlineOngoingRedisUtil.zpopmin(triggerRedisKey, triggerBatchSize);
                final TreeSet<Integer> targetFreqs = trigger2TarFrequence.get(triggerFreq);
                if (null == targetFreqs) {
                    continue;
                }



                Map<Integer, Set<Long>> targetFreq2StartMsSet = new HashMap<>(targetFreqs.size());

                for (Tuple trigger : triggers) {
                    final String element = trigger.getElement();
                    final long ms = Double.valueOf(trigger.getScore()).longValue();
                    for (Integer targetFreq : targetFreqs) {
                        Long[] startAndEndMs = getStartEndMs(ms, triggerFreq, targetFreq);

                        Set<Long> startMsSet = targetFreq2StartMsSet.get(targetFreq);
                        if (null == startMsSet) {
                            startMsSet = new HashSet<>();
                            targetFreq2StartMsSet.put(targetFreq, startMsSet);
                        }
/**
 * 此参数用于优化如下场景：
 * 补历史数据时，1分钟的数据批量更新，则5分钟的数据只需要处理一次即可。
 * 如：1分钟数据 中，第 0 ~ 9 分钟数据更新，则 5分钟的 0,5 数据，只需要在 第0和5分钟的1分钟数据 更新时触发
 * 第 1,2,3,4 分钟的1分钟数据，和 0分钟的数 据是一样的，直接continue;
 */
                        if (startMsSet.contains(startAndEndMs[0])) {
                            logger.debug("freq {},{},ignore", targetFreq, startAndEndMs[0]);
                            continue;
                        }

                        final List<BBKLine> bbkLines = listKlineResource(asset, symbol, triggerFreq, startAndEndMs[0], startAndEndMs[1]);

                        if (null == bbkLines || bbkLines.isEmpty()) {
                            logger.debug("freq {},{},data empty,ignore", targetFreq, startAndEndMs[0]);
                            continue;
                        } else {
                            BBKLine newKline = merge(asset, symbol, targetFreq, startAndEndMs[0], bbkLines);
                            saveOrUpdateKline(asset, symbol, targetFreq, newKline);
                            notifyKlineUpdate(asset, symbol, targetFreq, startAndEndMs[0]);
                        }
                        startMsSet.add(startAndEndMs[0]);
                    }
                }
            }
        }
    }

    private void notifyKlineUpdate(String asset, String symbol, Integer targetFreq, Long startMs) {
        //向集合中插入元素，并设置分数
        String key = BbKlineRedisKeyUtil.buildKlineUpdateEventRedisKey(updateEventPattern, asset, symbol, targetFreq);
        bbKlineOngoingRedisUtil.zadd(key, new HashMap<String, Double>() {{
                    put(BbKlineRedisKeyUtil.buildUpdateRedisMember(asset, symbol, targetFreq, startMs), Long.valueOf(startMs).doubleValue());
                }}
        );
    }

    private void saveOrUpdateKline(String asset, String symbol, Integer targetFreq, BBKLine newKline) {
        final String targetFreqRedisKey = BbKlineRedisKeyUtil.buildKlineDataRedisKey(bbKlinePattern, asset, symbol, targetFreq);
        //删除老数据
        bbKlineOngoingRedisUtil.zremrangeByScore(targetFreqRedisKey, newKline.getMs(), newKline.getMs());
        //新增新数据
        bbKlineOngoingRedisUtil.zadd(targetFreqRedisKey, new HashMap<String, Double>() {{
            final String data = BBKlineUtil.kline2ArrayData(newKline);
            put(data, Long.valueOf(newKline.getMs()).doubleValue());
        }});
        logger.debug("freq {},{}", targetFreq, newKline.getMs());
    }

    private BBKLine merge(String asset, String symbol, Integer targetFreq, Long startMs, List<BBKLine> bbkLines) {
        final BBKLine bbkLine = new BBKLine();
        bbkLine.setAsset(asset);
        bbkLine.setSymbol(symbol);

        BigDecimal highPrice = BigDecimal.ZERO;
        BigDecimal lowPrice = new BigDecimal(String.valueOf(Long.MAX_VALUE));
        BigDecimal openPrice = null;
        BigDecimal closePrice = null;
        BigDecimal volume = BigDecimal.ZERO;

        for (BBKLine kLine : bbkLines) {
            BigDecimal high = kLine.getHigh();
            BigDecimal low = kLine.getLow();
            BigDecimal open = kLine.getOpen();
            BigDecimal close = kLine.getClose();
            highPrice = (high.compareTo(highPrice) >= 0) ? high : highPrice;
            lowPrice = (low.compareTo(lowPrice) <= 0) ? low : lowPrice;
            openPrice = (null == openPrice) ? open : openPrice;
            closePrice = close;
            volume = volume.add(kLine.getVolume());
        }

        bbkLine.setHigh(highPrice);
        bbkLine.setLow(lowPrice);
        bbkLine.setOpen(openPrice);
        bbkLine.setClose(closePrice);
        bbkLine.setVolume(volume);
        bbkLine.setFrequence(targetFreq);
        bbkLine.setMinute(TimeUnit.MILLISECONDS.toMinutes(startMs));
        bbkLine.setMs(startMs);
        return bbkLine;
    }

    private List<BBKLine> listKlineResource(String asset, String symbol, Integer triggerFreq, Long startMs, Long endMs) {
        final String triggerFreqRedisKey = BbKlineRedisKeyUtil.buildKlineDataRedisKey(bbKlinePattern, asset, symbol, triggerFreq);
        final Set<String> klines = bbKlineOngoingRedisUtil.zrangeByScore(triggerFreqRedisKey, startMs + "", endMs + "", 0, Long.valueOf(endMs - startMs + 1).intValue());
        List<BBKLine> list = new ArrayList<>();
        // 按照时间minute升序
        if (!klines.isEmpty()) {
            for (String kline : klines) {
                BBKLine bbkLine1 = BBKlineUtil.convert2KlineData(kline, triggerFreq);
                bbkLine1.setAsset(asset);
                bbkLine1.setSymbol(symbol);
                list.add(bbkLine1);
            }
        }
        List<BBKLine> sortedList = list.stream().sorted(Comparator.comparing(BBKLine::getMinute)).collect(Collectors.toList());

        return sortedList;
    }

    /**
     * <pre>
     * minute=45 这个5分钟频率的K线更新，触发10分钟的K线更新
     * minute = 45
     * triggerFreq = 5
     * targetFreq = 10
     * </pre>
     *
     * @param ms          triggerFreq的起始时间
     * @param triggerFreq
     * @param targetFreq
     * @return
     */
    private Long[] getStartEndMs(long ms, Integer triggerFreq, Integer targetFreq) {
        final long divisor = TimeUnit.MILLISECONDS.toMinutes(ms) / targetFreq;
        return new Long[]{
                TimeUnit.MINUTES.toMillis(divisor * targetFreq),
                TimeUnit.MINUTES.toMillis((divisor + 1) * targetFreq - 1)
        };
    }


    private TreeMap<Integer, TreeSet<Integer>> buildTrigger2TarFrequence(TreeMap<Integer, Integer> targetFreq2TriggerFreq) {
        TreeMap<Integer, TreeSet<Integer>> trigger2Tar = new TreeMap<>();
        final Set<Map.Entry<Integer, Integer>> kvs = targetFreq2TriggerFreq.entrySet();
        for (Map.Entry<Integer, Integer> kv : kvs) {
            final Integer targetFreq = kv.getKey();
            final Integer triggerFreq = kv.getValue();
            TreeSet<Integer> targetFreqs = trigger2Tar.get(triggerFreq);
            if (targetFreqs == null) {
                targetFreqs = new TreeSet<Integer>();
                trigger2Tar.put(triggerFreq, targetFreqs);
            }
            targetFreqs.add(targetFreq);
        }
        return trigger2Tar;
    }

    /**
     * 谁由谁触发
     *
     * @param supportFrequence，必须有序
     * @return
     */

    private TreeMap<Integer, Integer> buildTargetFrequence2TriggerFrequence(List<Integer> supportFrequence) {
        // 1,5,10,15,30
        supportFrequence.sort(Comparator.naturalOrder());
        TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();

        for (int i = 0; i < supportFrequence.size(); i++) {
            if (i == 0) {
                continue;
            } else {
                map.put(supportFrequence.get(i), 1);
            }
        }

        for (Integer tarFreq : map.keySet()) {
            final Integer oldTriggerFreq = map.get(tarFreq);
            for (Integer nextTriggerFreq : supportFrequence) {
                if (nextTriggerFreq >= tarFreq) {
                    break;
                }
                if (nextTriggerFreq < tarFreq && nextTriggerFreq > oldTriggerFreq) {
                    BigDecimal remainder = BigDecimal.valueOf(tarFreq).divideAndRemainder(BigDecimal.valueOf(nextTriggerFreq))[1];
                    if (0 == BigDecimal.ZERO.compareTo(remainder)) {
                        map.put(tarFreq, nextTriggerFreq);
                    }
                }
            }
        }
        return map;
    }


}
