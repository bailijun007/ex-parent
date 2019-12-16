/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.component.notify;

import com.hp.sh.expv3.match.bo.PcTradeBo;
import com.hp.sh.expv3.match.component.rocketmq.PcMatchProducer;
import com.hp.sh.expv3.match.config.setting.PcmatchRocketMqSetting;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.RmqTagEnum;
import com.hp.sh.expv3.match.mqmsg.PcOrderCancelMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.PcOrderMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.PcOrderTradeMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.PcPosLockedMqMsgDto;
import com.hp.sh.expv3.match.util.JsonUtil;
import com.hp.sh.expv3.match.util.PcRocketMqUtil;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
public class PcMatchMqNotify {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PcMatchProducer pcmatchProducer;

    @Autowired
    private PcmatchRocketMqSetting pcmatchRocketMqSetting;

    @Deprecated
    public boolean sendOrderMatched(String asset, String symbol, List<PcTradeBo> tradeList) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcmatchRocketMqSetting.getPcMatchTopicNamePattern(), asset, symbol);
        if (null != tradeList && !tradeList.isEmpty()) {

            Message message = new Message(
                    topic,// topic
                    "" + RmqTagEnum.PC_MATCH_ORDER_MATCHED.getConstant(),// tag
                    "" + tradeList.get(0).getTkOrderId(),
                    JsonUtil.toJsonString(tradeList).getBytes()// body
            );
            safeSend2MatchTopic(message, tradeList.get(0).getTkAccountId());

            if (logger.isDebugEnabled()) {
                logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(tradeList));
            }
        }
        return true;
    }

    public boolean sendOrderNotMatched(String asset, String symbol, long accountId, long orderId) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcmatchRocketMqSetting.getPcMatchTopicNamePattern(), asset, symbol);
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
        safeSend2MatchTopic(message, accountId);

        if (logger.isDebugEnabled()) {
            logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
        }
        return true;
    }

    public boolean sendOrderMatchCancelled(String asset, String symbol, long accountId, long orderId, BigDecimal cancelDeltaAmt) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcmatchRocketMqSetting.getPcMatchTopicNamePattern(), asset, symbol);
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
        safeSend2MatchTopic(message, accountId);

        if (logger.isDebugEnabled()) {
            logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
        }
        return true;
    }

    public boolean sendSameSideCloseOrderCancelled(String asset, String symbol, Collection<PcOrderCancelMqMsgDto> msgs) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcmatchRocketMqSetting.getPcMatchTopicNamePattern(), asset, symbol);

        if (null == msgs || msgs.isEmpty()) {
        } else {
            for (PcOrderCancelMqMsgDto msg : msgs) {

                Message message = new Message(
                        topic,// topic
                        "" + RmqTagEnum.PC_MATCH_SAME_SIDE_CLOSE_ORDER_CANCELLED.getConstant(),// tag
                        "" + msg.getOrderId(),
                        JsonUtil.toJsonString(msg).getBytes()// body
                );
                safeSend2MatchTopic(message, msg.getAccountId());

                if (logger.isDebugEnabled()) {
                    logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
                }
            }
        }
        return true;
    }

    public boolean sendTrade(String asset, String symbol, List<PcTradeBo> tradeList) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcmatchRocketMqSetting.getPcMatchTopicNamePattern(), asset, symbol);

        for (PcTradeBo trade : tradeList) {

            PcOrderTradeMqMsgDto maker = new PcOrderTradeMqMsgDto();

            maker.setMakerFlag(CommonConst.MAKER);
            maker.setAccountId(trade.getMkAccountId());
            maker.setNumber(trade.getNumber());
            maker.setAsset(trade.getAsset());
            maker.setMatchTxId(trade.getMatchTxId());
            maker.setOrderId(trade.getMkOrderId());
            maker.setPrice(trade.getPrice());
            maker.setSymbol(trade.getSymbol());
            maker.setTradeTime(trade.getTradeTime());
            maker.setOpponentOrderId(trade.getTkOrderId()); // 对手委托ID
            maker.setTradeId(trade.getId());
            Message makerMsg = new Message(
                    topic,// topic
                    "" + RmqTagEnum.PC_TRADE.getConstant(),// tag
                    "" + maker.getOrderId(),
                    JsonUtil.toJsonString(maker).getBytes()// body
            );
            safeSend2MatchTopic(makerMsg, maker.getAccountId());

            if (logger.isDebugEnabled()) {
                logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, makerMsg.getTopic(), makerMsg.getTags(), makerMsg.getKeys(), JsonUtil.toJsonString(maker));
            }

            PcOrderTradeMqMsgDto taker = new PcOrderTradeMqMsgDto();
            taker.setMakerFlag(CommonConst.TAKER);
            taker.setAccountId(trade.getTkAccountId());
            taker.setNumber(trade.getNumber());
            taker.setAsset(trade.getAsset());
            taker.setMatchTxId(trade.getMatchTxId());
            taker.setOrderId(trade.getTkOrderId());
            taker.setPrice(trade.getPrice());
            taker.setSymbol(trade.getSymbol());
            taker.setTradeTime(trade.getTradeTime());
            taker.setOpponentOrderId(trade.getMkOrderId()); // 对手委托ID
            taker.setTradeId(trade.getId());
            Message takerMsg = new Message(
                    topic,// topic
                    "" + RmqTagEnum.PC_TRADE.getConstant(),// tag
                    "" + taker.getOrderId(),
                    JsonUtil.toJsonString(taker).getBytes()                    // body
            );
            safeSend2MatchTopic(takerMsg, taker.getAccountId());
            if (logger.isDebugEnabled()) {
                logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, takerMsg.getTopic(), takerMsg.getTags(), takerMsg.getKeys(), JsonUtil.toJsonString(taker));
            }
        }
        return true;
    }

    public boolean sendSameSideCloseOrderAllCancelled(String asset, String symbol, PcPosLockedMqMsgDto msg) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcmatchRocketMqSetting.getPcMatchTopicNamePattern(), asset, symbol);

        Message message = new Message(
                topic,// topic
                "" + RmqTagEnum.PC_MATCH_SAME_SIDE_CLOSE_ORDER_ALL_CANCELLED.getConstant(),// tag
                "" + msg.getId(), // pos id
                JsonUtil.toJsonString(msg).getBytes()// body
        );
        safeSend2MatchTopic(message, msg.getAccountId());

        if (logger.isDebugEnabled()) {
            logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
        }
        return true;
    }

    private boolean safeSend2MatchTopic(Message message, long accountId) {
        while (true) {
            try {
                pcmatchProducer.send(message,
                        (mqs, msg1, arg) -> mqs.get(Math.abs(Long.valueOf(accountId).intValue()) % mqs.size()),
                        0L);
                break;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return true;
    }

}