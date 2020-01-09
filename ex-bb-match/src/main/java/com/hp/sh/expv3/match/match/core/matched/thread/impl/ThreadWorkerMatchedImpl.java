/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.matched.thread.impl;

import com.hp.sh.expv3.match.thread.def.ITask;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Scope("prototype")
public class ThreadWorkerMatchedImpl implements IThreadWorker, Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    String assetSymbol;
    IThreadManager manager;
    Thread thread = null;
    ITask initTask = null;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public AtomicBoolean getIsRunning() {
        return isRunning;
    }

    //        BlockingQueue<ITask> queue = new PriorityBlockingQueue<>(10240, Comparator.comparingLong(ITask::getPriority));
    BlockingQueue<ITask> queue = new ArrayBlockingQueue<>(102400);

    public ThreadWorkerMatchedImpl(IThreadManager manager) {
        this.manager = manager;
    }

    @Override
    public void start(String assetSymbol, ITask initTask) {
        this.assetSymbol = assetSymbol;
        isRunning.set(false);
        this.initTask = initTask;
        thread = new Thread(this) {
            @Override
            public void setUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
                super.setUncaughtExceptionHandler((Thread t, Throwable e) -> logger.error("^^^" + t.getName() + " going to killed.", e));
            }
        };
        thread.start();
    }

    @Override
    public void stop() {
        logger.warn(thread.getName() + " going to stop");
        isRunning.set(false);
        addTask(new ITask() {
            @Override
            public void onSucess() {
            }

            @Override
            public boolean onError(Exception ex) {
                return false;
            }

            @Override
            public long getPriority() {
                return 0;
            }

            @Override
            public void run() {
            }
        });
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.warn(thread.getName() + " stoped");
    }

    @Override
    public boolean isReady() {
        return isRunning.get();
    }

    @Override
    public String getAssetSymbol() {
        return assetSymbol;
    }

    @Override
    public void addTask(ITask task) {
        queue.offer(task);
    }

    @Override
    public void cleanTask() {
        queue.clear();
    }

    @Override
    public int getTaskCount() {
        return queue.size();
    }

    @Override
    public void run() {
        try {
            init();
            loop();
        } finally {
            onQuit();
        }
    }

    void init() {
        // notify zookepper

        if (initTask != null) {
            doTask(initTask);
            initTask = null;
        }
        isRunning.set(true);
    }

    void loop() {
        while (isRunning.get()) {
            ITask obj = null;
            try {
                obj = queue.poll(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
            }
            if (obj != null) {
                doTask(obj);
            }
        }
        while (true) {
            Object obj = null;
            try {
                obj = queue.poll(0, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (obj == null) break;
            doTask((ITask) obj);
        }
    }

    void doTask(ITask task) {
        if (task == null) return;
        try {
            task.run();
            task.onSucess();
        } catch (Exception e) {
            logger.warn(task.getClass().getSimpleName() + ":", e);
            try {
                if (task.onError(e)) stop();
            } catch (Exception ex) {
                logger.warn(task.getClass().getSimpleName() + ":", ex);
                stop();
            }
        }
    }

    void onQuit() {
        logger.warn(thread.getName() + " quit");
    }

    @Override
    public BlockingQueue<ITask> getTasks() {
        return queue;
    }
}
