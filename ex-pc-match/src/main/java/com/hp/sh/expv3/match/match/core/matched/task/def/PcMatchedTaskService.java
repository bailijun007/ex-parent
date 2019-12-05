/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.matched.task.def;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.PcMatchedInitTask;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;

import java.math.BigDecimal;
import java.util.Collection;

public interface PcMatchedTaskService {

    PcMatchedInitTask buildMatchedInitTask(String assetSymbol, String asset, String symbol);

    void addMatchedBookResetTask(PcMatchHandlerContext context, long currentMsgOffset);

    void addOrderSnapshotTask(PcMatchHandlerContext context, long currentMsgOffset);

    void addMatchedOrderCancelByLiqTask(PcMatchHandlerContext context, long currentMsgOffset, Collection<PcOrder4MatchBo> order2CancelByLiq, PcPosLockedMqMsgDto msg);

    void addMatchedOrderMatchedTask(PcMatchHandlerContext context, long currentMsgOffset, PcOrder4MatchBo takerOrder);

    void addMatchedOrderCancelTask(PcMatchHandlerContext context, long currentMsgOffset, long accountId, long orderId, BigDecimal cancelDeltaAmt);

}