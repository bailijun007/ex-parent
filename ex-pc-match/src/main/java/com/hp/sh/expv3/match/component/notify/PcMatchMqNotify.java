/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.component.notify;

import com.google.common.collect.Lists;
import com.hp.sh.expv3.match.bo.PcTradeBo;
import com.hp.sh.expv3.match.component.rocketmq.PcMatchProducer;
import com.hp.sh.expv3.match.config.setting.PcmatchRocketMqSetting;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.RmqTagEnum;
import com.hp.sh.expv3.match.mqmsg.*;
import com.hp.sh.expv3.match.util.BeanCopyUtil;
import com.hp.sh.expv3.match.util.JsonUtil;
import com.hp.sh.expv3.match.util.PcRocketMqUtil;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

            Message message = buildMessage(
                    topic,// topic
                    "" + RmqTagEnum.PC_MATCH_ORDER_MATCHED.getConstant(),// tag
                    "" + tradeList.get(0).getTkOrderId(),
                    tradeList// body
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

        Message message = buildMessage(
                topic,// topic
                "" + RmqTagEnum.PC_MATCH_ORDER_NOT_MATCHED.getConstant(),// tag
                "" + orderId,
                msg// body
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
        Message message = buildMessage(
                topic,// topic
                "" + RmqTagEnum.PC_MATCH_ORDER_CANCELLED.getConstant(),// tag
                "" + orderId,
                msg // body
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

                Message message = buildMessage(
                        topic,// topic
                        "" + RmqTagEnum.PC_MATCH_SAME_SIDE_CLOSE_ORDER_CANCELLED.getConstant(),// tag
                        "" + msg.getOrderId(),
                        msg// body
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
            Message makerMsg = buildMessage(
                    topic,// topic
                    "" + RmqTagEnum.PC_TRADE.getConstant(),// tag
                    "" + maker.getOrderId(),
                    maker// body
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
            Message takerMsg = buildMessage(
                    topic,// topic
                    "" + RmqTagEnum.PC_TRADE.getConstant(),// tag
                    "" + taker.getOrderId(),
                    taker                // body
            );
            safeSend2MatchTopic(takerMsg, taker.getAccountId());
            if (logger.isDebugEnabled()) {
                logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, takerMsg.getTopic(), takerMsg.getTags(), takerMsg.getKeys(), JsonUtil.toJsonString(taker));
            }
        }
        return true;
    }

    public boolean sendSameSideCloseOrderAllCancelled(String asset, String symbol, PcPosLockedMqMsgDto msg, List<PcOrderCancelMqMsgDto> cancelMqMsgs) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcmatchRocketMqSetting.getPcMatchTopicNamePattern(), asset, symbol);

        PcOrderSameSideCancelled4PosLockMqMsgDto dto = BeanCopyUtil.copy(msg);

        List<List<PcOrderCancelMqMsgDto>> partitions = Lists.partition(Optional.ofNullable(cancelMqMsgs).orElse(new ArrayList<>()), 10);

        for (int i = 0; i < partitions.size(); i++) {
            if (i == partitions.size() - 1) {
                dto.setLastFlag(CommonConst.YES);
            } else {
                dto.setLastFlag(CommonConst.NO);
            }
            dto.setCancelOrders(partitions.get(i));
            Message message = buildMessage(
                    topic,// topic
                    "" + RmqTagEnum.PC_MATCH_SAME_SIDE_CLOSE_ORDER_ALL_CANCELLED.getConstant(),// tag
                    "" + dto.getPosId(), // pos id
                    dto// body
            );
            safeSend2MatchTopic(message, dto.getAccountId());
            if (logger.isDebugEnabled()) {
                logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
            }
        }
        return true;
    }

    private Message buildMessage(String topic, String tags, String keys, Object o) {
        try {
            return new Message(
                    topic,// topic
                    tags,// tag
                    keys, // pos id
                    JsonUtil.toJsonString(o).getBytes(RemotingHelper.DEFAULT_CHARSET)// body
            );
        } catch (UnsupportedEncodingException e) {
        }
        return null;
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