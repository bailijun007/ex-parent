/**
 * @author corleone
 * @date 2018/8/24 0024
 */
package com.hp.sh.expv3.match.thread.def;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public abstract class BaseTask implements ITask {

    private long priority;

    @Override
    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }
}
