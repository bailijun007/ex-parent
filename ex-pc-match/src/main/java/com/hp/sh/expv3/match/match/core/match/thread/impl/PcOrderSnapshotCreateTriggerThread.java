/**
 * @author 10086
 * @date 2019/10/25
 */
package com.hp.sh.expv3.match.match.core.match.thread.impl;

import com.hp.sh.expv3.match.component.notify.PcOrderMqNotify;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.util.PcUtil;
import com.hp.sh.expv3.match.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PcOrderSnapshotCreateTriggerThread {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PcOrderMqNotify pcOrderMqNotify;

    @Autowired
    @Qualifier("threadManagerPcMatchImpl")
    private IThreadManager iThreadManager;

    @Scheduled(initialDelay = 5000, fixedDelay = 3600000)
    private void process() {
        trigger();
    }

    public void trigger() {
        if (null != iThreadManager.getWorkerKeys() && iThreadManager.getWorkerKeys().size() > 0) {
            for (String assetSymbol : iThreadManager.getWorkerKeys()) {
                Tuple2<String, String> assetSymbolTuple = PcUtil.splitAssetAndSymbol(assetSymbol);
                pcOrderMqNotify.sendOrderSnapshotTrigger(assetSymbolTuple.first, assetSymbolTuple.second);
            }
        }
    }

    public void triggerBookReset(String asset, String symbol) {
        String assetAndSymbol = PcUtil.concatAssetAndSymbol(null, asset, symbol);
        boolean ok = false;
        if (null != iThreadManager.getWorkerKeys() && iThreadManager.getWorkerKeys().size() > 0) {
            for (String assetSymbol : iThreadManager.getWorkerKeys()) {
                if (assetAndSymbol.equalsIgnoreCase(assetSymbol)) {
                    pcOrderMqNotify.sendOrderBookResetTrigger(asset, symbol);
                    ok = true;
                }
            }
        }
        if (!ok) {
            logger.warn("book reset not matched {},{}", asset, symbol);
        }
    }
}