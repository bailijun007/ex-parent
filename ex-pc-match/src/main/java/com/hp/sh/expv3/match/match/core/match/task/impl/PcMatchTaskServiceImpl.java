/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.match.task.impl;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.constant.MatchTaskConst;
import com.hp.sh.expv3.match.match.core.match.task.*;
import com.hp.sh.expv3.match.match.core.match.task.def.PcMatchTaskService;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class PcMatchTaskServiceImpl implements PcMatchTaskService, ApplicationContextAware {

    @Override
    public PcOrderInitTask buildPcOrderInitTask(String assetSymbol, String asset, String symbol, IThreadWorker matchedThreadWorker) {
        PcOrderInitTask task = applicationContext.getBean(PcOrderInitTask.class);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_INIT);
        task.setMatchedThreadWorker(matchedThreadWorker);
        return task;
    }

    @Override
    public PcOrderNewTask buildPcOrderNewTask(String assetSymbol, String asset, String symbol, long currentOffset, PcOrder4MatchBo order) {
        PcOrderNewTask task = applicationContext.getBean(PcOrderNewTask.class);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_PENDING_NEW);
        task.setCurrentMsgOffset(currentOffset);
        task.setOrder(order);
        return task;
    }

    @Override
    public PcOrderBookResetTask buildPcOrderBookReset(String assetSymbol, String asset, String symbol, long currentOffset) {
        PcOrderBookResetTask task = applicationContext.getBean(PcOrderBookResetTask.class);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_BOOK_RESET);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setCurrentMsgOffset(currentOffset);
        return task;
    }

    @Override
    public PcOrderCancelTask buildPcOrderCancelTask(String assetSymbol, String asset, String symbol, long currentOffset, long accountId, long orderId) {
        PcOrderCancelTask task = applicationContext.getBean(PcOrderCancelTask.class);
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
    public PcOrderCancel4LiqTask buildPcOrderCancelByLiqTask(String assetSymbol, String asset, String symbol, long currentOffset, PcPosLockedMqMsgDto msg) {
        PcOrderCancel4LiqTask task = applicationContext.getBean(PcOrderCancel4LiqTask.class);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setMsg(msg);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_CANCEL_BY_LIQ);
        task.setCurrentMsgOffset(currentOffset);
        return task;
    }

    @Override
    public PcOrderSnapshotCreateTask buildOrderSnapshotTask(String assetSymbol, String asset, String symbol, long currentOffset) {
        PcOrderSnapshotCreateTask task = applicationContext.getBean(PcOrderSnapshotCreateTask.class);
        task.setAssetSymbol(assetSymbol);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setPriority(MatchTaskConst.PRIORITY_ORDER_INIT);
        task.setCurrentMsgOffset(currentOffset);
        return task;
    }

    @Override
    public PcOrderRebaseTask buildOrderRebaseTask(String assetSymbol, String asset, String symbol, long currentOffset) {
        PcOrderRebaseTask task = applicationContext.getBean(PcOrderRebaseTask.class);
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