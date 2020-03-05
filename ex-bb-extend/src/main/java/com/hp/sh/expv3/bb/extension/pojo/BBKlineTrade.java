package com.hp.sh.expv3.bb.extension.pojo;

import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/5
 */
public class BBKlineTrade implements Serializable {

    private int msgType;

    private  Long messageId;

    private  Long matchTxId;

    private BigDecimal lastPrice;

    private List<BbTradeVo> kLineTrades;

    public BBKlineTrade() {
    }

    @Override
    public String toString() {
        return "BBKlineTrade{" +
                "msgType=" + msgType +
                ", messageId=" + messageId +
                ", matchTxId=" + matchTxId +
                ", lastPrice=" + lastPrice +
                ", kLineTrades=" + kLineTrades +
                '}';
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public List<BbTradeVo> getkLineTrades() {
        return kLineTrades;
    }

    public void setkLineTrades(List<BbTradeVo> kLineTrades) {
        this.kLineTrades = kLineTrades;
    }
}
