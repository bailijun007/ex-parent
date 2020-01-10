/**
 * @author 10086
 * @date 2019/12/16
 */
package com.hp.sh.expv3.match.match.core.match.vo;

import com.hp.sh.expv3.match.bo.BbTradeBo;
import com.hp.sh.expv3.match.msg.BookMsgDto;

import java.math.BigDecimal;
import java.util.List;

public class BbOrderMatchResultVo {

    // book list
    private List<BookMsgDto.BookEntry> bookUpdateList;
    // trade list
    private List<BbTradeBo> tradeList;
    private Long matchTxId;

    private boolean cancelFlag;
    private BigDecimal cancelNumber;

    public boolean isCancelFlag() {
        return cancelFlag;
    }

    public void setCancelFlag(boolean cancelFlag) {
        this.cancelFlag = cancelFlag;
    }

    public BigDecimal getCancelNumber() {
        return cancelNumber;
    }

    public void setCancelNumber(BigDecimal cancelNumber) {
        this.cancelNumber = cancelNumber;
    }

    public List<BookMsgDto.BookEntry> getBookUpdateList() {
        return bookUpdateList;
    }

    public void setBookUpdateList(List<BookMsgDto.BookEntry> bookUpdateList) {
        this.bookUpdateList = bookUpdateList;
    }

    public List<BbTradeBo> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<BbTradeBo> tradeList) {
        this.tradeList = tradeList;
    }

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
    }
}
