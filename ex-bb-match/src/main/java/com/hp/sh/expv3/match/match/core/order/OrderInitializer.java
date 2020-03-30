/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.order;

import com.hp.sh.expv3.match.component.MatchSupportContractService;
import com.hp.sh.expv3.match.config.setting.BbmatchSetting;
import com.hp.sh.expv3.match.match.core.match.task.BbOrderInitTask;
import com.hp.sh.expv3.match.match.core.match.task.def.BbMatchTaskService;
import com.hp.sh.expv3.match.match.core.matched.task.BbMatchedInitTask;
import com.hp.sh.expv3.match.match.core.matched.task.def.BbMatchedTaskService;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import com.hp.sh.expv3.match.util.BbUtil;
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
    @Qualifier("threadManagerBbMatchImpl")
    private IThreadManager threadManagerBbMatchImpl;

    @Autowired
    @Qualifier("threadManagerBbMatchedImpl")
    private IThreadManager threadManagerBbMatchedImpl;

    @Autowired
    private BbmatchSetting bbmatchSetting;

    @Autowired
    private BbMatchTaskService bbMatchTaskService;
    @Autowired
    private BbMatchedTaskService bbMatchedTaskService;

    @Autowired
    private MatchSupportContractService matchSupportContractService;
//    @Autowired
//    @Qualifier("threadManagerBbMatchImpl")
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

                if (threadManagerBbMatchImpl.getWorkerKeys().contains(assetSymbol)) {
                    // 已启动的，忽略
                    continue;
                }

                // build init task
                Tuple2<String, String> assetSymbolTuple = BbUtil.splitAssetAndSymbol(assetSymbol);
                String asset = assetSymbolTuple.first;
                String symbol = assetSymbolTuple.second;
                BbMatchedInitTask matchedInitTask = bbMatchedTaskService.buildMatchedInitTask(assetSymbol, asset, symbol);
                IThreadWorker matchedThreadWorker = threadManagerBbMatchedImpl.addWorker(assetSymbol, matchedInitTask);

                BbOrderInitTask task = bbMatchTaskService.buildBbOrderInitTask(assetSymbol, asset, symbol, matchedThreadWorker);
                threadManagerBbMatchImpl.addWorker(assetSymbol, task);
            }
        }
        return true;
    }
}