/**
 * @author 10086
 * @date 2019/10/24
 */
package com.hp.sh.expv3.match.match.core.matched.task.impl;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.config.setting.BbmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.BbmatchConst;
import com.hp.sh.expv3.match.match.core.match.task.service.BbOrderBookEventService;
import com.hp.sh.expv3.match.match.core.match.thread.BbMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.match.vo.BbOrderMatchResultVo;
import com.hp.sh.expv3.match.match.core.matched.task.*;
import com.hp.sh.expv3.match.match.core.matched.task.def.BbMatchedTaskService;
import com.hp.sh.expv3.match.msg.BookMsgDto.BookEntry;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import com.hp.sh.expv3.match.util.BbOrder4MatchBoUtil;
import com.hp.sh.expv3.match.util.RedisKeyUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class BbMatchedTaskServiceImpl implements BbMatchedTaskService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BbOrderBookEventService bbOrderBookEventService;

    /**
     * new:
     * (matchList | orderNew ) , bookUpdateList,bookResetFlag
     * cancel:
     * bookUpdateList,bookResetFlag , order2Cancel:[accountId,orderId,cancelDeltaAmt]
     * bookReset
     * bookUpdateList,bookResetFlag,
     */
    @Override
    public BbMatchedInitTask buildMatchedInitTask(String assetSymbol, String asset, String symbol) {
        BbMatchedInitTask task = applicationContext.getBean(BbMatchedInitTask.class);
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setAssetSymbol(assetSymbol);
        return task;
    }

    private void baseSet(BbMatchedBaseTask task, String asset, String symbol, String assetSymbol, long currentMsgOffset, String currentMsgId, IThreadWorker matchedThreadWorker) {
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setAssetSymbol(assetSymbol);
        task.setCurrentMsgOffset(currentMsgOffset);
        task.setMatchedThreadWorker(matchedThreadWorker);
        task.setCurrentMsgId(currentMsgId);
    }

    @Override
    public void addMatchedBookResetTask(BbMatchHandlerContext context, List<BookEntry> entries, BigDecimal lastPrice, long currentMsgOffset, String currentMsgId) {
        BbMatchedBookResetTask task = applicationContext.getBean(BbMatchedBookResetTask.class);
        baseSet(task, context.getAsset(), context.getSymbol(), context.getAssetSymbol(), currentMsgOffset, currentMsgId, context.matchedThreadWorker);
        task.setBookUpdateList(entries);
        task.setLastPrice(lastPrice);
        context.matchedThreadWorker.addTask(task);
        context.setSentMqOffset(currentMsgOffset);
    }

    @Override
    public void logQueueSize(String asset, String symbol, IThreadWorker matchedThreadWorker, BbMatchedBaseTask currentMatchedTask) {
        Runnable headTask = matchedThreadWorker.getTasks().peek();
        long headOffset = (null == headTask) ? currentMatchedTask.getCurrentMsgOffset() : ((BbMatchedBaseTask) headTask).getCurrentMsgOffset();
        long endOffset = currentMatchedTask.getCurrentMsgOffset();
        long queueSize = matchedThreadWorker.getTaskCount(); // 队列长度

        String redisKey = RedisKeyUtil.buildBbOrderMatchedQueueRedisKey(bbmatchRedisKeySetting.getBbOrderMatchedQueueRedisKeyPattern(), asset, symbol);
        bbRedisUtil.hmset(new HashMap<String, String>() {
                              {
                                  put(bbmatchRedisKeySetting.getBbOrderMatchedQueueHeadOffsetRedisKeyPattern(), "" + headOffset);
                                  put(bbmatchRedisKeySetting.getBbOrderMatchedQueueEndOffsetRedisKeyPattern(), "" + endOffset);
                                  put(bbmatchRedisKeySetting.getBbOrderMatchedQueueSizeRedisKeyPattern(), "" + queueSize);
                              }
                          },
                redisKey);

    }


    @Autowired
    @Qualifier(BbmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil bbRedisUtil;

    @Autowired
    private BbmatchRedisKeySetting bbmatchRedisKeySetting;

    @Override
    public void addMatchedOrderCancelTask(BbMatchHandlerContext context, long currentMsgOffset, String currentMsgId, long accountId, long orderId, BigDecimal cancelDeltaAmt) {
        // send book,and Rmq Order Canceled
        BbMatchedOrderCancelTask task = applicationContext.getBean(BbMatchedOrderCancelTask.class);
        baseSet(task, context.getAsset(), context.getSymbol(), context.getAssetSymbol(), currentMsgOffset, currentMsgId, context.matchedThreadWorker);
        task.setAccountId(accountId);
        task.setOrderId(orderId);
        task.setCancelDeltaAmt(cancelDeltaAmt);
        context.matchedThreadWorker.addTask(task);
        context.setSentMqOffset(currentMsgOffset);
    }

    @Override
    public void addOrderSnapshotTask(BbMatchHandlerContext context, PriorityQueue<BbOrder4MatchBo> limitBidQueue, PriorityQueue<BbOrder4MatchBo> limitAskQueue, long currentMsgOffset, String currentMsgId) {
        BbMatchedBookSnapshotTask task = applicationContext.getBean(BbMatchedBookSnapshotTask.class);
        baseSet(task, context.getAsset(), context.getSymbol(), context.getAssetSymbol(), currentMsgOffset, currentMsgId, context.matchedThreadWorker);
        task.setLastPrice(context.getLastPrice());

        task.setLimitAskOrders(deepClone(limitAskQueue));
        task.setLimitBidOrders(deepClone(limitBidQueue));

        context.matchedThreadWorker.addTask(task);
        context.setSentMqOffset(currentMsgOffset);
    }

    private List<BbOrder4MatchBo> deepClone(PriorityQueue<BbOrder4MatchBo> os) {
        List<BbOrder4MatchBo> ords = new ArrayList<>();
        if (null != os) {
            for (BbOrder4MatchBo bbOrder4MatchBo : os) {
                BbOrder4MatchBo bo = BbOrder4MatchBoUtil.deepClone(bbOrder4MatchBo);
                ords.add(bo);
            }
        }
        return ords;
    }


    @Override
    public void addMatchedOrderMatchedTask(BbMatchHandlerContext context, long currentMsgOffset, String currentMsgId, BbOrder4MatchBo takerOrder) {
        BbMatchedOrderMatchedTask task = applicationContext.getBean(BbMatchedOrderMatchedTask.class);
        baseSet(task, context.getAsset(), context.getSymbol(), context.getAssetSymbol(), currentMsgOffset, currentMsgId, context.matchedThreadWorker);

        task.setLastPrice(context.getLastPrice());

        BbOrderMatchResultVo matchResult = context.getMatchResult();

        task.setBookUpdateList(matchResult.getBookUpdateList());
        task.setMatchList(matchResult.getMatchList());
        task.setMatchTxId(matchResult.getMatchTxId());
        task.setCancelFlag(matchResult.isCancelFlag());
        task.setCancelNumber(matchResult.getCancelNumber());

        // 必须深复制
        BbOrder4MatchBo currentOrder = BbOrder4MatchBoUtil.deepClone(takerOrder);
        task.setTakerOrder(currentOrder);

        context.matchedThreadWorker.addTask(task);

        context.setSentMqOffset(currentMsgOffset);
    }
}