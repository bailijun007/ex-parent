/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.match.handler;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.bo.PcTradeBo;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.PcOrderTypeEnum;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.util.BidUtil;
import com.hp.sh.expv3.match.util.DecimalUtil;
import com.hp.sh.expv3.match.util.PcUtil;
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

public abstract class PcOrderHandler implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    final Logger logger = LoggerFactory.getLogger(getClass());

    public void process(PcMatchHandlerContext matchHandlerContext, PcOrder4MatchBo order) {
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

    void match(PcMatchHandlerContext matchHandlerContext, PcOrder4MatchBo taker) {
        int bidFlag = taker.getBidFlag();
        PriorityQueue<PcOrder4MatchBo> takerQueue = matchHandlerContext.getQueue(bidFlag);
        PriorityQueue<PcOrder4MatchBo> makerLimitQueue = matchHandlerContext.getOppositeQueue(bidFlag);

        if (null == makerLimitQueue || makerLimitQueue.isEmpty()) { // 没对手单
        } else { // 有对手单
            matchLimit(matchHandlerContext, makerLimitQueue, taker);
            if (isOrderComplete(taker)) {
                // taker removed from allOrderMap here
                // maker removed from allOrderMap in detail
                completeOrder(matchHandlerContext, taker);
                return;
            }
        }
        // 后续有其他的委托类型，比如市价，可以在这里增加新的逻辑
        if (!isOrderComplete(taker)) {
            handleTakerNotFinishedOrder(matchHandlerContext, taker, takerQueue);
        }
    }

    public void checkOrder(PcMatchHandlerContext matchHandlerContext, PcOrder4MatchBo order) {

        if (matchHandlerContext.limitAskQueue.size() + matchHandlerContext.limitBidQueue.size() != matchHandlerContext.allOpenOrders.size()) {
            logger.error("bug=======================bid:{}+ask:{} !={},oid:{} {},u:{},{}@{},exchanged:{} after matcher"
                    , matchHandlerContext.limitAskQueue.size(),
                    matchHandlerContext.limitBidQueue.size(),
                    matchHandlerContext.allOpenOrders.size()
                    , order.getOrderId(), BidUtil.getPcBidCloseDesc(order.getBidFlag()), order.getAccountId(), DecimalUtil.toTrimLiteral(order.getNumber()), DecimalUtil.toTrimLiteral(order.getPrice()), DecimalUtil.toTrimLiteral(order.getFilledNumber()));

            Set<Long> askOrderId = new HashSet<>();
            logger.error("=======limit ask queue:====================");
            if (null != matchHandlerContext.limitAskQueue && !matchHandlerContext.limitAskQueue.isEmpty()) {
                for (PcOrder4MatchBo orderBo : matchHandlerContext.limitAskQueue) {
                    logger.error("oid:{} {},u:{},{}@{}", orderBo.getOrderId(), BidUtil.getPcBidCloseDesc(orderBo.getBidFlag()), orderBo.getAccountId(), DecimalUtil.toTrimLiteral(orderBo.getNumber()), DecimalUtil.toTrimLiteral(orderBo.getPrice()));
                    if (!matchHandlerContext.allOpenOrders.containsKey(orderBo.getOrderId())) {
                        logger.error("ask oid:{} not in map", orderBo.getOrderId());
                    }
                    askOrderId.add(orderBo.getOrderId());
                }
            }

            Set<Long> bidOrderId = new HashSet<>();
            logger.error("=======limit bid queue:====================");
            if (null != matchHandlerContext.limitBidQueue && !matchHandlerContext.limitBidQueue.isEmpty()) {
                for (PcOrder4MatchBo orderBo : matchHandlerContext.limitBidQueue) {
                    logger.error("oid:{} {},u:{},{}@{}", orderBo.getOrderId(), BidUtil.getPcBidCloseDesc(orderBo.getBidFlag()), orderBo.getAccountId(), DecimalUtil.toTrimLiteral(orderBo.getNumber()), DecimalUtil.toTrimLiteral(orderBo.getPrice()));
                    if (!matchHandlerContext.allOpenOrders.containsKey(orderBo.getOrderId())) {
                        logger.error("bid oid:{} not in map", orderBo.getOrderId());
                    }
                    bidOrderId.add(orderBo.getOrderId());
                }
            }

            logger.error("=======all orders in map:====================");
            if (null != matchHandlerContext.allOpenOrders && !matchHandlerContext.allOpenOrders.isEmpty()) {
                for (PcOrder4MatchBo orderBo : matchHandlerContext.allOpenOrders.values()) {
                    logger.error("oid:{} {},u:{},{}@{}", orderBo.getOrderId(), BidUtil.getPcBidCloseDesc(orderBo.getBidFlag()), orderBo.getAccountId(), DecimalUtil.toTrimLiteral(orderBo.getNumber()), DecimalUtil.toTrimLiteral(orderBo.getPrice()));
                    if (orderBo.getBidFlag() == CommonConst.BID && !bidOrderId.contains(orderBo.getOrderId())) {
                        logger.error("bid oid:{} not in queue. check!!!", orderBo.getOrderId());
                    }
                    if (orderBo.getBidFlag() == CommonConst.ASK && !askOrderId.contains(orderBo.getOrderId())) {
                        logger.error("ask oid:{} not in queue. check!!!", orderBo.getOrderId());
                    }
                }
            }
            logger.error("=======end====================");
        }
    }

    protected void handleTakerNotFinishedOrder(PcMatchHandlerContext context, PcOrder4MatchBo takerOrder, PriorityQueue<PcOrder4MatchBo> sameSideQueue) {
        if (PcOrderTypeEnum.LIMIT.getCode() == takerOrder.getOrderType()) {
            sameSideQueue.offer(takerOrder);
            BigDecimal bookNumber = PcUtil.calcBookNumber(takerOrder.getNumber(), takerOrder.getFilledNumber(), takerOrder.getDisplayNumber());
            bookUpdate(context, takerOrder.getOrderId(), takerOrder.getBidFlag(), takerOrder.getPrice(), bookNumber);
            context.setOrderNew(takerOrder);
        } else if (PcOrderTypeEnum.MARKET.getCode() == takerOrder.getOrderType()) {
            context.allOpenOrders.remove(takerOrder.getOrderId());
            context.setOrderNew(takerOrder);
        }
//        pcExDef.doOrderNew(context.asset, context.symbol, takerOrder.getAccountId(), takerOrder.getId());
    }

    protected void bookUpdate(PcMatchHandlerContext matchHandlerContext, long orderId, int bidFlag, BigDecimal price, BigDecimal bookNumber) {
        BookMsgDto.BookEntry entry = new BookMsgDto.BookEntry(orderId, price, bookNumber, bidFlag);
        if (null == matchHandlerContext.getBookUpdateList()) {
            matchHandlerContext.setBookUpdateList(new ArrayList<>());
        }
        matchHandlerContext.getBookUpdateList().add(entry);
    }

    protected void completeOrder(PcMatchHandlerContext context, PcOrder4MatchBo order) {
        context.allOpenOrders.remove(order.getOrderId());
    }

    /**
     * 判断订单是否完成，如果设置的购买数量到了，就完成了
     *
     * @param order
     * @return
     */
    protected boolean isOrderComplete(PcOrder4MatchBo order) {
        // 不用考虑取消的情况，取消会直接在任务中取消
        return DecimalUtil.toTrimLiteral(order.getNumber()).equals(DecimalUtil.toTrimLiteral(order.getFilledNumber()));
    }

    /**
     * 新增撮合结果至缓冲列表
     *
     * @param trade
     * @return
     */
    protected void appendMatchResult(PcMatchHandlerContext context, PcTradeBo trade) {
        if (null == context.getTradeList()) {
            context.setTradeList(new ArrayList<>());
        }
        context.getTradeList().add(trade);
    }

    /**
     * 匹配限价队列
     * 有成交，则在内部需要完成 maker的book notify 的功能
     *
     * @param context
     * @param queue
     * @param taker
     */
    abstract void matchLimit(PcMatchHandlerContext context, PriorityQueue<PcOrder4MatchBo> queue, PcOrder4MatchBo taker);

    public PcTradeBo buildTrade(
            String asset,
            String symbol,
            long tradeId,
            long matchTxId,

            PcOrder4MatchBo takerOrder,
            PcOrder4MatchBo makerOrder,

            BigDecimal price,
            BigDecimal amt,
            long ctime
    ) {

        PcTradeBo trade = new PcTradeBo();
        trade.setId(tradeId);
        trade.setAsset(asset);
        trade.setSymbol(symbol);
        trade.setMatchTxId(matchTxId);

//        trade.setVolume(pcVolumeService.calcVolume(amt, price));

        trade.setPrice(price);
        trade.setNumber(amt);

        trade.setTradeTime(ctime);

        trade.setTkAccountId(takerOrder.getAccountId());
        trade.setTkOrderId(takerOrder.getOrderId());
        trade.setTkBidFlag(takerOrder.getBidFlag());
        trade.setTkCloseFlag(takerOrder.getCloseFlag());

        trade.setMkAccountId(makerOrder.getAccountId());
        trade.setMkOrderId(makerOrder.getOrderId());
        trade.setMkCloseFlag(makerOrder.getCloseFlag());

        return trade;
    }

}
