/**
 * @author zw
 * @date 2019/9/6
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.EventEnum;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.component.notify.BbNotify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Scope("prototype")
public class BbMatchedBookResetTask extends BbMatchedBaseTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<BookMsgDto.BookEntry> bookUpdateList;
    public BigDecimal lastPrice;

    @Override
    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    @Override
    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    @Autowired
    private BbNotify bbNotify;

    public List<BookMsgDto.BookEntry> getBookUpdateList() {
        return bookUpdateList;
    }

    public void setBookUpdateList(List<BookMsgDto.BookEntry> bookUpdateList) {
        this.bookUpdateList = bookUpdateList;
    }

    @Override
    public void onSucess() {
    }

    @Override
    public void run() {

        // book reset 无需 作为 snapshot 使用，职责单一化
        BookMsgDto msg = new BookMsgDto();
        msg.setSymbol(this.getSymbol());
        msg.setAsset(this.getAsset());
        msg.setLastPrice(lastPrice);
        msg.setResetFlag(CommonConst.YES);
        msg.setMsgType(EventEnum.BOOK.getCode());
        msg.setOrders(this.bookUpdateList);

        bbNotify.safeNotify(this.getAsset(), this.getSymbol(), msg);

        updateSentMqOffset();
    }

}