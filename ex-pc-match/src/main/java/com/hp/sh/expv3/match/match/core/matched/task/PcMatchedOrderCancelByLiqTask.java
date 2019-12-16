/**
 * @author zw
 * @date 2019/9/6
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.hp.sh.expv3.match.component.notify.PcMatchMqNotify;
import com.hp.sh.expv3.match.component.notify.PcNotify;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.EventEnum;
import com.hp.sh.expv3.match.mqmsg.PcOrderCancelMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.msg.BookMsgDto.BookEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class PcMatchedOrderCancelByLiqTask extends PcMatchedBaseTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PcMatchMqNotify pcOrderMqNotify;
    @Autowired
    private PcNotify pcNotify;

    private List<PcOrderCancelMqMsgDto> cancelMqMsgs;

    public List<PcOrderCancelMqMsgDto> getCancelMqMsgs() {
        return cancelMqMsgs;
    }

    public void setCancelMqMsgs(List<PcOrderCancelMqMsgDto> cancelMqMsgs) {
        this.cancelMqMsgs = cancelMqMsgs;
    }

    private PcPosLockedMqMsgDto msg;

    public PcPosLockedMqMsgDto getMsg() {
        return msg;
    }

    public void setMsg(PcPosLockedMqMsgDto msg) {
        this.msg = msg;
    }

    private List<BookEntry> bookEntries;

    public List<BookEntry> getBookEntries() {
        return bookEntries;
    }

    public void setBookEntries(List<BookEntry> bookEntries) {
        this.bookEntries = bookEntries;
    }

    @Override
    public void onSucess() {
    }

    @Override
    public void run() {

        if (null == cancelMqMsgs || cancelMqMsgs.isEmpty()) {
        } else {

            // send book
            BookMsgDto bookMsgDto = new BookMsgDto();
            bookMsgDto.setLastPrice(this.getLastPrice());
            bookMsgDto.setAsset(this.getAsset());
            bookMsgDto.setSymbol(this.getSymbol());
            bookMsgDto.setResetFlag(CommonConst.NO);
            bookMsgDto.setMsgType(EventEnum.PC_BOOK.getCode());

            bookMsgDto.setOrders(bookEntries);
            // prepared book end
            pcOrderMqNotify.sendSameSideCloseOrderCancelled(this.getAsset(), this.getSymbol(), cancelMqMsgs);
            pcNotify.safeNotify(this.getAsset(), this.getSymbol(), bookMsgDto);
        }

        // 不管是否有委托被取消，都要发送此消息，以便后续执行强平操作
        pcOrderMqNotify.sendSameSideCloseOrderAllCancelled(this.getAsset(), this.getSymbol(), msg);

        updateSentMqOffset();

    }
}
