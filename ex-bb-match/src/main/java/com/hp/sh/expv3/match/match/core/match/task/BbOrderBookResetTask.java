/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.match.core.match.task.service.BbOrderBookEventService;
import com.hp.sh.expv3.match.match.core.match.thread.BbMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.def.BbMatchedTaskService;
import com.hp.sh.expv3.match.msg.BookMsgDto.BookEntry;
import com.hp.sh.expv3.match.thread.def.ITask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class BbOrderBookResetTask extends BbOrderBaseTask implements ITask {

    final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private BbOrderBookEventService bbOrderBookEventService;
    @Autowired
    private BbMatchedTaskService bbMatchedTaskService;

    private String asset;
    private String symbol;

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
    public boolean onError(Exception ex) {
        return false;
    }

    @Override
    public void run() {
        BbMatchHandlerContext context = BbMatchHandlerContext.getLocalContext();

        if (this.getCurrentMsgOffset() > context.getSentMqOffset()) {
            List<BookEntry> entries = bbOrderBookEventService.buildBookEntry(context.limitAskQueue, context.limitBidQueue);
            bbMatchedTaskService.addMatchedBookResetTask(context, entries, context.getLastPrice(), this.getCurrentMsgOffset(), this.getCurrentMsgId());
        }

    }

}