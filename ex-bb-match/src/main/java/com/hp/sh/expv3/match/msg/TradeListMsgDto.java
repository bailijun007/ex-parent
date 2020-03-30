package com.hp.sh.expv3.match.msg;

import com.hp.sh.expv3.match.bo.BbMatchBo;

import java.math.BigDecimal;
import java.util.List;

public class TradeListMsgDto extends BaseMessageDto {

    private long matchTxId;
    private List<MatchMsgDto> Trades;
    private BigDecimal lastPrice;

    public long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(long matchTxId) {
        this.matchTxId = matchTxId;
    }

    public List<MatchMsgDto> getTrades() {
        return Trades;
    }

    public void setTrades(List<MatchMsgDto> trades) {
        Trades = trades;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public static class MatchMsgDto extends BbMatchBo {
    }
}
