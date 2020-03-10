/**
 * @author zw
 * @date 2019/9/6
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.bo.BbTradeBo;
import com.hp.sh.expv3.match.component.notify.BbMatchMqNotify;
import com.hp.sh.expv3.match.component.notify.BbNotify;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.BbOrderTimeInForceEnum;
import com.hp.sh.expv3.match.enums.BbOrderTypeEnum;
import com.hp.sh.expv3.match.enums.EventEnum;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.msg.TradeListMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
public class BbMatchedOrderMatchedTask extends BbMatchedBaseTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<BbTradeBo> tradeList;

    // 市价订单若没有匹配完成，则撤单
    public BbOrder4MatchBo takerOrder;

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

    public List<BookMsgDto.BookEntry> bookUpdateList;

    private Long matchTxId;

    public BbOrder4MatchBo getTakerOrder() {
        return takerOrder;
    }

    public void setTakerOrder(BbOrder4MatchBo takerOrder) {
        this.takerOrder = takerOrder;
    }

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
    }

    @Autowired
    private BbMatchMqNotify bbMatchMqNotify;
    @Autowired
    private BbNotify bbNotify;

    public List<BbTradeBo> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<BbTradeBo> tradeList) {
        this.tradeList = tradeList;
    }

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

        if (BbOrderTypeEnum.LIMIT.getCode() == takerOrder.getOrderType()) {
            doLimit();
        } else if (BbOrderTypeEnum.MARKET.getCode() == takerOrder.getOrderType()) {
            doMarket();
        }
        updateSentMqOffset();
    }

    private void doLimit() {
        if (null == tradeList || tradeList.isEmpty()) {
        } else {
            bbMatchMqNotify.sendTradeBatch(this.getAsset(), this.getSymbol(), this.tradeList);
        }

        Long tkOrderId = takerOrder.getOrderId();
        Long tkAccountId = takerOrder.getAccountId();

        // 现价才会有不同的时间有效性
        if (BbOrderTimeInForceEnum.GOOD_TILL_CANCEL.getCode() == takerOrder.getTimeInForce()) {
            if (takerOrder.getFilledNumber().compareTo(BigDecimal.ZERO) == 0) {
                bbMatchMqNotify.sendOrderNotMatched(this.getAsset(), this.getSymbol(), tkAccountId, tkOrderId);
            }
        } else if (BbOrderTimeInForceEnum.IMMEDIATE_OR_CANCEL.getCode() == takerOrder.getTimeInForce()
                || BbOrderTimeInForceEnum.FILL_OR_KILL.getCode() == takerOrder.getOrderType()) {
            // 发送取消委托消息
            if (isCancelFlag()) {
                bbMatchMqNotify.sendOrderMatchCancelled(this.getAsset(), this.getSymbol(), tkAccountId, tkOrderId, cancelNumber);
            }
        } else if (BbOrderTimeInForceEnum.MAKER_ONLY.getCode() == takerOrder.getTimeInForce()) {
            if (isCancelFlag()) {
                bbMatchMqNotify.sendOrderMatchCancelled(this.getAsset(), this.getSymbol(), tkAccountId, tkOrderId, cancelNumber);
            }
        }
        sendNotifyMsg();
    }


    private void doMarket() {

        // 先发成交消息
        if (null == tradeList || tradeList.isEmpty()) {
        } else {
            bbMatchMqNotify.sendTradeBatch(this.getAsset(), this.getSymbol(), this.tradeList);
        }

        if (isCancelFlag()) {
            bbMatchMqNotify.sendOrderMatchCancelled(this.getAsset(), this.getSymbol(), takerOrder.getAccountId(), takerOrder.getOrderId(), cancelNumber);
        }

        sendNotifyMsg();
    }

    private void sendNotifyMsg() {
        sendBookMsg();
//        sendTradeMsg();
    }

    private TradeListMsgDto.TradeMsgDto buildTrade(BbTradeBo trade) {
        TradeListMsgDto.TradeMsgDto msg = new TradeListMsgDto.TradeMsgDto();
        BeanUtils.copyProperties(trade, msg);
        return msg;
    }

    @Value("${bbmatch.trade.batchSize:10}")
    private int batchSize;

    private void sendBookMsg() {
        if (null == bookUpdateList || bookUpdateList.isEmpty()) {
        } else {
            try {
                // send book
                BookMsgDto bookMsgDto = new BookMsgDto();
                bookMsgDto.setLastPrice(this.getLastPrice());
                bookMsgDto.setAsset(this.getAsset());
                bookMsgDto.setSymbol(this.getSymbol());
                bookMsgDto.setResetFlag(CommonConst.NO);
                bookMsgDto.setMsgType(EventEnum.BB_BOOK.getCode());
                bookMsgDto.setOrders(this.bookUpdateList);
                // prepared book end
                bbNotify.safeNotify(this.getAsset(), this.getSymbol(), bookMsgDto);
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
    }

    private void sendTradeMsg() {
        if (null == tradeList || tradeList.isEmpty()) {
        } else {

            TradeListMsgDto tradeListMsgDto = new TradeListMsgDto();
            tradeListMsgDto.setLastPrice(this.getLastPrice());
            tradeListMsgDto.setMatchTxId(this.getMatchTxId());
            tradeListMsgDto.setMsgType(EventEnum.BB_MATCH.getCode());

            // send match as trade
            List<TradeListMsgDto.TradeMsgDto> trades = new ArrayList<>();
            tradeList.forEach(bbMatchBo -> trades.add(buildTrade(bbMatchBo)));

            tradeListMsgDto.setTrades(trades);

            bbNotify.safeNotify(this.getAsset(), this.getSymbol(), tradeListMsgDto);
        }
    }

}