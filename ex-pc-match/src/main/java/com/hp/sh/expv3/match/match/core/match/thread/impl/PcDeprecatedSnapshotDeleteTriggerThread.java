/**
 * @author 10086
 * @date 2019/10/25
 */
package com.hp.sh.expv3.match.match.core.match.thread.impl;

import com.hp.sh.expv3.match.config.setting.PcmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.PcmatchConst;
import com.hp.sh.expv3.match.match.core.order.OrderInitializer;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.util.PcUtil;
import com.hp.sh.expv3.match.util.RedisKeyUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import com.hp.sh.expv3.match.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class PcDeprecatedSnapshotDeleteTriggerThread {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderInitializer orderInitializer;
    @Autowired
    private PcmatchRedisKeySetting pcmatchRedisKeySetting;

    @Autowired
    @Qualifier("threadManagerPcMatchImpl")
    private IThreadManager iThreadManager;

    @Autowired
    @Qualifier(PcmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil pcRedisUtil;

    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    private void process() {
        trigger();
    }

    private long getSentMqOffset(String asset, String symbol) {
        String sentMqOffsetRedisKey = RedisKeyUtil.buildOrderSentMqOffsetRedisKeyPattern(pcmatchRedisKeySetting.getPcOrderSentMqOffsetRedisKeyPattern(), asset, symbol);
        long matchedMqOffset = -1L;
        if (pcRedisUtil.exists(sentMqOffsetRedisKey)) {
            String s = pcRedisUtil.get(sentMqOffsetRedisKey);
            matchedMqOffset = Long.parseLong(s);
        }
        return matchedMqOffset;
    }

    public void trigger() {

        if (null != iThreadManager.getWorkerKeys() && iThreadManager.getWorkerKeys().size() > 0) {
            for (String assetSymbol : iThreadManager.getWorkerKeys()) {
                Tuple2<String, String> assetSymbolTuple = PcUtil.splitAssetAndSymbol(assetSymbol);

                String asset = assetSymbolTuple.first;
                String symbol = assetSymbolTuple.second;

                long sentMqOffset = getSentMqOffset(asset, symbol);
                String snapshotRedisKey = RedisKeyUtil.buildPcOrderSnapshotRedisKey(pcmatchRedisKeySetting.getPcOrderSnapshotRedisKeyPattern(), asset, symbol);

                boolean exists = pcRedisUtil.exists(snapshotRedisKey);
                if (exists) {
                    Set<String> snapshotOffsetList = pcRedisUtil.hkeys(snapshotRedisKey);
                    TreeSet<Long> snapshotOffset = snapshotOffsetList.stream().map(Long::valueOf).collect(Collectors.toCollection(TreeSet::new));
                    Long startSnapshotOffset = snapshotOffset.floor(sentMqOffset);
                    if (null == startSnapshotOffset) {
                        continue;
                    } else {
                        /**
                         * 删除小于此offset的snapshot信息
                         */
                        Set<String> ignoreSnapshotOffset = snapshotOffset.stream().filter(x -> x < startSnapshotOffset).map(String::valueOf).collect(Collectors.toSet());
                        if (ignoreSnapshotOffset.size() > 0) {
                            pcRedisUtil.hdel(snapshotRedisKey, ignoreSnapshotOffset);
                        }
                    }

                }
            }
        }
    }
}