/**
 * @author corleone
 * @date 2018/8/23 0023
 */
package com.hp.sh.expv3.match.thread.def;

public interface ITask extends Runnable {

    void onSucess();

    /**
     * @param ex
     * @return quit on return true
     */
    boolean onError(Exception ex);

    long getPriority();
}