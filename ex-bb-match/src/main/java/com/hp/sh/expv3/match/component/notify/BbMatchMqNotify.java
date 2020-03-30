/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.component.notify;

import com.hp.sh.expv3.match.bo.BbMatchBo;
import com.hp.sh.expv3.match.component.id.SnowflakeIdWorker;
import com.hp.sh.expv3.match.component.rocketmq.BbMatchProducer;
import com.hp.sh.expv3.match.config.setting.BbmatchRocketMqSetting;
import com.hp.sh.expv3.match.config.setting.BbmatchSetting;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.RmqTagEnum;
import com.hp.sh.expv3.match.mqmsg.BbOrderCancelMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.BbOrderNotMatchedMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.BbTradeMqMsgDto;
import com.hp.sh.expv3.match.util.BbRocketMqUtil;
import com.hp.sh.expv3.match.util.JsonUtil;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BbMatchMqNotify {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BbMatchProducer bbmatchProducer;

    @Autowired
    private BbmatchRocketMqSetting bbmatchRocketMqSetting;
    @Autowired
    private BbmatchSetting bbmatchSetting;

    public boolean sendOrderNotMatched(String asset, String symbol, long accountId, long orderId) {
        String topic = BbRocketMqUtil.buildBbAccountContractMqTopicName(bbmatchRocketMqSetting.getBbMatchTopicNamePattern(), asset, symbol);
        BbOrderNotMatchedMqMsgDto msg = new BbOrderNotMatchedMqMsgDto();
        msg.setAccountId(accountId);
        msg.setOrderId(orderId);
        msg.setAsset(asset);
        msg.setSymbol(symbol);

        Message message = buildMessage(
                topic,// topic
                "" + RmqTagEnum.BB_MATCH_ORDER_NOT_MATCHED.getConstant(),// tag
                "" + orderId,
                msg// body
        );
//        SendResult send =
        safeSend2MatchTopic(message, orderId);

        if (logger.isDebugEnabled()) {
            logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
        }
        return true;
    }

    public boolean sendOrderMatchCancelled(String asset, String symbol, long accountId, long orderId, BigDecimal cancelDeltaAmt) {
        String topic = BbRocketMqUtil.buildBbAccountContractMqTopicName(bbmatchRocketMqSetting.getBbMatchTopicNamePattern(), asset, symbol);
        BbOrderCancelMqMsgDto msg = new BbOrderCancelMqMsgDto();
        msg.setAccountId(accountId);
        msg.setOrderId(orderId);
        msg.setAsset(asset);
        msg.setSymbol(symbol);
        msg.setCancelNumber(cancelDeltaAmt);
        Message message = buildMessage(
                topic,// topic
                "" + RmqTagEnum.BB_MATCH_ORDER_CANCELLED.getConstant(),// tag
                "" + orderId,
                msg // body
        );
        safeSend2MatchTopic(message, orderId);

        if (logger.isDebugEnabled()) {
            logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
        }
        return true;
    }

    public boolean sendTrade(String asset, String symbol, List<BbMatchBo> tradeList) {
        String topic = BbRocketMqUtil.buildBbAccountContractMqTopicName(bbmatchRocketMqSetting.getBbMatchTopicNamePattern(), asset, symbol);
        for (BbMatchBo trade : tradeList) {

            BbTradeMqMsgDto maker = new BbTradeMqMsgDto();

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
                    "" + RmqTagEnum.BB_TRADE.getConstant(),// tag
                    "" + maker.getOrderId(),
                    maker// body
            );
            safeSend2MatchTopic(makerMsg, maker.getOrderId());

            if (logger.isDebugEnabled()) {
                logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, makerMsg.getTopic(), makerMsg.getTags(), makerMsg.getKeys(), JsonUtil.toJsonString(maker));
            }

            BbTradeMqMsgDto taker = new BbTradeMqMsgDto();
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
                    "" + RmqTagEnum.BB_TRADE.getConstant(),// tag
                    "" + taker.getOrderId(),
                    taker                // body
            );
            safeSend2MatchTopic(takerMsg, taker.getOrderId());
            if (logger.isDebugEnabled()) {
                logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, takerMsg.getTopic(), takerMsg.getTags(), takerMsg.getKeys(), JsonUtil.toJsonString(taker));
            }
        }
        return true;
    }

    private Message buildMessage(String topic, String tags, String keys, Object o) {
        // todo bb 同一类topic 可内聚
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

    private boolean safeSend2MatchTopic(Message message, long objectId) {
        boolean first = true;
        while (true) {
            try {
                // 下单，撤单，未成交，都要顺序执行
                bbmatchProducer.send(message,
                        (mqs, msg1, id) -> mqs.get(Math.abs(Long.valueOf(SnowflakeIdWorker.getTimeInMs((Long) id)).intValue()) % mqs.size()),
                        objectId);
                first = false;
                break;
            } catch (Exception e) {
                if (first) {
                    logger.error(e.getMessage(), e);
                } else {
                    logger.error(e.getMessage());
                }
            }
        }
        return true;
    }

}