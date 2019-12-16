/**
 * @author 10086
 * @date 2019/10/24
 */
package com.hp.sh.expv3.match.match.core.matched.task.impl;

import com.hp.sh.expv3.match.bo.PcOrder2CancelBo;
import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.bo.PcOrderNotMatchedBo;
import com.hp.sh.expv3.match.enums.PcOrderTypeEnum;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.matched.task.*;
import com.hp.sh.expv3.match.match.core.matched.task.def.PcMatchedTaskService;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import com.hp.sh.expv3.match.util.PcOrder4MatchBoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class PcMatchedTaskServiceImpl implements PcMatchedTaskService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * new:
     * (matchList | orderNew ) , bookUpdateList,bookResetFlag
     * cancel:
     * bookUpdateList,bookResetFlag , order2Cancel:[accountId,orderId,cancelDeltaAmt]
     * bookReset
     * bookUpdateList,bookResetFlag,
     */
    @Override
    public PcMatchedInitTask buildMatchedInitTask(String assetSymbol, String asset, String symbol) {
        PcMatchedInitTask task = applicationContext.getBean(PcMatchedInitTask.class);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setAssetSymbol(assetSymbol);
        return task;
    }

    @Override
    public void addMatchedBookResetTask(PcMatchHandlerContext context, long currentMsgOffset) {
        PcMatchedBookResetTask task = applicationContext.getBean(PcMatchedBookResetTask.class);
        task.setAsset(context.getAsset());
        task.setSymbol(context.getSymbol());
        task.setAssetSymbol(context.getAssetSymbol());
        task.setBookUpdateList(context.getBookUpdateList());
        task.setCurrentMsgOffset(currentMsgOffset);
        context.matchedThreadWorker.addTask(task);
    }

    @Override
    public void addOrderSnapshotTask(PcMatchHandlerContext context, long currentMsgOffset) {

        PcMatchedBookSnapshotTask task = applicationContext.getBean(PcMatchedBookSnapshotTask.class);
        task.setAsset(context.getAsset());
        task.setSymbol(context.getSymbol());
        task.setAssetSymbol(context.getAssetSymbol());

        List<PcOrder4MatchBo> asks = new ArrayList<>();
        for (PcOrder4MatchBo pcOrder4MatchBo : context.limitAskQueue) {
            PcOrder4MatchBo bo = PcOrder4MatchBoUtil.deepClone(pcOrder4MatchBo);
            asks.add(bo);
        }

        List<PcOrder4MatchBo> bids = new ArrayList<>();
        for (PcOrder4MatchBo pcOrder4MatchBo : context.limitBidQueue) {
            PcOrder4MatchBo bo = PcOrder4MatchBoUtil.deepClone(pcOrder4MatchBo);
            bids.add(bo);
        }

        task.setLimitAskOrders(asks);
        task.setLimitBidOrders(bids);

        task.setLastPrice(context.getLastPrice());
        task.setCurrentMsgOffset(currentMsgOffset);

        context.matchedThreadWorker.addTask(task);
    }

    @Override
    public void addMatchedOrderCancelByLiqTask(PcMatchHandlerContext context, long currentMsgOffset, Collection<PcOrder4MatchBo> order2CancelByLiq, PcPosLockedMqMsgDto msg) {
// 此处 orders 可能为空

        PcMatchedOrderCancelByLiqTask task = applicationContext.getBean(PcMatchedOrderCancelByLiqTask.class);
        task.setAsset(context.getAsset());
        task.setSymbol(context.getSymbol());
        task.setAssetSymbol(context.getAssetSymbol());
        task.setCurrentMsgOffset(currentMsgOffset);
        task.setOrders(order2CancelByLiq);
        task.setMsg(msg);
        context.matchedThreadWorker.addTask(task);

    }

    @Override
    public void addMatchedOrderMatchedTask(PcMatchHandlerContext context, long currentMsgOffset, PcOrder4MatchBo takerOrder) {
        PcMatchedOrderMatchedTask task = applicationContext.getBean(PcMatchedOrderMatchedTask.class);
        task.setAsset(context.getAsset());
        task.setSymbol(context.getSymbol());
        task.setAssetSymbol(context.getAssetSymbol());
        task.setBookUpdateList(context.getBookUpdateList());
        task.setTradeList(context.getTradeList());
        task.setLastPrice(context.getLastPrice());
        task.setMatchTxId(context.getMatchTxId());
        task.setCurrentMsgOffset(currentMsgOffset);
        task.setTakerOrder(takerOrder);

        PcOrder4MatchBo order = context.getOrderNew();
        if (null != order) {
            if (PcOrderTypeEnum.MARKET.getCode() == order.getOrderType()) {
                PcOrder2CancelBo order2CancelBo = new PcOrder2CancelBo();
                order2CancelBo.setAsset(order.getAsset());
                order2CancelBo.setSymbol(order.getSymbol());
                order2CancelBo.setAccountId(order.getAccountId());
                order2CancelBo.setOrderId(order.getOrderId());
                order2CancelBo.setCancelNumber(order.getNumber().subtract(order.getFilledNumber()));
                task.setCancelTakerOrder(order2CancelBo);
            } else if (PcOrderTypeEnum.LIMIT.getCode() == order.getOrderType()) {
                if (order.getFilledNumber().compareTo(BigDecimal.ZERO) == 0) {
                    // 若完全没有成交
                    PcOrderNotMatchedBo pcOrderNotMatchedBo = new PcOrderNotMatchedBo();
                    pcOrderNotMatchedBo.setAsset(order.getAsset());
                    pcOrderNotMatchedBo.setSymbol(order.getSymbol());
                    pcOrderNotMatchedBo.setAccountId(order.getAccountId());
                    pcOrderNotMatchedBo.setOrderId(order.getOrderId());
                    task.setNotMatchedTakerOrder(pcOrderNotMatchedBo);
                }
            }
        }
        context.matchedThreadWorker.addTask(task);
    }

    @Override
    public void addMatchedOrderCancelTask(PcMatchHandlerContext context, long currentMsgOffset, long accountId, long orderId, BigDecimal cancelDeltaAmt) {
        // send book,and Rmq Order Canceled
        PcMatchedOrderCancelTask task = applicationContext.getBean(PcMatchedOrderCancelTask.class);
        task.setAsset(context.getAsset());
        task.setSymbol(context.getSymbol());
        task.setAssetSymbol(context.getAssetSymbol());
        task.setAccountId(accountId);
        task.setOrderId(orderId);
        task.setCurrentMsgOffset(currentMsgOffset);
        task.setCancelDeltaAmt(cancelDeltaAmt);
        context.matchedThreadWorker.addTask(task);
    }

}
