/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.util;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.bo.PcTradeBo;
import com.hp.sh.expv3.match.component.rocketmq.BasePcAccountContractProducer;
import com.hp.sh.expv3.match.config.setting.PcRocketMqSetting;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.RmqTagEnum;
import com.hp.sh.expv3.match.mqmsg.PcOrderCancelMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.PcOrderMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.PcOrderTradeMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
public class PcAccountContractMqNotify {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BasePcAccountContractProducer accountContractProducer;

    @Autowired
    private PcRocketMqSetting pcRocketMqSetting;


    public boolean sendOrderNotMatched(String asset, String symbol, long accountId, long orderId) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcRocketMqSetting.getPcAccountContractTopicNamePattern(), asset, symbol);
        PcOrderMqMsgDto msg = new PcOrderMqMsgDto();
        msg.setAccountId(accountId);
        msg.setOrderId(orderId);
        msg.setAsset(asset);
        msg.setSymbol(symbol);

        Message message = new Message(
                topic,// topic
                "" + RmqTagEnum.PC_MATCH_ORDER_NOT_MATCHED.getConstant(),// tag
                "" + orderId,
                JsonUtil.toJsonString(msg).getBytes()// body
        );
//        SendResult send =
        safeSend2AccountContractTopic(message, accountId);
        return true;
    }

    public boolean sendOrderMatched(String asset, String symbol, List<PcTradeBo> tradeList) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcRocketMqSetting.getPcAccountContractTopicNamePattern(), asset, symbol);

        for (PcTradeBo trade : tradeList) {

            PcOrderTradeMqMsgDto maker = new PcOrderTradeMqMsgDto();

            maker.setMakerFlag(CommonConst.MAKER);
            maker.setAccountId(trade.getMkAccountId());
            maker.setAsset(trade.getAsset());
            maker.setMatchTxId(trade.getMatchTxId());
            maker.setOrderId(trade.getMkOrderId());
            maker.setPrice(trade.getPrice());
            maker.setSymbol(trade.getSymbol());
            maker.setTradeTime(trade.getTradeTime());
            maker.setNumber(trade.getNumber());
            maker.setTradeId(trade.getId());
            Message makerMsg = new Message(
                    topic,// topic
                    "" + RmqTagEnum.PC_MATCH_ORDER_MATCHED.getConstant(),// tag
                    "" + maker.getOrderId(),
                    JsonUtil.toJsonString(maker).getBytes()// body
            );
            safeSend2AccountContractTopic(makerMsg, maker.getAccountId());

            PcOrderTradeMqMsgDto taker = new PcOrderTradeMqMsgDto();
            taker.setMakerFlag(CommonConst.TAKER);
            taker.setAccountId(trade.getTkAccountId());
            taker.setAsset(trade.getAsset());
            taker.setMatchTxId(trade.getMatchTxId());
            taker.setOrderId(trade.getTkOrderId());
            taker.setPrice(trade.getPrice());
            taker.setSymbol(trade.getSymbol());
            taker.setTradeTime(trade.getTradeTime());
            taker.setNumber(trade.getNumber());
            taker.setTradeId(trade.getId());

            Message takerMsg = new Message(
                    topic,// topic
                    "" + RmqTagEnum.PC_MATCH_ORDER_MATCHED.getConstant(),// tag
                    "" + taker.getOrderId(),
                    JsonUtil.toJsonString(taker).getBytes()                    // body
            );
            safeSend2AccountContractTopic(takerMsg, taker.getAccountId());
        }
        return true;
    }

    public boolean sendOrderMatchCancelled(String asset, String symbol, long accountId, long orderId, BigDecimal cancelDeltaAmt) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcRocketMqSetting.getPcAccountContractTopicNamePattern(), asset, symbol);

        PcOrderCancelMqMsgDto msg = new PcOrderCancelMqMsgDto();
        msg.setAccountId(accountId);
        msg.setOrderId(orderId);
        msg.setAsset(asset);
        msg.setSymbol(symbol);
        msg.setCancelNumber(cancelDeltaAmt);

        Message message = new Message(
                topic,// topic
                "" + RmqTagEnum.PC_MATCH_ORDER_CANCELLED.getConstant(),// tag
                "" + orderId,
                JsonUtil.toJsonString(msg).getBytes()// body
        );
        safeSend2AccountContractTopic(message, accountId);
        return true;
    }

    public boolean sendSameSideOrderCancelled(String asset, String symbol, Collection<PcOrder4MatchBo> orders) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcRocketMqSetting.getPcAccountContractTopicNamePattern(), asset, symbol);

        if (null == orders || orders.isEmpty()) {
        } else {
            for (PcOrder4MatchBo order : orders) {

                PcOrderCancelMqMsgDto msg = new PcOrderCancelMqMsgDto();
                msg.setAccountId(order.getAccountId());
                msg.setOrderId(order.getId());
                msg.setAsset(asset);
                msg.setSymbol(symbol);
                BigDecimal cancelDeltaAmt = order.getNumber().subtract(order.getFilledNumber());
                msg.setCancelNumber(cancelDeltaAmt);
                Message message = new Message(
                        topic,// topic
                        "" + RmqTagEnum.PC_MATCH_ORDER_CANCELLED.getConstant(),// tag
                        "" + order.getId(),
                        JsonUtil.toJsonString(msg).getBytes()// body
                );
                safeSend2AccountContractTopic(message, order.getAccountId());
            }
        }
        return true;
    }

    public boolean sendSameSideOrderAllCancelled(String asset, String symbol, PcPosLockedMqMsgDto msg) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcRocketMqSetting.getPcAccountContractTopicNamePattern(), asset, symbol);

        Message message = new Message(
                topic,// topic
                "" + RmqTagEnum.PC_SAME_SIDE_ORDER_ALL_CANCELLED.getConstant(),// tag
                "" + msg.getId(), // pos id
                JsonUtil.toJsonString(msg).getBytes()// body
        );
        safeSend2AccountContractTopic(message, msg.getAccountId());

        return true;
    }

    private boolean safeSend2AccountContractTopic(Message message, long accountId) {
        while (true) {
            try {
                accountContractProducer.send(message,
                        (mqs, msg1, arg) -> mqs.get(Long.valueOf(accountId).intValue() % mqs.size()),
                        5000L);
                break;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return true;
    }

}