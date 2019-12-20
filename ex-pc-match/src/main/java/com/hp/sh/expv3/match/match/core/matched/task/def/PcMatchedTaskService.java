/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.matched.task.def;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.PcMatchedBaseTask;
import com.hp.sh.expv3.match.match.core.matched.task.PcMatchedInitTask;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import com.hp.sh.expv3.match.msg.BookMsgDto.BookEntry;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

public interface PcMatchedTaskService {

    PcMatchedInitTask buildMatchedInitTask(String assetSymbol, String asset, String symbol);

    void addMatchedBookResetTask(PcMatchHandlerContext context, List<BookEntry> entries, BigDecimal lastPrice, long currentMsgOffset);

    void addOrderSnapshotTask(PcMatchHandlerContext context, PriorityQueue<PcOrder4MatchBo> limitBidQueue, PriorityQueue<PcOrder4MatchBo> limitAskQueue, long currentMsgOffset);

    void addMatchedOrderCancelByLiqTask(PcMatchHandlerContext context, long currentMsgOffset, Collection<PcOrder4MatchBo> order2CancelByLiq, PcPosLockedMqMsgDto msg);

    void addMatchedOrderMatchedTask(PcMatchHandlerContext context, long currentMsgOffset, PcOrder4MatchBo takerOrder);

    void addMatchedOrderCancelTask(PcMatchHandlerContext context, long currentMsgOffset, long accountId, long orderId, BigDecimal cancelDeltaAmt);

    void logQueueSize(String asset, String symbol, IThreadWorker matchedThreadWorker, PcMatchedBaseTask currentMatchedTask);
}