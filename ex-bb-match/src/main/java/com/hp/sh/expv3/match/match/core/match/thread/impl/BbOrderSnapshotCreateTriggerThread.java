/**
 * @author 10086
 * @date 2019/10/25
 */
package com.hp.sh.expv3.match.match.core.match.thread.impl;

import com.hp.sh.expv3.match.component.notify.BbOrderMqNotify;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.util.BbUtil;
import com.hp.sh.expv3.match.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BbOrderSnapshotCreateTriggerThread {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BbOrderMqNotify bbOrderMqNotify;

    @Autowired
    @Qualifier("threadManagerBbMatchImpl")
    private IThreadManager iThreadManager;

    @Scheduled(initialDelay = 5000, fixedDelay = 3600000)
    private void process() {
        trigger();
    }

    public void trigger() {
        if (null != iThreadManager.getWorkerKeys() && iThreadManager.getWorkerKeys().size() > 0) {
            for (String assetSymbol : iThreadManager.getWorkerKeys()) {
                Tuple2<String, String> assetSymbolTuple = BbUtil.splitAssetAndSymbol(assetSymbol);
                bbOrderMqNotify.sendOrderSnapshotTrigger(assetSymbolTuple.first, assetSymbolTuple.second);
            }
        }
    }

}