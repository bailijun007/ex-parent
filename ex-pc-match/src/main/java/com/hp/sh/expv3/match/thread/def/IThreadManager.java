package com.hp.sh.expv3.match.thread.def;

public interface IThreadManager {

    IThreadWorker addWorker(String assetSymbol, ITask initTask);

    void waitAllReady();

    boolean safeStopWorker(String assetSymbol);

    void safeStop();

    IThreadWorker getWorker(String assetSymbol);

}