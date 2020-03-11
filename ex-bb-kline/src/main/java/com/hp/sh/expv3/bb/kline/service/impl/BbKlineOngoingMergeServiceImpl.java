package com.hp.sh.expv3.bb.kline.service.impl;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
import com.hp.sh.expv3.bb.kline.constant.BbextendConst;
import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.service.BbKlineOngoingMergeService;
import com.hp.sh.expv3.bb.kline.util.StringReplaceUtil;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Tuple;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/10
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbKlineOngoingMergeServiceImpl implements BbKlineOngoingMergeService {
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

//    @Value("${bb.kline.trigger.pattern}")
//    private String bbKlineTriggerPattern;

    @Value("${bb.kline.updateEventPattern}")
    private String updateEventPattern;

    @Value("${bb.kline.ongoingMerge.enable}")
    private int ongoingMergeEnable;

    @PostConstruct
    private void init() {
        final String[] freqs = supportFrequenceString.split(",");
        for (String freq : freqs) {
            supportFrequence.add(Integer.valueOf(freq));
        }
    }

    private List<Integer> supportFrequence = new ArrayList<>();


    //    @Scheduled(cron = "0 0/1 * * * ?")
    @Override
    @Scheduled(cron = "*/1 * * * * *")
    public void getKlineData() {
//        ongoingMergeEnable=1
        if (1 != ongoingMergeEnable) {
            return;
        }

        List<BBSymbol> bbSymbols = listSymbol();
        List<BBSymbol> targetBbSymbols = filterBbSymbols(bbSymbols);

        // 谁由谁触发
        TreeMap<Integer, Integer> tar2TriggerFrequence = buildTargetFrequence2TriggerFrequence(supportFrequence);
        // 谁触发谁
        Map<Integer, TreeSet<Integer>> trigger2TarFrequence = buildTrigger2TarFrequence(tar2TriggerFrequence);

        for (BBSymbol bbSymbol : targetBbSymbols) {
            final String asset = bbSymbol.getAsset();
            final String symbol = bbSymbol.getSymbol();
            for (Integer triggerFreq : supportFrequence) {
                String triggerRedisKey = buildTriggerRedisKey(asset, symbol, triggerFreq);
//                String triggerRedisKey = buildKlineSaveRedisKey(asset, symbol, triggerFreq);
                final Set<Tuple> triggers = bbKlineOngoingRedisUtil.zpopmin(triggerRedisKey, triggerBatchSize);
                final TreeSet<Integer> targetFreqs = trigger2TarFrequence.get(triggerFreq);
                if (null == targetFreqs) {
                    continue;
                }
                for (Tuple trigger : triggers) {
                    final String element = trigger.getElement();
                    final long minute = Double.valueOf(trigger.getScore()).longValue();
                    for (Integer targetFreq : targetFreqs) {
                        Long[] startAndEndMinutes = getStartEndMinute(minute, triggerFreq, targetFreq);
                        final List<BBKLine> bbkLines = listKlineResource(asset, symbol, triggerFreq, startAndEndMinutes[0], startAndEndMinutes[1]);
                        if (null == bbkLines || bbkLines.isEmpty()) {
                            continue;
                        } else {
                            BBKLine newKline = merge(asset, symbol, targetFreq, startAndEndMinutes[0], bbkLines);
                            saveOrUpdateKline(asset, symbol, targetFreq, newKline);
                            notifyKlineUpdate(asset, symbol, targetFreq, startAndEndMinutes[0]);
                        }
                    }
                }

            }

        }
    }


    private String buildKlineSaveRedisKey(String asset, String symbol, int frequency) {
        return StringReplaceUtil.replace(bbKlinePattern, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
        }});
    }

    private void notifyKlineUpdate(String asset, String symbol, Integer targetFreq, Long startMinute) {
        //向集合中插入元素，并设置分数
        String key = buildUpdateRedisKey(asset, symbol, targetFreq);
        bbKlineOngoingRedisUtil.zadd(key, new HashMap<String, Double>() {{
                    put(buildUpdateRedisMember(asset, symbol, targetFreq, startMinute), Long.valueOf(startMinute).doubleValue());
                }}
        );
    }

    private String buildUpdateRedisMember(String asset, String symbol, int frequency, long minute) {
        return StringReplaceUtil.replace(BbextendConst.BB_KLINE_UPDATE_MEMBER, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
            put("minute", "" + minute);
        }});
    }

    private void saveOrUpdateKline(String asset, String symbol, Integer targetFreq, BBKLine newKline) {
        final String targetFreqRedisKey = buildKlineSaveRedisKey(asset, symbol, targetFreq);
        //删除老数据
        bbKlineOngoingRedisUtil.zremrangeByScore(targetFreqRedisKey, newKline.getMinute(), newKline.getMinute());
        //新增新数据
        bbKlineOngoingRedisUtil.zadd(targetFreqRedisKey, new HashMap<String, Double>() {{
            put(JSON.toJSONString(newKline), Long.valueOf(newKline.getMinute()).doubleValue());
        }});

    }

    private BBKLine merge(String asset, String symbol, Integer targetFreq, Long startMinute, List<BBKLine> bbkLines) {
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
            BigDecimal low = kLine.getHigh();
            BigDecimal open = kLine.getHigh();
            highPrice = (high.compareTo(highPrice) >= 0) ? high : highPrice;
            lowPrice = (low.compareTo(lowPrice) <= 0) ? low : lowPrice;
            openPrice = (null == openPrice) ? open : openPrice;
            volume = volume.add(kLine.getVolume());
        }

        bbkLine.setHigh(highPrice);
        bbkLine.setLow(lowPrice);
        bbkLine.setOpen(openPrice);
        bbkLine.setClose(closePrice);
        bbkLine.setVolume(volume);
        bbkLine.setFrequence(targetFreq);
        bbkLine.setMinute(startMinute);
        return bbkLine;
    }

    private List<BBKLine> listKlineResource(String asset, String symbol, Integer triggerFreq, Long startMinute, Long endMinute) {
        final String triggerFreqRedisKey = buildKlineSaveRedisKey(asset, symbol, triggerFreq);
        final Set<String> klines = bbKlineOngoingRedisUtil.zrangeByScore(triggerFreqRedisKey, startMinute + "", endMinute + "", 0, Long.valueOf(endMinute - startMinute + 1).intValue());
        List<BBKLine> list = new ArrayList<>();
        // 按照时间minute升序
        if (!klines.isEmpty()) {
            for (String kline : klines) {
                BBKLine bbkLine1 = JSON.parseObject(kline, BBKLine.class);
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
     * @param minute      triggerFreq的起始时间
     * @param triggerFreq
     * @param targetFreq
     * @return
     */
    private Long[] getStartEndMinute(long minute, Integer triggerFreq, Integer targetFreq) {
        final long divisor = minute / targetFreq;
        return new Long[]{divisor * targetFreq, (divisor + 1) * targetFreq - 1};
    }


    private String buildTriggerRedisKey(String asset, String symbol, Integer frequency) {
        return StringReplaceUtil.replace(updateEventPattern, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", "" + frequency);
            }
        });
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

    private List<BBSymbol> filterBbSymbols(List<BBSymbol> bbSymbols) {
        return bbSymbols.stream()
                .filter(symbol -> supportBbGroupIds.contains(symbol.getBbGroupId()))
                .collect(Collectors.toList());
    }

    private List<BBSymbol> listSymbol() {
        final Map<String, BBSymbol> key2Value = metadataRedisUtil.hgetAll(BbKLineKey.BB_SYMBOL, BBSymbol.class);
        List<BBSymbol> list = key2Value.values().stream().collect(Collectors.toList());
        return list;
    }


    private String buildUpdateRedisKey(String asset, String symbol, int frequency) {
//        bb.kline.update
        return StringReplaceUtil.replace(updateEventPattern, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", "" + frequency);
            }
        });
    }
}
