/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.match.core.match.task.service.PcOrderBookEventService;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.def.PcMatchedTaskService;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.thread.def.ITask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class PcOrderBookResetTask extends PcOrderBaseTask implements ITask {

    final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PcOrderBookEventService pcOrderBookEventService;
    @Autowired
    private PcMatchedTaskService pcMatchedTaskService;

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
        PcMatchHandlerContext context = PcMatchHandlerContext.getLocalContext();

        if (this.getCurrentMsgOffset() > context.getSentMqOffset()) {

            List<BookMsgDto.BookEntry> entries = pcOrderBookEventService.buildBookEntry(context.limitAskQueue, context.limitBidQueue);
            context.setBookUpdateList(entries);

            pcMatchedTaskService.addMatchedBookResetTask(context, this.getCurrentMsgOffset());
            context.setSentMqOffset(this.getCurrentMsgOffset());
        }
        context.clear();
    }

}