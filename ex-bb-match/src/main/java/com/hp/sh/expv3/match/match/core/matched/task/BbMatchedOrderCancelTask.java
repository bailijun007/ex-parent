/**
 * @author zw
 * @date 2019/9/6
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.EventEnum;
import com.hp.sh.expv3.match.match.core.match.task.service.BbOrderBookEventService;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.msg.BookMsgDto.BookEntry;
import com.hp.sh.expv3.match.component.notify.BbMatchMqNotify;
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
public class BbMatchedOrderCancelTask extends BbMatchedBaseTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BbMatchMqNotify bbOrderMqNotify;
    @Autowired
    private BbOrderBookEventService bbOrderBookEventService;
    @Autowired
    private BbNotify bbNotify;

    private long accountId;
    private long orderId;

    /**
     * 若 为 null，则全部取消
     */
    private BigDecimal cancelDeltaAmt;

    public BigDecimal getCancelDeltaAmt() {
        return cancelDeltaAmt;
    }

    public void setCancelDeltaAmt(BigDecimal cancelDeltaAmt) {
        this.cancelDeltaAmt = cancelDeltaAmt;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    @Override
    public void onSucess() {
    }

    @Override
    public void run() {

        // send book
        BookMsgDto bookMsgDto = new BookMsgDto();
        bookMsgDto.setLastPrice(this.getLastPrice());
        bookMsgDto.setAsset(this.getAsset());
        bookMsgDto.setSymbol(this.getSymbol());
        bookMsgDto.setResetFlag(CommonConst.NO);
        bookMsgDto.setMsgType(EventEnum.BOOK.getCode());

        List<BookEntry> bookEntries = bbOrderBookEventService.buildBookEntry4Cancel(accountId, orderId);

        bookMsgDto.setOrders(bookEntries);
        // prepared book end

        bbOrderMqNotify.sendOrderMatchCancelled(this.getAsset(), this.getSymbol(), accountId, orderId, cancelDeltaAmt);

        try {
            bbNotify.safeNotify(this.getAsset(), this.getSymbol(), bookMsgDto);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        updateSentMqOffset();

    }
}