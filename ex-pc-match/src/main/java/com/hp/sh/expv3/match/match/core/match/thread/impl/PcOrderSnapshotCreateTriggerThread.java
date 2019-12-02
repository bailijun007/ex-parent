/**
 * @author 10086
 * @date 2019/10/25
 */
package com.hp.sh.expv3.match.match.core.match.thread.impl;

import com.hp.sh.expv3.match.config.setting.PcmatchSetting;
import com.hp.sh.expv3.match.util.PcOrderMqNotify;
import com.hp.sh.expv3.match.util.PcUtil;
import com.hp.sh.expv3.match.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PcOrderSnapshotCreateTriggerThread {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PcOrderMqNotify pcOrderMqNotify;

    @Autowired
    private PcmatchSetting pcmatchSetting;

    @Scheduled(initialDelay = 300000, fixedDelay = 3600000)
    private void process() {
        trigger();
    }

    public void trigger() {
        for (String assetSymbol : pcmatchSetting.getSupportAssetSymbol()) {
            Tuple2<String, String> assetSymbolTuple = PcUtil.splitAssetAndSymbol(assetSymbol);
            pcOrderMqNotify.sendOrderSnapshotTrigger(assetSymbolTuple.first, assetSymbolTuple.second);
        }
    }

}