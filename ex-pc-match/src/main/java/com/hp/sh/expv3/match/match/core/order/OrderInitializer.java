/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.order;

import com.hp.sh.expv3.match.component.MatchSupportContractService;
import com.hp.sh.expv3.match.config.setting.PcmatchSetting;
import com.hp.sh.expv3.match.match.core.match.task.PcOrderInitTask;
import com.hp.sh.expv3.match.match.core.match.task.def.PcMatchTaskService;
import com.hp.sh.expv3.match.match.core.matched.task.PcMatchedInitTask;
import com.hp.sh.expv3.match.match.core.matched.task.def.PcMatchedTaskService;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import com.hp.sh.expv3.match.util.PcUtil;
import com.hp.sh.expv3.match.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;

@Service
public class OrderInitializer implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("threadManagerPcMatchImpl")
    private IThreadManager threadManagerPcMatchImpl;

    @Autowired
    @Qualifier("threadManagerPcMatchedImpl")
    private IThreadManager threadManagerPcMatchedImpl;

    @Autowired
    private PcmatchSetting pcmatchSetting;

    @Autowired
    private PcMatchTaskService pcMatchTaskService;
    @Autowired
    private PcMatchedTaskService pcMatchedTaskService;

    @Autowired
    private MatchSupportContractService matchSupportContractService;
//    @Autowired
//    @Qualifier("threadManagerPcMatchImpl")
//    private IThreadManager iThreadManager;

    @PostConstruct
    private void init() {
    }

    public synchronized boolean start(boolean initFlag) {

        Set<String> supportAssetSymbol = matchSupportContractService.getSupportAssetSymbol(false);
        if (null == supportAssetSymbol || supportAssetSymbol.isEmpty()) {
            if (initFlag) {
                logger.warn("support assetSymbol empty.pls check!");
            }
        } else {

            for (String assetSymbol : supportAssetSymbol) {

                if (threadManagerPcMatchImpl.getWorkerKeys().contains(assetSymbol)) {
                    // 已启动的，忽略
                    continue;
                }

                // build init task
                Tuple2<String, String> assetSymbolTuple = PcUtil.splitAssetAndSymbol(assetSymbol);
                String asset = assetSymbolTuple.first;
                String symbol = assetSymbolTuple.second;
                PcMatchedInitTask matchedInitTask = pcMatchedTaskService.buildMatchedInitTask(assetSymbol, asset, symbol);
                IThreadWorker matchedThreadWorker = threadManagerPcMatchedImpl.addWorker(assetSymbol, matchedInitTask);

                PcOrderInitTask task = pcMatchTaskService.buildPcOrderInitTask(assetSymbol, asset, symbol, matchedThreadWorker);
                threadManagerPcMatchImpl.addWorker(assetSymbol, task);
            }
        }
        return true;
    }
}