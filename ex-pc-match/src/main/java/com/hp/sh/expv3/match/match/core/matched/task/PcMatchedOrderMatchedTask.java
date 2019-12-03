/**
 * @author zw
 * @date 2019/9/6
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.hp.sh.expv3.match.bo.PcOrderNotMatchedBo;
import com.hp.sh.expv3.match.bo.PcTradeBo;
import com.hp.sh.expv3.match.component.notify.PcMatchMqNotify;
import com.hp.sh.expv3.match.component.notify.PcNotify;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.EventEnum;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.msg.TradeListMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
public class PcMatchedOrderMatchedTask extends PcMatchedBaseTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<PcTradeBo> tradeList;
    public PcOrderNotMatchedBo notMatchedTakerOrder;

    public List<BookMsgDto.BookEntry> bookUpdateList;

    private Long matchTxId;

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
    }

    @Autowired
    private PcMatchMqNotify pcMatchMqNotify;
    @Autowired
    private PcNotify pcNotify;

    public List<PcTradeBo> getTradeList() {
        return tradeList;
    }

    public void setTradeList(List<PcTradeBo> tradeList) {
        this.tradeList = tradeList;
    }

    public List<BookMsgDto.BookEntry> getBookUpdateList() {
        return bookUpdateList;
    }

    public void setBookUpdateList(List<BookMsgDto.BookEntry> bookUpdateList) {
        this.bookUpdateList = bookUpdateList;
    }

    public PcOrderNotMatchedBo getNotMatchedTakerOrder() {
        return notMatchedTakerOrder;
    }

    public void setNotMatchedTakerOrder(PcOrderNotMatchedBo notMatchedTakerOrder) {
        this.notMatchedTakerOrder = notMatchedTakerOrder;
    }

    @Override
    public void onSucess() {
    }

    @Override
    public void run() {

        /**
         * new:
         * (matchList | orderNew ) , bookUpdateList,bookResetFlag
         */

        if (null == notMatchedTakerOrder && (null == tradeList || tradeList.isEmpty())) {
            throw new RuntimeException();
        }

        if (null != notMatchedTakerOrder) {
            // 未匹配的委托
            pcMatchMqNotify.sendOrderNotMatched(this.getAsset(), this.getSymbol(), notMatchedTakerOrder.getAccountId(), notMatchedTakerOrder.getOrderId());
        } else {
            if (null != tradeList && (!tradeList.isEmpty())) {
//                long takerAccountId = tradeList.get(0).getTkOrderId();
//                long takerOrderId = tradeList.get(0).getTkOrderId();
//
//                List<PcTradeBo> existMatches = pcTradeReadService.listByTakerOrderId(this.getAsset(), this.getSymbol(), takerAccountId, takerOrderId);
//
//                if (null == existMatches || existMatches.isEmpty()) {
//                    pcTradeDao.insert(this.getAsset(), this.getSymbol(), tradeList);
//                } else {
//                    // 使用上次的matchTxId覆盖
//                    Long existMatchTxId = existMatches.get(0).getMatchTxId();
//                    for (PcTradeBo pcMatchBo : tradeList) {
//                        pcMatchBo.setMatchTxId(existMatchTxId);
//                    }
//                    Map<Long, PcTradeBo> id2Match = existMatches.stream().collect(Collectors.toMap(PcTradeBo::getMkOrderId, Function.identity()));
//                    for (PcTradeBo match : tradeList) {
//                        if (!id2Match.containsKey(match.getMkOrderId())) {
//                            pcTradeDao.insert(match);
//                        }
//                    }
//                }
//                pcAccountContractMqNotify.sendOrderMatched(this.getAsset(), this.getSymbol(), tradeList);
//                pcNotify.safeNotify(this.getAsset(), this.getSymbol(), this.getLastPrice());

                pcMatchMqNotify.sendTrade(this.getAsset(), this.getSymbol(), this.tradeList);
            }
        }

        // send book
        BookMsgDto bookMsgDto = new BookMsgDto();
        bookMsgDto.setLastPrice(this.getLastPrice());
        bookMsgDto.setAsset(this.getAsset());
        bookMsgDto.setSymbol(this.getSymbol());
        bookMsgDto.setResetFlag(CommonConst.NO);
        bookMsgDto.setMsgType(EventEnum.PC_BOOK.getCode());
        bookMsgDto.setOrders(this.bookUpdateList);
        // prepared book end

        TradeListMsgDto tradeListMsgDto = new TradeListMsgDto();
        tradeListMsgDto.setLastPrice(this.getLastPrice());
        tradeListMsgDto.setMatchTxId(this.getMatchTxId());
        tradeListMsgDto.setMsgType(EventEnum.PC_TRADE.getCode());
        if (null != tradeList && (!tradeList.isEmpty())) {
            // send match as trade
            List<TradeListMsgDto.TradeMsgDto> trades = new ArrayList<>();
            for (PcTradeBo pcMatchBo : tradeList) {
                trades.add(buildTrade(pcMatchBo));
            }
            tradeListMsgDto.setTrades(trades);
        }

        pcNotify.safeNotify(this.getAsset(), this.getSymbol(), bookMsgDto);
        if (null == tradeListMsgDto.getTrades() || tradeListMsgDto.getTrades().isEmpty()) {
        } else {
            pcNotify.safeNotify(this.getAsset(), this.getSymbol(), tradeListMsgDto);
        }

        updateSentMqOffset();

    }

    private TradeListMsgDto.TradeMsgDto buildTrade(PcTradeBo trade) {
        TradeListMsgDto.TradeMsgDto msg = new TradeListMsgDto.TradeMsgDto();
        BeanUtils.copyProperties(trade, msg);
        return msg;
    }

}