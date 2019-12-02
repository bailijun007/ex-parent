/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.match.thread;


import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.bo.PcTradeBo;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import com.hp.sh.expv3.match.util.PcUtil;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class PcMatchHandlerContext {

    public IThreadWorker matchedThreadWorker;

    // book list
    private List<BookMsgDto.BookEntry> bookUpdateList;

    // order new
    private PcOrder4MatchBo orderNew;

    // trade list
    private List<PcTradeBo> tradeList;

    // last price
    private BigDecimal lastPrice;


    private Boolean PendingNewIgnoreReasonAlreadyInQueue;
    private Boolean PendingNewIgnoreReasonAlreadyCompleted;
    private Boolean PendingCancelIgnoreReasonNotInQueue;

    private long sentMqOffset;
    private long matchTxId;

    public long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(long matchTxId) {
        this.matchTxId = matchTxId;
    }

    public long getSentMqOffset() {
        return sentMqOffset;
    }

    public void setSentMqOffset(long sentMqOffset) {
        this.sentMqOffset = sentMqOffset;
    }

    private String assetSymbol;
    private String asset;
    private String symbol;

    public List<BookMsgDto.BookEntry> getBookUpdateList() {
        return bookUpdateList;
    }

    public void setBookUpdateList(List<BookMsgDto.BookEntry> bookUpdateList) {
        this.bookUpdateList = bookUpdateList;
    }

    public PcOrder4MatchBo getOrderNew() {
        return orderNew;
    }

    public void setOrderNew(PcOrder4MatchBo orderNew) {
        this.orderNew = orderNew;
    }

    public List<PcTradeBo> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<PcTradeBo> tradeList) {
        this.tradeList = tradeList;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Boolean getPendingNewIgnoreReasonAlreadyInQueue() {
        return PendingNewIgnoreReasonAlreadyInQueue;
    }

    public void setPendingNewIgnoreReasonAlreadyInQueue(Boolean pendingNewIgnoreReasonAlreadyInQueue) {
        PendingNewIgnoreReasonAlreadyInQueue = pendingNewIgnoreReasonAlreadyInQueue;
    }

    public Boolean getPendingNewIgnoreReasonAlreadyCompleted() {
        return PendingNewIgnoreReasonAlreadyCompleted;
    }

    public void setPendingNewIgnoreReasonAlreadyCompleted(Boolean pendingNewIgnoreReasonAlreadyCompleted) {
        PendingNewIgnoreReasonAlreadyCompleted = pendingNewIgnoreReasonAlreadyCompleted;
    }

    public Boolean getPendingCancelIgnoreReasonNotInQueue() {
        return PendingCancelIgnoreReasonNotInQueue;
    }

    public void setPendingCancelIgnoreReasonNotInQueue(Boolean pendingCancelIgnoreReasonNotInQueue) {
        PendingCancelIgnoreReasonNotInQueue = pendingCancelIgnoreReasonNotInQueue;
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }

    public void setAssetSymbol(String assetSymbol) {
        this.assetSymbol = assetSymbol;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    static ThreadLocal<PcMatchHandlerContext> threadLocal = ThreadLocal.withInitial(() -> new PcMatchHandlerContext());

    public static PcMatchHandlerContext getLocalContext() {
        return threadLocal.get();
    }

    // 先价格升序后时间顺序排列
    public static final Comparator<PcOrder4MatchBo> PRICE_ASC = (o1, o2) -> {
        int ret = o1.getPrice().compareTo(o2.getPrice());
        if (ret != 0) {
            return ret;
        } else if (o2.getOrderTime() != o1.getOrderTime()) {
            return o2.getOrderTime() > o1.getOrderTime() ? -1 : 1;
        }
        return 0;
    };

    // 先价格降序后时间顺序排列
    public static final Comparator<PcOrder4MatchBo> PRICE_DESC = (o1, o2) -> {
        int ret = o1.getPrice().compareTo(o2.getPrice());
        if (ret != 0) {
            return -ret;
        } else if (o2.getOrderTime() != o1.getOrderTime()) {
            return o2.getOrderTime() > o1.getOrderTime() ? -1 : 1;
        }
        return 0;
    };

    public PriorityQueue<PcOrder4MatchBo> limitAskQueue = new PriorityQueue<>(20000, PRICE_ASC);
    public PriorityQueue<PcOrder4MatchBo> limitBidQueue = new PriorityQueue<>(20000, PRICE_DESC);

    public Map<Long, PcOrder4MatchBo> allOpenOrders = new ConcurrentHashMap<>();

    public PriorityQueue<PcOrder4MatchBo> getOppositeQueue(int bidFlag) {
        return getQueue(PcUtil.oppositeBidFlag(bidFlag));
    }

    public PriorityQueue<PcOrder4MatchBo> getQueue(PcOrder4MatchBo order) {
        return getQueue(order.getBidFlag());
    }

    public PriorityQueue<PcOrder4MatchBo> getQueue(int bidFlag) {
        switch (bidFlag) {
            case CommonConst.BID:
                return limitBidQueue;
            case CommonConst.ASK:
                return limitAskQueue;
            default:
                return null;
        }
    }


    public void clear() {
        this.setBookUpdateList(null);
        this.setOrderNew(null);
        this.setTradeList(null);
        this.setPendingCancelIgnoreReasonNotInQueue(null);
        this.setPendingNewIgnoreReasonAlreadyCompleted(null);
        this.setPendingNewIgnoreReasonAlreadyInQueue(null);
        // 最新价格无需 重置
//        context.lastPrice = null;
    }


}