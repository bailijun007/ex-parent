/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.match.core.match.task.service.PcOrderBookEventService;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.def.PcMatchedTaskService;
import com.hp.sh.expv3.match.msg.BookMsgDto.BookEntry;
import com.hp.sh.expv3.match.thread.def.ITask;
import com.hp.sh.expv3.match.util.DecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
            long now = System.currentTimeMillis();
            if (context.lastBookResetTimeInMs == null || context.lastBookResetTimeInMs + 30000 < now) {
                PcOrder4MatchBo maxBidLimit = context.limitBidQueue.peek();
                BigDecimal maxBidPrice = (null == maxBidLimit) ? null : maxBidLimit.getPrice();
                PcOrder4MatchBo minAskLimit = context.limitAskQueue.peek();
                BigDecimal minAskPrice = (null == minAskLimit) ? null : minAskLimit.getPrice();
                logger.info("{},currentOffset:{},contextOffset:{},limitBid:{},limitAsk:{},maxBidPrice:{},minAskPrice:{},lastBookResetMs:{},now:{}",
                        now, this.getCurrentMsgOffset(), context.getSentMqOffset(),
                        context.limitBidQueue.size(), context.limitAskQueue.size(),
                        DecimalUtil.toTrimLiteral(maxBidPrice),
                        DecimalUtil.toTrimLiteral(minAskPrice),
                        context.lastBookResetTimeInMs, now
                );
                if (maxBidPrice.compareTo(minAskPrice) >= 0) {
                    logger.error("bid ask price cross:maxBidPrice:{},minAskPrice:{}",
                            DecimalUtil.toTrimLiteral(maxBidPrice),
                            DecimalUtil.toTrimLiteral(minAskPrice));
                }
                List<BookEntry> entries = pcOrderBookEventService.buildBookEntry(context.limitAskQueue, context.limitBidQueue);
                pcMatchedTaskService.addMatchedBookResetTask(context, entries, context.getLastPrice(), this.getCurrentMsgOffset(), this.getCurrentMsgId());
                context.lastBookResetTimeInMs = now;
            }
        }

    }

}