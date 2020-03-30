/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.match.handler;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.bo.BbMatchBo;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.BbOrderTimeInForceEnum;
import com.hp.sh.expv3.match.enums.BbOrderTypeEnum;
import com.hp.sh.expv3.match.match.core.match.thread.BbMatchHandlerContext;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.util.BbUtil;
import com.hp.sh.expv3.match.util.BidUtil;
import com.hp.sh.expv3.match.util.DecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public abstract class BbOrderHandler implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    final Logger logger = LoggerFactory.getLogger(getClass());

    public void process(BbMatchHandlerContext matchHandlerContext, BbOrder4MatchBo order) {
        if (matchHandlerContext.allOpenOrders.containsKey(order.getOrderId())) {
            // 已存在，忽略
        } else if (isOrderComplete(order)) {
            // 已完成的，忽略
        } else {
            matchHandlerContext.allOpenOrders.put(order.getOrderId(), order);
            match(matchHandlerContext, order);
            checkOrder(matchHandlerContext, order);
        }
    }

    void match(BbMatchHandlerContext matchHandlerContext, BbOrder4MatchBo taker) {
        int bidFlag = taker.getBidFlag();
        PriorityQueue<BbOrder4MatchBo> takerQueue = matchHandlerContext.getQueue(bidFlag);
        PriorityQueue<BbOrder4MatchBo> makerLimitQueue = matchHandlerContext.getOppositeQueue(bidFlag);

        if (null == makerLimitQueue || makerLimitQueue.isEmpty()) { // 没对手单
        } else { // 有对手单
            matchLimit(matchHandlerContext, makerLimitQueue, taker);
            if (isOrderComplete(taker)) {
                completeOrder(matchHandlerContext, taker);
                return;
            }
        }
        // 后续有其他的委托类型，比如市价，可以在这里增加新的逻辑
        if (!isOrderComplete(taker)) {
            handleTakerNotFinishedOrder(matchHandlerContext, taker, takerQueue);
        }
    }

    public void checkOrder(BbMatchHandlerContext matchHandlerContext, BbOrder4MatchBo order) {
        if (null == matchHandlerContext.limitBidQueue || matchHandlerContext.limitBidQueue.isEmpty()) {
            return;
        }
        if (null == matchHandlerContext.limitAskQueue || matchHandlerContext.limitAskQueue.isEmpty()) {
            return;
        }

        if (matchHandlerContext.limitAskQueue.size() + matchHandlerContext.limitBidQueue.size() != matchHandlerContext.allOpenOrders.size()) {
            logger.error("bug=======================bid:{}+ask:{} !={},oid:{} {},u:{},{}@{},exchanged:{} after matcher"
                    , matchHandlerContext.limitAskQueue.size(),
                    matchHandlerContext.limitBidQueue.size(),
                    matchHandlerContext.allOpenOrders.size()
                    , order.getOrderId(), BidUtil.getBidDesc(order.getBidFlag()), order.getAccountId(), DecimalUtil.toTrimLiteral(order.getNumber()), DecimalUtil.toTrimLiteral(order.getPrice()), DecimalUtil.toTrimLiteral(order.getFilledNumber()));

            Set<Long> askOrderId = new HashSet<>();
            logger.error("=======limit ask queue:====================");
            if (null != matchHandlerContext.limitAskQueue && !matchHandlerContext.limitAskQueue.isEmpty()) {
                for (BbOrder4MatchBo orderBo : matchHandlerContext.limitAskQueue) {
                    logger.error("oid:{} {},u:{},{}@{}", orderBo.getOrderId(), BidUtil.getBidDesc(orderBo.getBidFlag()), orderBo.getAccountId(), DecimalUtil.toTrimLiteral(orderBo.getNumber()), DecimalUtil.toTrimLiteral(orderBo.getPrice()));
                    if (!matchHandlerContext.allOpenOrders.containsKey(orderBo.getOrderId())) {
                        logger.error("ask oid:{} not in map", orderBo.getOrderId());
                    }
                    askOrderId.add(orderBo.getOrderId());
                }
            }

            Set<Long> bidOrderId = new HashSet<>();
            logger.error("=======limit bid queue:====================");

            for (BbOrder4MatchBo orderBo : matchHandlerContext.limitBidQueue) {
                logger.error("oid:{} {},u:{},{}@{}", orderBo.getOrderId(), BidUtil.getBidDesc(orderBo.getBidFlag()), orderBo.getAccountId(), DecimalUtil.toTrimLiteral(orderBo.getNumber()), DecimalUtil.toTrimLiteral(orderBo.getPrice()));
                if (!matchHandlerContext.allOpenOrders.containsKey(orderBo.getOrderId())) {
                    logger.error("bid oid:{} not in map", orderBo.getOrderId());
                }
                bidOrderId.add(orderBo.getOrderId());
            }

            logger.error("=======all orders in map:====================");
            for (BbOrder4MatchBo orderBo : matchHandlerContext.allOpenOrders.values()) {
                logger.error("oid:{} {},u:{},{}@{}", orderBo.getOrderId(), BidUtil.getBidDesc(orderBo.getBidFlag()), orderBo.getAccountId(), DecimalUtil.toTrimLiteral(orderBo.getNumber()), DecimalUtil.toTrimLiteral(orderBo.getPrice()));
                if (orderBo.getBidFlag() == CommonConst.BID && !bidOrderId.contains(orderBo.getOrderId())) {
                    logger.error("bid oid:{} not in queue. check!!!", orderBo.getOrderId());
                }
                if (orderBo.getBidFlag() == CommonConst.ASK && !askOrderId.contains(orderBo.getOrderId())) {
                    logger.error("ask oid:{} not in queue. check!!!", orderBo.getOrderId());
                }
            }
            logger.error("=======end====================");
        }

    }

    protected void handleTakerNotFinishedOrder(BbMatchHandlerContext context, BbOrder4MatchBo takerOrder, PriorityQueue<BbOrder4MatchBo> sameSideQueue) {
        if (BbOrderTypeEnum.LIMIT.getCode() == takerOrder.getOrderType()) {
            if (BbOrderTimeInForceEnum.GOOD_TILL_CANCEL.getCode() == takerOrder.getTimeInForce()) {
                continueMatch(context, takerOrder, sameSideQueue);
                BigDecimal displayNumber = BbUtil.calcBookNumber(takerOrder.getNumber(), takerOrder.getFilledNumber(), takerOrder.getDisplayNumber());
                bookUpdate(context, takerOrder.getOrderId(), takerOrder.getBidFlag(), takerOrder.getPrice(), displayNumber);
            } else if (BbOrderTimeInForceEnum.MAKER_ONLY.getCode() == takerOrder.getTimeInForce()) {
                if (context.getMatchResult().isCancelFlag()) {
                    cancelAndComplete(context, takerOrder, sameSideQueue);
                } else {
                    BigDecimal displayNumber = BbUtil.calcBookNumber(takerOrder.getNumber(), takerOrder.getFilledNumber(), takerOrder.getDisplayNumber());
                    bookUpdate(context, takerOrder.getOrderId(), takerOrder.getBidFlag(), takerOrder.getPrice(), displayNumber);
                    continueMatch(context, takerOrder, sameSideQueue);
                }
            } else if (BbOrderTimeInForceEnum.IMMEDIATE_OR_CANCEL.getCode() == takerOrder.getTimeInForce()
                    || BbOrderTimeInForceEnum.FILL_OR_KILL.getCode() == takerOrder.getTimeInForce()) {
                // 这里一定是未全部成交
                cancelAndComplete(context, takerOrder, sameSideQueue);
            }
        } else if (BbOrderTypeEnum.MARKET.getCode() == takerOrder.getOrderType()) {
            cancelAndComplete(context, takerOrder, sameSideQueue);
        }
    }

    private void continueMatch(BbMatchHandlerContext context, BbOrder4MatchBo takerOrder, PriorityQueue<BbOrder4MatchBo> sameSideQueue) {
        sameSideQueue.offer(takerOrder);
    }

    private void cancelAndComplete(BbMatchHandlerContext context, BbOrder4MatchBo takerOrder, PriorityQueue<BbOrder4MatchBo> sameSideQueue) {
        BigDecimal unfilledNumber = BbUtil.calcUnfilledNumber(takerOrder.getNumber(), takerOrder.getFilledNumber());
        context.getMatchResult().setCancelFlag(true);
        context.getMatchResult().setCancelNumber(unfilledNumber);
        completeOrder(context, takerOrder);
    }

    protected void bookUpdate(BbMatchHandlerContext matchHandlerContext, long orderId, int bidFlag, BigDecimal price, BigDecimal bookNumber) {
        BookMsgDto.BookEntry entry = new BookMsgDto.BookEntry(orderId, price, bookNumber, bidFlag);
        if (null == matchHandlerContext.getMatchResult().getBookUpdateList()) {
            matchHandlerContext.getMatchResult().setBookUpdateList(new ArrayList<>());
        }
        matchHandlerContext.getMatchResult().getBookUpdateList().add(entry);
    }

    protected void completeOrder(BbMatchHandlerContext context, BbOrder4MatchBo order) {
        context.allOpenOrders.remove(order.getOrderId());
    }

    /**
     * 判断订单是否完成，如果设置的购买数量到了，就完成了
     *
     * @param order
     * @return
     */
    protected boolean isOrderComplete(BbOrder4MatchBo order) {
        // 不用考虑取消的情况，取消会直接在任务中取消
        return BbUtil.calcUnfilledNumber(order.getNumber(), order.getFilledNumber()).compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 新增撮合结果至缓冲列表
     *
     * @param match
     * @return
     */
    protected void appendMatchResult(BbMatchHandlerContext context, BbMatchBo match) {
        if (null == context.getMatchResult().getMatchList()) {
            context.getMatchResult().setMatchList(new ArrayList<>());
        }
        context.getMatchResult().getMatchList().add(match);
        context.setLastPrice(match.getPrice());
    }

    /**
     * 匹配限价队列
     * 有成交，则在内部需要完成 maker的book notify 的功能
     *
     * @param context
     * @param queue
     * @param taker
     */
    abstract void matchLimit(BbMatchHandlerContext context, PriorityQueue<BbOrder4MatchBo> queue, BbOrder4MatchBo taker);

    public BbMatchBo buildMatch(
            String asset,
            String symbol,
            long tradeId,
            long matchTxId,

            BbOrder4MatchBo takerOrder,
            BbOrder4MatchBo makerOrder,

            BigDecimal price,
            BigDecimal amt,
            long ctime
    ) {

        BbMatchBo trade = new BbMatchBo();
        trade.setId(tradeId);
        trade.setAsset(asset);
        trade.setSymbol(symbol);
        trade.setMatchTxId(matchTxId);

        trade.setPrice(price);
        trade.setNumber(amt);

        trade.setTradeTime(ctime);

        trade.setTkAccountId(takerOrder.getAccountId());
        trade.setTkOrderId(takerOrder.getOrderId());
        trade.setTkBidFlag(takerOrder.getBidFlag());

        trade.setMkAccountId(makerOrder.getAccountId());
        trade.setMkOrderId(makerOrder.getOrderId());

        long now = System.currentTimeMillis();
        trade.setCreated(now);
        trade.setModified(now);

        return trade;
    }

}
