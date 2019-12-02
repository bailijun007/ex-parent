package com.hp.sh.expv3.match.msg;

import java.math.BigDecimal;
import java.util.List;

/**
 * 如：
 * ETH_BTC,price:100 表示 1 BTC = 100 ETH
 * volume 是 成交中 ETH 的 成交数量
 * amt  是 成交中 BTC 的 成交金额
 */
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

    public static class TradeMsgDto
            // extends PcTradeBo
    {
    }
}
