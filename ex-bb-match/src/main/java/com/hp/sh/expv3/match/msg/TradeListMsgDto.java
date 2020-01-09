package com.hp.sh.expv3.match.msg;

import com.hp.sh.expv3.match.bo.BbTradeBo;

import java.math.BigDecimal;
import java.util.List;

public class TradeListMsgDto extends BaseMessageDto {

    private long matchTxId;
    private List<TradeMsgDto> Trades;
    private BigDecimal lastPrice;

    public long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(long matchTxId) {
        this.matchTxId = matchTxId;
    }

    public List<TradeMsgDto> getTrades() {
        return Trades;
    }

    public void setTrades(List<TradeMsgDto> trades) {
        Trades = trades;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public static class TradeMsgDto extends BbTradeBo {
    }
}
