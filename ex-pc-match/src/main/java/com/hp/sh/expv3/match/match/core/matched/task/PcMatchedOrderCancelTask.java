/**
 * @author zw
 * @date 2019/9/6
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.EventEnum;
import com.hp.sh.expv3.match.match.core.match.task.service.PcOrderBookEventService;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.msg.BookMsgDto.BookEntry;
import com.hp.sh.expv3.match.util.PcAccountContractMqNotify;
import com.hp.sh.expv3.match.util.PcNotify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Scope("prototype")
public class PcMatchedOrderCancelTask extends PcMatchedBaseTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PcAccountContractMqNotify pcOrderMqNotify;
    @Autowired
    private PcOrderBookEventService pcOrderBookEventService;
    @Autowired
    private PcNotify pcNotify;

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
        bookMsgDto.setMsgType(EventEnum.PC_BOOK.getCode());

        List<BookEntry> bookEntries = pcOrderBookEventService.buildBookEntry4Cancel(accountId, orderId);

        bookMsgDto.setOrders(bookEntries);
        // prepared book end

        pcOrderMqNotify.sendOrderMatchCancelled(this.getAsset(), this.getSymbol(), accountId, orderId, cancelDeltaAmt);

        pcNotify.safeNotify(this.getAsset(), this.getSymbol(), bookMsgDto);
        updateSentMqOffset();

    }
}