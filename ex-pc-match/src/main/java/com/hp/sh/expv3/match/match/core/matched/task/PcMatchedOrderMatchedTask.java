/**
 * @author zw
 * @date 2019/9/6
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import com.google.common.collect.Lists;
import com.hp.sh.expv3.match.bo.PcOrder2CancelBo;
import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.bo.PcOrderNotMatchedBo;
import com.hp.sh.expv3.match.bo.PcTradeBo;
import com.hp.sh.expv3.match.component.notify.PcMatchMqNotify;
import com.hp.sh.expv3.match.component.notify.PcNotify;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.EventEnum;
import com.hp.sh.expv3.match.enums.PcOrderTypeEnum;
import com.hp.sh.expv3.match.msg.BookMsgDto;
import com.hp.sh.expv3.match.msg.TradeListMsgDto;
import com.hp.sh.expv3.pc.module.trade.dao.PcTradeDAO;
import com.hp.sh.expv3.pc.module.trade.entity.PcTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Scope("prototype")
public class PcMatchedOrderMatchedTask extends PcMatchedBaseTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<PcTradeBo> tradeList;

    public PcOrder4MatchBo takerOrder;
    public PcOrderNotMatchedBo notMatchedTakerOrder;
    // 市价订单若没有匹配完成，则撤单
    public PcOrder2CancelBo cancelTakerOrder;

    public List<BookMsgDto.BookEntry> bookUpdateList;

    private Long matchTxId;

    public PcOrder4MatchBo getTakerOrder() {
        return takerOrder;
    }

    public void setTakerOrder(PcOrder4MatchBo takerOrder) {
        this.takerOrder = takerOrder;
    }

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
    }

    public PcOrder2CancelBo getCancelTakerOrder() {
        return cancelTakerOrder;
    }

    public void setCancelTakerOrder(PcOrder2CancelBo cancelTakerOrder) {
        this.cancelTakerOrder = cancelTakerOrder;
    }

    @Autowired
    private PcTradeDAO pcTradeDAO;
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

        if (PcOrderTypeEnum.LIMIT.getCode() == takerOrder.getOrderType()) {
            doLimit();
        } else if (PcOrderTypeEnum.MARKET.getCode() == takerOrder.getOrderType()) {
            doMarket();
        }
        updateSentMqOffset();
    }

    private void doLimit() {
        if (null == notMatchedTakerOrder && (null == tradeList || tradeList.isEmpty())) {
            throw new RuntimeException();
        }
        if (null != notMatchedTakerOrder) {
            // 未匹配的委托
            pcMatchMqNotify.sendOrderNotMatched(this.getAsset(), this.getSymbol(), notMatchedTakerOrder.getAccountId(), notMatchedTakerOrder.getOrderId());
        } else {
            if (null != tradeList && (!tradeList.isEmpty())) {
                List<PcTradeBo> notSavedTrade = filterNotSavedTrade(this.tradeList);
                if (null != notSavedTrade && !notSavedTrade.isEmpty()) {
                    saveTradeList(notSavedTrade);
                }
                pcMatchMqNotify.sendTrade(this.getAsset(), this.getSymbol(), this.tradeList);
            }
        }
        sendBookMsg();
        sendTradeMsg();
    }

    private void doMarket() {
        if (null == cancelTakerOrder && (null == tradeList || tradeList.isEmpty())) {
            throw new RuntimeException();
        }

        // 先发成交消息
        if (null != tradeList && (!tradeList.isEmpty())) {
            List<PcTradeBo> notSavedTrade = filterNotSavedTrade(this.tradeList);
            if (null != notSavedTrade && !notSavedTrade.isEmpty()) {
                saveTradeList(notSavedTrade);
            }
            pcMatchMqNotify.sendTrade(this.getAsset(), this.getSymbol(), this.tradeList);
        }

        // 后发取消消息
        if (null != cancelTakerOrder && cancelTakerOrder.getCancelNumber().compareTo(BigDecimal.ZERO) > 0) {
            // 未匹配的市价委托
            pcMatchMqNotify.sendOrderMatchCancelled(this.getAsset(), this.getSymbol(), cancelTakerOrder.getAccountId(), cancelTakerOrder.getOrderId(), cancelTakerOrder.getCancelNumber());
        }
        sendBookMsg();
        sendTradeMsg();
    }

    private TradeListMsgDto.TradeMsgDto buildTrade(PcTradeBo trade) {
        TradeListMsgDto.TradeMsgDto msg = new TradeListMsgDto.TradeMsgDto();
        BeanUtils.copyProperties(trade, msg);
        return msg;
    }

    @Value("${pcmatch.trade.batchSize:10}")
    private int batchSize;

    private void saveTradeList(List<PcTradeBo> tradeList) {
        List<List<PcTradeBo>> batchTrade = Lists.partition(tradeList, batchSize);
        for (List<PcTradeBo> trades : batchTrade) {
            pcTradeDAO.batchSave(trades);
        }
    }

    /**
     * 查询匹配结果是否已经入库，若已经入库，则使用已入库的记录的matchId 作为本批次入库的matchId。同时返回未入库的成交记录
     *
     * @param tradeList
     * @return
     */
    private List<PcTradeBo> filterNotSavedTrade(List<PcTradeBo> tradeList) {
        long takerAccountId = tradeList.get(0).getTkOrderId();
        long takerOrderId = tradeList.get(0).getTkOrderId();

        List<PcTrade> existMatches = pcTradeDAO.queryList(new HashMap<String, Object>() {
            {
                put("tkAccountId", takerAccountId);
                put("tkOrderId", takerOrderId);
            }
        });

        List<PcTradeBo> needSave;

        if (null == existMatches || existMatches.isEmpty()) {
            needSave = tradeList;
        } else {
            // 使用上次的matchTxId覆盖
            Long existMatchTxId = existMatches.get(0).getMatchTxId();
            for (PcTradeBo pcMatchBo : tradeList) {
                pcMatchBo.setMatchTxId(existMatchTxId);
            }
            needSave = new ArrayList<>(tradeList.size());
            Map<Long, PcTrade> id2Match = existMatches.stream().collect(Collectors.toMap(PcTrade::getMkOrderId, Function.identity()));
            for (PcTradeBo match : tradeList) {
                if (!id2Match.containsKey(match.getMkOrderId())) {
                    needSave.add(match);
                }
            }
        }
        return needSave;
    }

    private void sendBookMsg() {
        // send book
        BookMsgDto bookMsgDto = new BookMsgDto();
        bookMsgDto.setLastPrice(this.getLastPrice());
        bookMsgDto.setAsset(this.getAsset());
        bookMsgDto.setSymbol(this.getSymbol());
        bookMsgDto.setResetFlag(CommonConst.NO);
        bookMsgDto.setMsgType(EventEnum.PC_BOOK.getCode());
        bookMsgDto.setOrders(this.bookUpdateList);
        // prepared book end
        pcNotify.safeNotify(this.getAsset(), this.getSymbol(), bookMsgDto);
    }

    private void sendTradeMsg() {
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
        if (null == tradeListMsgDto.getTrades() || tradeListMsgDto.getTrades().isEmpty()) {
        } else {
            pcNotify.safeNotify(this.getAsset(), this.getSymbol(), tradeListMsgDto);
        }
    }

}