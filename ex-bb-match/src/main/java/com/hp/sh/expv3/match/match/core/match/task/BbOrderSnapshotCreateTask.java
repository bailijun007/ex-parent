/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.match.core.match.thread.BbMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.def.BbMatchedTaskService;
import com.hp.sh.expv3.match.thread.def.ITask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class BbOrderSnapshotCreateTask extends BbOrderBaseTask implements ITask {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private String asset;
    private String symbol;

    @Autowired
    private BbMatchedTaskService bbMatchedTaskService;

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public void onSucess() {
    }

    @Override
    public void run() {
        BbMatchHandlerContext context = BbMatchHandlerContext.getLocalContext();
        if (this.getCurrentMsgOffset() > context.getSentMqOffset()) {
            bbMatchedTaskService.addOrderSnapshotTask(context, context.limitBidQueue, context.limitAskQueue, this.getCurrentMsgOffset());
        }
    }
}