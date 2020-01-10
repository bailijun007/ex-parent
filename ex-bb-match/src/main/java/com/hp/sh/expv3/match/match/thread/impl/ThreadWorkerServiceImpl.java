/**
 * @author 10086
 * @date 2019/10/24
 */
package com.hp.sh.expv3.match.match.thread.impl;

import com.hp.sh.expv3.match.match.thread.def.ThreadWorkerService;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ThreadWorkerServiceImpl implements ThreadWorkerService {

    @Autowired
    @Qualifier("threadManagerBbMatchImpl")
    private IThreadManager threadManagerBbMatchImpl;
    @Autowired
    @Qualifier("threadManagerBbMatchedImpl")
    private IThreadManager threadManagerBbMatchedImpl;

    public IThreadWorker getThreadWorker(IThreadManager threadManager, String assetSymbol) {
        IThreadWorker worker = threadManager.getWorker(assetSymbol);

        if (null == worker || (!worker.isReady())) {
            for (int i = 0; i < 6000; i++) { // 自旋
                try {
                    Thread.sleep(100L);
                    worker = threadManager.getWorker(assetSymbol);
                    if (null != worker && worker.isReady()) {
                        break;
                    }
                } catch (Exception e) {
                }
            }
        }

        if (null == worker) {
            throw new RuntimeException();
        }
        return worker;
    }

    @Override
    public IThreadWorker getMatchThreadWorker(String assetSymbol) {
        return getThreadWorker(threadManagerBbMatchImpl, assetSymbol);
    }

    @Override
    public IThreadWorker getMatchedThreadWorker(String assetSymbol) {
        return getThreadWorker(threadManagerBbMatchedImpl, assetSymbol);
    }

}