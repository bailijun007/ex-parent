/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task.def;


import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.match.core.match.task.*;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;

public interface BbMatchTaskService {

    BbOrderInitTask buildBbOrderInitTask(String assetSymbol, String asset, String symbol, IThreadWorker matchedThreadWorker);

    BbOrderNewTask buildBbOrderNewTask(String assetSymbol, String asset, String symbol, long currentOffset, String currentMsgId, BbOrder4MatchBo order);

    BbOrderBookResetTask buildBbOrderBookReset(String assetSymbol, String asset, String symbol, long currentOffset, String currentMsgId);

    BbOrderCancelTask buildBbOrderCancelTask(String assetSymbol, String asset, String symbol, long currentOffset, String currentMsgId, long accountId, long orderId);

    BbOrderSnapshotCreateTask buildOrderSnapshotTask(String assetSymbol, String asset, String symbol, long currentOffset, String currentMsgId);

    BbOrderRebaseTask buildOrderRebaseTask(String assetSymbol, String asset, String symbol, long currentOffset, String currentMsgId);

}