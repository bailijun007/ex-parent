/**
 * @author 10086
 * @date 2019/12/16
 */
package com.hp.sh.expv3.match.match.core.match.vo;

import com.hp.sh.expv3.match.bo.PcTradeBo;
import com.hp.sh.expv3.match.msg.BookMsgDto;

import java.util.ArrayList;
import java.util.List;

public class PcOrderMatchResultVo {

    // book list
    private List<BookMsgDto.BookEntry> bookUpdateList = new ArrayList<>();
    // trade list
    private List<PcTradeBo> tradeList = new ArrayList<>();
    private Long matchTxId;

    public List<BookMsgDto.BookEntry> getBookUpdateList() {
        return bookUpdateList;
    }

    public void setBookUpdateList(List<BookMsgDto.BookEntry> bookUpdateList) {
        this.bookUpdateList = bookUpdateList;
    }

    public List<PcTradeBo> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<PcTradeBo> tradeList) {
        this.tradeList = tradeList;
    }

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
    }
}
