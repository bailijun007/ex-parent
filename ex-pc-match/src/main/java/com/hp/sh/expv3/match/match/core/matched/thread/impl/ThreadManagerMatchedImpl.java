/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.matched.thread.impl;

import com.google.common.collect.ImmutableSet;
import com.hp.sh.expv3.match.thread.def.ITask;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service("threadManagerPcMatchedImpl")
public class ThreadManagerMatchedImpl implements IThreadManager, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    Map<String, IThreadWorker> workerMap = new ConcurrentHashMap<>();

    @Override
    public synchronized IThreadWorker addWorker(String assetSymbol, ITask initTask) {
        if (workerMap.containsKey(assetSymbol)) {
            return null;
        }
        ThreadWorkerMatchedImpl worker = this.applicationContext.getBean(ThreadWorkerMatchedImpl.class, this);
        workerMap.put(assetSymbol, worker);
        worker.start(assetSymbol, initTask);
        return worker;
    }

    @Override
    public synchronized boolean safeStopWorker(String assetSymbol) {
        if (!workerMap.containsKey(assetSymbol)) {
            return false;
        }
        IThreadWorker worker = workerMap.remove(assetSymbol);
        worker.stop();
        return true;
    }

    @Override
    public void safeStop() {
        IThreadWorker[] workers = new IThreadWorker[workerMap.size()];
        workerMap.values().toArray(workers);
        workerMap.clear();
        for (IThreadWorker worker : workers) {
            worker.stop();
        }
    }

    @Override
    public void waitAllReady() {
        IThreadWorker[] workers = new IThreadWorker[workerMap.size()];
        workerMap.values().toArray(workers);
        for (IThreadWorker worker : workers) {
            if (!worker.isReady()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public IThreadWorker getWorker(String assetSymbol) {
        return workerMap.get(assetSymbol);
    }

    @Override
    public Set<String> getWorkerKeys() {
        return ImmutableSet.copyOf(this.workerMap.keySet());
    }
}