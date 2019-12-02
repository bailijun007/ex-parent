package com.hp.sh.expv3.match.thread.def;

import java.util.concurrent.BlockingQueue;

public interface IThreadWorker {

    <T extends ITask> void start(String symbol, T initTask);

    void stop();

    boolean isReady();

    String getAssetSymbol();

    <T extends ITask> void addTask(T task);

    void cleanTask();

    int getTaskCount();

    <T extends Runnable> BlockingQueue<T> getTasks();

}