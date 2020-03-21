/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.match.thread;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.match.core.match.vo.BbOrderMatchResultVo;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import com.hp.sh.expv3.match.util.BbUtil;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class BbMatchHandlerContext {

    public Long lastBookResetTimeInMs = null;

    public IThreadWorker matchedThreadWorker;

    private BbOrderMatchResultVo matchResult = new BbOrderMatchResultVo();

    public BbOrderMatchResultVo getMatchResult() {
        return matchResult;
    }

    // last price
    private BigDecimal lastPrice;
    private Long sentMqOffset;
    private String assetSymbol;
    private String asset;
    private String symbol;

    public Long getSentMqOffset() {
        return sentMqOffset;
    }

    public void setSentMqOffset(Long sentMqOffset) {
        this.sentMqOffset = sentMqOffset;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
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

    static ThreadLocal<BbMatchHandlerContext> threadLocal = ThreadLocal.withInitial(() -> new BbMatchHandlerContext());

    public static BbMatchHandlerContext getLocalContext() {
        return threadLocal.get();
    }

    // 先价格升序后时间顺序排列
    public static final Comparator<BbOrder4MatchBo> PRICE_ASC = (o1, o2) -> {
        int ret = o1.getPrice().compareTo(o2.getPrice());
        if (ret != 0) {
            return ret;
        } else if (o2.getOrderTime() != o1.getOrderTime()) {
            return o2.getOrderTime() > o1.getOrderTime() ? -1 : 1;
        }
        return 0;
    };

    // 先价格降序后时间顺序排列
    public static final Comparator<BbOrder4MatchBo> PRICE_DESC = (o1, o2) -> {
        int ret = o1.getPrice().compareTo(o2.getPrice());
        if (ret != 0) {
            return -ret;
        } else if (o2.getOrderTime() != o1.getOrderTime()) {
            return o2.getOrderTime() > o1.getOrderTime() ? -1 : 1;
        }
        return 0;
    };

    public PriorityQueue<BbOrder4MatchBo> limitAskQueue = new PriorityQueue<>(20000, PRICE_ASC);
    public PriorityQueue<BbOrder4MatchBo> limitBidQueue = new PriorityQueue<>(20000, PRICE_DESC);

    public Map<Long, BbOrder4MatchBo> allOpenOrders = new ConcurrentHashMap<>();

    public PriorityQueue<BbOrder4MatchBo> getOppositeQueue(int bidFlag) {
        return getQueue(BbUtil.oppositeBidFlag(bidFlag));
    }

    public PriorityQueue<BbOrder4MatchBo> getQueue(BbOrder4MatchBo order) {
        return getQueue(order.getBidFlag());
    }

    public PriorityQueue<BbOrder4MatchBo> getQueue(int bidFlag) {
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
        this.getMatchResult().setBookUpdateList(null);
        this.getMatchResult().setTradeList(null);
        this.getMatchResult().setMatchTxId(null);
        this.getMatchResult().setCancelFlag(false);
        this.getMatchResult().setCancelNumber(null);
    }

}