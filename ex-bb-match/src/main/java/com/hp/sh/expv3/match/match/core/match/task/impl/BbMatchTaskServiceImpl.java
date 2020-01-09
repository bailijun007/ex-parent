/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task.impl;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.constant.MatchTaskConst;
import com.hp.sh.expv3.match.match.core.match.task.*;
import com.hp.sh.expv3.match.match.core.match.task.def.BbMatchTaskService;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class BbMatchTaskServiceImpl implements BbMatchTaskService, ApplicationContextAware {

    @Override
    public BbOrderInitTask buildBbOrderInitTask(String assetSymbol, String asset, String symbol, IThreadWorker matchedThreadWorker) {
        BbOrderInitTask task = applicationContext.getBean(BbOrderInitTask.class);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_INIT);
        task.setMatchedThreadWorker(matchedThreadWorker);
        return task;
    }

    @Override
    public BbOrderNewTask buildBbOrderNewTask(String assetSymbol, String asset, String symbol, long currentOffset, BbOrder4MatchBo order) {
        BbOrderNewTask task = applicationContext.getBean(BbOrderNewTask.class);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_PENDING_NEW);
        task.setCurrentMsgOffset(currentOffset);
        task.setOrder(order);
        return task;
    }

    @Override
    public BbOrderBookResetTask buildBbOrderBookReset(String assetSymbol, String asset, String symbol, long currentOffset) {
        BbOrderBookResetTask task = applicationContext.getBean(BbOrderBookResetTask.class);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_BOOK_RESET);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setCurrentMsgOffset(currentOffset);
        return task;
    }

    @Override
    public BbOrderCancelTask buildBbOrderCancelTask(String assetSymbol, String asset, String symbol, long currentOffset, long accountId, long orderId) {
        BbOrderCancelTask task = applicationContext.getBean(BbOrderCancelTask.class);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setAccountId(accountId);
        task.setOrderId(orderId);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_PENDING_CANCEL);
        task.setCurrentMsgOffset(currentOffset);
        return task;
    }

    @Override
    public BbOrderSnapshotCreateTask buildOrderSnapshotTask(String assetSymbol, String asset, String symbol, long currentOffset) {
        BbOrderSnapshotCreateTask task = applicationContext.getBean(BbOrderSnapshotCreateTask.class);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_INIT);
        task.setCurrentMsgOffset(currentOffset);
        return task;
    }

    @Override
    public BbOrderRebaseTask buildOrderRebaseTask(String assetSymbol, String asset, String symbol, long currentOffset) {
        BbOrderRebaseTask task = applicationContext.getBean(BbOrderRebaseTask.class);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_INIT);
        task.setCurrentMsgOffset(currentOffset);
        return task;
    }


    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}