package com.hp.sh.expv3.bb.trade.job;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.trade.pojo.BBKLine;
import com.hp.sh.expv3.bb.trade.pojo.BBSymbol;
import com.hp.sh.expv3.bb.trade.service.BbKlinePersistentDataService;
import com.hp.sh.expv3.bb.trade.service.SupportBbGroupIdsJobService;
import com.hp.sh.expv3.bb.trade.util.BBKlineUtil;
import com.hp.sh.expv3.bb.trade.util.BbKlineRedisKeyUtil;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Tuple;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/4/7
 */
@Component
public class BbKlinePersistentTask {
    private static final Logger logger = LoggerFactory.getLogger(BbKlinePersistentTask.class);
    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;

    @Value("${kline.persistentData.updateEventPattern}")
    private String updateEventPattern;

    @Autowired
    @Qualifier("metadataDb5RedisUtil")
    private RedisUtil metadataDb5RedisUtil;

    @Value("${kline.persistent.batchSize}")
    private Integer persistentBatchSize;

    @Value("${bb.kline.data}")
    private String bbKlinePattern;

    @Autowired
    private BbKlinePersistentDataService bbKlinePersistentDataService;


    @Scheduled(cron = "*/59 * * * * *")
    public void start1MinutePersistentTask() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();

        Integer freq = 1;
        for (BBSymbol bbSymbol : bbSymbolList) {
            start(freq, bbSymbol);
        }
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void start5MinutePersistentTask() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();

        Integer freq = 5;
        for (BBSymbol bbSymbol : bbSymbolList) {
            start(freq, bbSymbol);
        }
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    public void start15MinutePersistentTask() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();

        Integer freq = 15;
        for (BBSymbol bbSymbol : bbSymbolList) {
            start(freq, bbSymbol);
        }
    }

    @Scheduled(cron = "0 0/30 * * * ?")
    public void start30MinutePersistentTask() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();

        Integer freq = 30;
        for (BBSymbol bbSymbol : bbSymbolList) {
            start(freq, bbSymbol);
        }
    }

    @Scheduled(cron = "0 0/59 * * * ?")
    public void start60MinutePersistentTask() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();

        Integer freq = 60;
        for (BBSymbol bbSymbol : bbSymbolList) {
            start(freq, bbSymbol);
        }
    }

    @Scheduled(cron = "0 0 0-6 * * ?")
    public void start6HourPersistentTask() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();

        Integer freq = 360;
        for (BBSymbol bbSymbol : bbSymbolList) {
            start(freq, bbSymbol);
        }
    }

    @Scheduled(cron = "0 0 0-12 * * ?")
    public void start1DayPersistentTask() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();

        Integer freq = 1440;
        for (BBSymbol bbSymbol : bbSymbolList) {
            start(freq, bbSymbol);
        }
    }

    private void start(Integer freq, BBSymbol bbSymbol) {
        String persistentDataUpdateEventKey = BbKlineRedisKeyUtil.buildPersistentDataUpdateEventKey(updateEventPattern, bbSymbol.getAsset(), bbSymbol.getSymbol(), freq);
        String bbKlineDataRedisKey = BbKlineRedisKeyUtil.buildKlineDataRedisKey(bbKlinePattern, bbSymbol.getAsset(), bbSymbol.getSymbol(), freq);

        Set<Tuple> triggers = metadataDb5RedisUtil.zpopmin(persistentDataUpdateEventKey, persistentBatchSize);
        if (CollectionUtils.isEmpty(triggers)) {
            return;
        }
        List<Long> startAndEndList = new ArrayList<>();
        for (Tuple trigger : triggers) {
            Long ms = Double.valueOf(trigger.getScore()).longValue();
            startAndEndList.add(ms);
        }

        LongSummaryStatistics collect = startAndEndList.stream().collect(Collectors.summarizingLong(Long::longValue));
        Long startMs = collect.getMin();
        Long endMs = collect.getMax();
        Set<String> set = metadataDb5RedisUtil.zrangeByScore(bbKlineDataRedisKey, startMs + "", endMs + "", 0, Long.valueOf(endMs - startMs + 1).intValue());
        List<BBKLine> batchSaveList = new ArrayList<>();
        List<BBKLine> batchUpdateList = new ArrayList<>();
        for (String s : set) {
            BBKLine bbkLine = BBKlineUtil.convertKlineData(s, freq, bbSymbol.getAsset(), bbSymbol.getSymbol());
            //查询是否存在，存在-->更新;  不存在-->新增
            boolean exist = bbKlinePersistentDataService.isExist(bbkLine);
             long milli = Instant.now().toEpochMilli();
            if (!exist) {
                bbkLine.setCreated(milli);
                bbkLine.setModified(milli);
                batchSaveList.add(bbkLine);
            } else {
                Long id = bbKlinePersistentDataService.queryIdByBBKLine(bbkLine);
                bbkLine.setModified(milli);
                bbkLine.setId(id);
                batchUpdateList.add(bbkLine);
            }
        }

        bbKlinePersistentDataService.batchSave(batchSaveList);
        logger.info("asset={},symbol={},freq={},batchSaveList={}", bbSymbol.getAsset(), bbSymbol.getSymbol(), freq, JSON.toJSONString(batchSaveList));
        bbKlinePersistentDataService.batchUpdate(batchUpdateList);
        logger.info("asset={},symbol={},freq={},batchUpdateList={}", bbSymbol.getAsset(), bbSymbol.getSymbol(), freq, JSON.toJSONString(batchUpdateList));

    }

}
