/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task.def;


import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.match.core.match.task.*;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;

public interface PcMatchTaskService {

    PcOrderInitTask buildPcOrderInitTask(String assetSymbol, String asset, String symbol, IThreadWorker matchedThreadWorker);

    PcOrderNewTask buildPcOrderNewTask(String assetSymbol, String asset, String symbol, long currentOffset, PcOrder4MatchBo order);

    PcOrderBookResetTask buildPcOrderBookReset(String assetSymbol, String asset, String symbol, long currentOffset);

    PcOrderCancelTask buildPcOrderCancelTask(String assetSymbol, String asset, String symbol, long currentOffset, long accountId, long orderId);

    PcOrderCancel4LiqTask buildPcOrderCancelByLiqTask(String assetSymbol, String asset, String symbol, long currentOffset, PcPosLockedMqMsgDto msg);

    PcOrderSnapshotCreateTask buildOrderSnapshotTask(String assetSymbol, String asset, String symbol, long currentOffset);

}