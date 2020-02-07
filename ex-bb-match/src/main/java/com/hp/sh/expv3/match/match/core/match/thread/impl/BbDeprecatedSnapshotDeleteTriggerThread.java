/**
 * @author 10086
 * @date 2019/10/25
 */
package com.hp.sh.expv3.match.match.core.match.thread.impl;

import com.hp.sh.expv3.match.config.setting.BbmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.BbmatchConst;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.util.BbUtil;
import com.hp.sh.expv3.match.util.RedisKeyUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import com.hp.sh.expv3.match.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class BbDeprecatedSnapshotDeleteTriggerThread {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BbmatchRedisKeySetting bbmatchRedisKeySetting;

    @Autowired
    @Qualifier("threadManagerBbMatchImpl")
    private IThreadManager iThreadManager;

    @Autowired
    @Qualifier(BbmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil bbRedisUtil;

    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    private void process() {
        trigger();
    }

    private long getSentMqOffset(String asset, String symbol) {
        String sentMqOffsetRedisKey = RedisKeyUtil.buildOrderSentMqOffsetRedisKeyPattern(bbmatchRedisKeySetting.getBbOrderSentMqOffsetRedisKeyPattern(), asset, symbol);
        long matchedMqOffset = -1L;
        if (bbRedisUtil.exists(sentMqOffsetRedisKey)) {
            String s = bbRedisUtil.get(sentMqOffsetRedisKey);
            matchedMqOffset = Long.parseLong(s);
        }
        return matchedMqOffset;
    }

    public void trigger() {

        if (null != iThreadManager.getWorkerKeys() && iThreadManager.getWorkerKeys().size() > 0) {
            for (String assetSymbol : iThreadManager.getWorkerKeys()) {
                Tuple2<String, String> assetSymbolTuple = BbUtil.splitAssetAndSymbol(assetSymbol);

                String asset = assetSymbolTuple.first;
                String symbol = assetSymbolTuple.second;

                long sentMqOffset = getSentMqOffset(asset, symbol);
                String snapshotRedisKey = RedisKeyUtil.buildBbOrderSnapshotRedisKey(bbmatchRedisKeySetting.getBbOrderSnapshotRedisKeyPattern(), asset, symbol);

                boolean exists = bbRedisUtil.exists(snapshotRedisKey);
                if (exists) {
                    Set<String> snapshotOffsetList = bbRedisUtil.hkeys(snapshotRedisKey);
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
                            bbRedisUtil.hdel(snapshotRedisKey, ignoreSnapshotOffset);
                        }
                    }

                }
            }
        }
    }
}