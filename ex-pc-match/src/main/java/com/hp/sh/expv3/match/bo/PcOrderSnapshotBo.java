/**
 * @author 10086
 * @date 2019/10/25
 */
package com.hp.sh.expv3.match.bo;

import java.math.BigDecimal;
import java.util.List;

public class PcOrderSnapshotBo {

    private List<PcOrder4MatchBo> limitAskOrders;
    private List<PcOrder4MatchBo> limitBidOrders;

    private BigDecimal lastPrice;
    private Long rmqCurrentOffset;

    public Long getRmqCurrentOffset() {
        return rmqCurrentOffset;
    }

    public void setRmqCurrentOffset(Long rmqCurrentOffset) {
        this.rmqCurrentOffset = rmqCurrentOffset;
    }

    public List<PcOrder4MatchBo> getLimitAskOrders() {
        return limitAskOrders;
    }

    public void setLimitAskOrders(List<PcOrder4MatchBo> limitAskOrders) {
        this.limitAskOrders = limitAskOrders;
    }

    public List<PcOrder4MatchBo> getLimitBidOrders() {
        return limitBidOrders;
    }

    public void setLimitBidOrders(List<PcOrder4MatchBo> limitBidOrders) {
        this.limitBidOrders = limitBidOrders;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

}