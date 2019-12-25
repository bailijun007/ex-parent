/**
 * @author 10086
 * @date 2019/10/24
 */
package com.hp.sh.expv3.match.match.core.matched.task.impl;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.config.setting.PcmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.PcmatchConst;
import com.hp.sh.expv3.match.match.core.match.task.service.PcOrderBookEventService;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.match.core.match.vo.PcOrderMatchResultVo;
import com.hp.sh.expv3.match.match.core.matched.task.*;
import com.hp.sh.expv3.match.match.core.matched.task.def.PcMatchedTaskService;
import com.hp.sh.expv3.match.mqmsg.PcOrderCancelMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import com.hp.sh.expv3.match.msg.BookMsgDto.BookEntry;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import com.hp.sh.expv3.match.util.PcOrder4MatchBoUtil;
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
import java.util.*;

@Service
public class PcMatchedTaskServiceImpl implements PcMatchedTaskService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PcOrderBookEventService pcOrderBookEventService;

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

    private void baseSet(PcMatchedBaseTask task, String asset, String symbol, String assetSymbol, long currentMsqOffset, IThreadWorker matchedThreadWorker) {
        task.setAsset(asset);
        task.setSymbol(symbol);
        task.setAssetSymbol(assetSymbol);
        task.setCurrentMsgOffset(currentMsqOffset);
        task.setMatchedThreadWorker(matchedThreadWorker);
    }

    @Override
    public void addMatchedBookResetTask(PcMatchHandlerContext context, List<BookEntry> entries, BigDecimal lastPrice, long currentMsgOffset) {
        PcMatchedBookResetTask task = applicationContext.getBean(PcMatchedBookResetTask.class);
        baseSet(task, context.getAsset(), context.getSymbol(), context.getAssetSymbol(), currentMsgOffset, context.matchedThreadWorker);
        task.setBookUpdateList(entries);
        task.setLastPrice(lastPrice);
        context.matchedThreadWorker.addTask(task);
        context.setSentMqOffset(currentMsgOffset);
    }

    @Override
    public void logQueueSize(String asset, String symbol, IThreadWorker matchedThreadWorker, PcMatchedBaseTask currentMatchedTask) {
        Runnable headTask = matchedThreadWorker.getTasks().peek();
        long headOffset = (null == headTask) ? currentMatchedTask.getCurrentMsgOffset() : ((PcMatchedBaseTask) headTask).getCurrentMsgOffset();
        long endOffset = currentMatchedTask.getCurrentMsgOffset();
        long queueSize = matchedThreadWorker.getTaskCount(); // 队列长度

        String redisKey = RedisKeyUtil.buildPcOrderMatchedQueueRedisKey(pcmatchRedisKeySetting.getPcOrderMatchedQueueRedisKeyPattern(), asset, symbol);
        pcRedisUtil.hmset(new HashMap<String, String>() {
                              {
                                  put(pcmatchRedisKeySetting.getPcOrderMatchedQueueHeadOffsetRedisKeyPattern(), "" + headOffset);
                                  put(pcmatchRedisKeySetting.getPcOrderMatchedQueueEndOffsetRedisKeyPattern(), "" + endOffset);
                                  put(pcmatchRedisKeySetting.getPcOrderMatchedQueueSizeRedisKeyPattern(), "" + queueSize);
                              }
                          },
                redisKey);

    }


    @Autowired
    @Qualifier(PcmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil pcRedisUtil;

    @Autowired
    private PcmatchRedisKeySetting pcmatchRedisKeySetting;

    @Override
    public void addMatchedOrderCancelTask(PcMatchHandlerContext context, long currentMsgOffset, long accountId, long orderId, BigDecimal cancelDeltaAmt) {
        // send book,and Rmq Order Canceled
        PcMatchedOrderCancelTask task = applicationContext.getBean(PcMatchedOrderCancelTask.class);
        baseSet(task, context.getAsset(), context.getSymbol(), context.getAssetSymbol(), currentMsgOffset, context.matchedThreadWorker);
        task.setAccountId(accountId);
        task.setOrderId(orderId);
        task.setCancelDeltaAmt(cancelDeltaAmt);
        context.matchedThreadWorker.addTask(task);
        context.setSentMqOffset(currentMsgOffset);
    }

    @Override
    public void addOrderSnapshotTask(PcMatchHandlerContext context, PriorityQueue<PcOrder4MatchBo> limitBidQueue, PriorityQueue<PcOrder4MatchBo> limitAskQueue, long currentMsgOffset) {
        PcMatchedBookSnapshotTask task = applicationContext.getBean(PcMatchedBookSnapshotTask.class);
        baseSet(task, context.getAsset(), context.getSymbol(), context.getAssetSymbol(), currentMsgOffset, context.matchedThreadWorker);
        task.setLastPrice(context.getLastPrice());

        task.setLimitAskOrders(deepClone(limitAskQueue));
        task.setLimitBidOrders(deepClone(limitBidQueue));

        context.matchedThreadWorker.addTask(task);
        context.setSentMqOffset(currentMsgOffset);
    }

    private List<PcOrder4MatchBo> deepClone(PriorityQueue<PcOrder4MatchBo> os) {
        List<PcOrder4MatchBo> ords = new ArrayList<>();
        if (null != os) {
            for (PcOrder4MatchBo pcOrder4MatchBo : os) {
                PcOrder4MatchBo bo = PcOrder4MatchBoUtil.deepClone(pcOrder4MatchBo);
                ords.add(bo);
            }
        }
        return ords;
    }

    @Override
    public void addMatchedOrderCancelByLiqTask(PcMatchHandlerContext context, long currentMsgOffset, Collection<PcOrder4MatchBo> order2CancelByLiq, PcPosLockedMqMsgDto msg) {
        PcMatchedOrderCancelByLiqTask task = applicationContext.getBean(PcMatchedOrderCancelByLiqTask.class);
        String asset = context.getAsset();
        String symbol = context.getSymbol();
        baseSet(task, asset, symbol, context.getAssetSymbol(), currentMsgOffset, context.matchedThreadWorker);

        List<PcOrderCancelMqMsgDto> cancelMqMsgs = new ArrayList<>(8);
        if (null == order2CancelByLiq || order2CancelByLiq.isEmpty()) {
        } else {
            for (PcOrder4MatchBo order : order2CancelByLiq) {
                PcOrderCancelMqMsgDto cancelMqMsgDto = new PcOrderCancelMqMsgDto();
//                cancelMqMsgDto.setAccountId(order.getAccountId());
                cancelMqMsgDto.setOrderId(order.getOrderId());
//                cancelMqMsgDto.setAsset(asset);
//                cancelMqMsgDto.setSymbol(symbol);
                BigDecimal cancelDeltaAmt = order.getNumber().subtract(order.getFilledNumber());
                cancelMqMsgDto.setCancelNumber(cancelDeltaAmt);
                cancelMqMsgs.add(cancelMqMsgDto);
            }

            List<BookEntry> bookEntries = pcOrderBookEventService.buildBookEntry4CancelByLiq(order2CancelByLiq);
            task.setBookEntries(bookEntries);
        }

        task.setMsg(msg);
        context.matchedThreadWorker.addTask(task);

        context.setSentMqOffset(currentMsgOffset);
    }

    @Override
    public void addMatchedOrderMatchedTask(PcMatchHandlerContext context, long currentMsgOffset, PcOrder4MatchBo takerOrder) {
        PcMatchedOrderMatchedTask task = applicationContext.getBean(PcMatchedOrderMatchedTask.class);
        baseSet(task, context.getAsset(), context.getSymbol(), context.getAssetSymbol(), currentMsgOffset, context.matchedThreadWorker);

        task.setLastPrice(context.getLastPrice());

        PcOrderMatchResultVo matchResult = context.getMatchResult();

        task.setBookUpdateList(matchResult.getBookUpdateList());
        task.setTradeList(matchResult.getTradeList());
        task.setMatchTxId(matchResult.getMatchTxId());
        task.setCancelFlag(matchResult.isCancelFlag());
        task.setCancelNumber(matchResult.getCancelNumber());

        // 必须深复制
        PcOrder4MatchBo currentOrder = PcOrder4MatchBoUtil.deepClone(takerOrder);
        task.setTakerOrder(currentOrder);

        context.matchedThreadWorker.addTask(task);

        context.setSentMqOffset(currentMsgOffset);
    }
}