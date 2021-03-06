/**
 * @author 10086
 * @date 2019/10/25
 */
package com.hp.sh.expv3.match.bo;

import java.math.BigDecimal;
import java.util.List;

public class BbOrderSnapshotBo {

    private List<BbOrder4MatchBo> limitAskOrders;
    private List<BbOrder4MatchBo> limitBidOrders;

    private BigDecimal lastPrice;
    private Long rmqCurrentOffset;
    private String rmqCurrentMsgId;

    public String getRmqCurrentMsgId() {
        return rmqCurrentMsgId;
    }

    public void setRmqCurrentMsgId(String rmqCurrentMsgId) {
        this.rmqCurrentMsgId = rmqCurrentMsgId;
    }

    public Long getRmqCurrentOffset() {
        return rmqCurrentOffset;
    }

    public void setRmqCurrentOffset(Long rmqCurrentOffset) {
        this.rmqCurrentOffset = rmqCurrentOffset;
    }

    public List<BbOrder4MatchBo> getLimitAskOrders() {
        return limitAskOrders;
    }

    public void setLimitAskOrders(List<BbOrder4MatchBo> limitAskOrders) {
        this.limitAskOrders = limitAskOrders;
    }

    public List<BbOrder4MatchBo> getLimitBidOrders() {
        return limitBidOrders;
    }

    public void setLimitBidOrders(List<BbOrder4MatchBo> limitBidOrders) {
        this.limitBidOrders = limitBidOrders;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

}