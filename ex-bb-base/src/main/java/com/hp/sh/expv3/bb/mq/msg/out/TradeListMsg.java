package com.hp.sh.expv3.bb.mq.msg.out;

import java.math.BigDecimal;
import java.util.List;

import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;

public class TradeListMsg {
	
    private int msgType = 42;
    private String messageId;

    private long matchTxId;
    private List<BBMatchedTrade> Trades;
    private BigDecimal lastPrice;

    public long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(long matchTxId) {
        this.matchTxId = matchTxId;
    }

    public List<BBMatchedTrade> getTrades() {
        return Trades;
    }

    public void setTrades(List<BBMatchedTrade> trades) {
        Trades = trades;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

}
