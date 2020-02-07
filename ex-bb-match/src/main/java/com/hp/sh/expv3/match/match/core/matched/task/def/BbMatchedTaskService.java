/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.matched.task.def;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.match.core.match.thread.BbMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.BbMatchedBaseTask;
import com.hp.sh.expv3.match.match.core.matched.task.BbMatchedInitTask;
import com.hp.sh.expv3.match.msg.BookMsgDto.BookEntry;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;

import java.math.BigDecimal;
import java.util.List;
import java.util.PriorityQueue;

public interface BbMatchedTaskService {

    BbMatchedInitTask buildMatchedInitTask(String assetSymbol, String asset, String symbol);

    void addMatchedBookResetTask(BbMatchHandlerContext context, List<BookEntry> entries, BigDecimal lastPrice, long currentMsgOffset, String currentMsgId);

    void addOrderSnapshotTask(BbMatchHandlerContext context, PriorityQueue<BbOrder4MatchBo> limitBidQueue, PriorityQueue<BbOrder4MatchBo> limitAskQueue, long currentMsgOffset, String currentMsgId);

    void addMatchedOrderMatchedTask(BbMatchHandlerContext context, long currentMsgOffset, String currentMsgId, BbOrder4MatchBo takerOrder);

    void addMatchedOrderCancelTask(BbMatchHandlerContext context, long currentMsgOffset, String currentMsgId, long accountId, long orderId, BigDecimal cancelDeltaAmt);

    void logQueueSize(String asset, String symbol, IThreadWorker matchedThreadWorker, BbMatchedBaseTask currentMatchedTask);
}