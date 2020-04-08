package com.hp.sh.expv3.bb.trade.job;

import com.hp.sh.expv3.bb.trade.pojo.BBSymbol;
import com.hp.sh.expv3.bb.trade.service.SupportBbGroupIdsJobService;
import com.hp.sh.expv3.bb.trade.util.BbKlineRedisKeyUtil;
import com.hp.sh.expv3.config.redis.RedisUtil;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Tuple;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author BaiLiJun  on 2020/4/7
 */
@Slf4j
@Component
public class BbKlinePersistentTask {
    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;

    @Value("${kline.persistentData.updateEventPattern}")
    private String updateEventPattern;

    @Value("${bb.kline.supportFrequenceString}")
    private String supportFrequenceString;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Value("${kline.persistent.batchSize}")
    private Integer persistentBatchSize;

    @PostConstruct
    private void init() {
        final String[] freqs = supportFrequenceString.split(",");
        for (String freq : freqs) {
            supportFrequence.add(Integer.valueOf(freq));
        }
    }

    private List<Integer> supportFrequence = new CopyOnWriteArrayList<>();

    @Scheduled(cron = "*/1 * * * * *")
    public void startPersistentTask() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();

        for (Integer freq : supportFrequence) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                 String persistentDataUpdateEventKey = BbKlineRedisKeyUtil.buildPersistentDataUpdateEventKey(updateEventPattern, bbSymbol.getAsset(), bbSymbol.getSymbol(), freq);
                final Set<Tuple> triggers = metadataRedisUtil.zpopmin(persistentDataUpdateEventKey, persistentBatchSize);
                if (CollectionUtils.isEmpty(triggers)) {
                    continue;
                }
                for (Tuple trigger : triggers) {
                    final long ms = Double.valueOf(trigger.getScore()).longValue();

                }

            }
        }

    }

}
