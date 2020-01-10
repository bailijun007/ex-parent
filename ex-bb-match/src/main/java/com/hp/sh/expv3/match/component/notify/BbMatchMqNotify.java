/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.component.notify;

import com.hp.sh.expv3.match.bo.BbTradeBo;
import com.hp.sh.expv3.match.component.rocketmq.BbMatchProducer;
import com.hp.sh.expv3.match.config.setting.BbmatchRocketMqSetting;
import com.hp.sh.expv3.match.enums.RmqTagEnum;
import com.hp.sh.expv3.match.mqmsg.BbOrderCancelMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.BbOrderMqMsgDto;
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

    @Deprecated
    public boolean sendOrderMatched(String asset, String symbol, List<BbTradeBo> tradeList) {
        String topic = BbRocketMqUtil.buildBbAccountContractMqTopicName(bbmatchRocketMqSetting.getBbMatchTopicNamePattern(), asset, symbol);
        if (null != tradeList && !tradeList.isEmpty()) {

            Message message = buildMessage(
                    topic,// topic
                    "" + RmqTagEnum.MATCH_ORDER_MATCHED.getConstant(),// tag
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
        String topic = BbRocketMqUtil.buildBbAccountContractMqTopicName(bbmatchRocketMqSetting.getBbMatchTopicNamePattern(), asset, symbol);
        BbOrderMqMsgDto msg = new BbOrderMqMsgDto();
        msg.setAccountId(accountId);
        msg.setOrderId(orderId);
        msg.setAsset(asset);
        msg.setSymbol(symbol);

        Message message = buildMessage(
                topic,// topic
                "" + RmqTagEnum.MATCH_ORDER_NOT_MATCHED.getConstant(),// tag
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
        String topic = BbRocketMqUtil.buildBbAccountContractMqTopicName(bbmatchRocketMqSetting.getBbMatchTopicNamePattern(), asset, symbol);
        BbOrderCancelMqMsgDto msg = new BbOrderCancelMqMsgDto();
        msg.setAccountId(accountId);
        msg.setOrderId(orderId);
        msg.setAsset(asset);
        msg.setSymbol(symbol);
        msg.setCancelNumber(cancelDeltaAmt);
        Message message = buildMessage(
                topic,// topic
                "" + RmqTagEnum.MATCH_ORDER_CANCELLED.getConstant(),// tag
                "" + orderId,
                msg // body
        );
        safeSend2MatchTopic(message, accountId);

        if (logger.isDebugEnabled()) {
            logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, message.getTopic(), message.getTags(), message.getKeys(), JsonUtil.toJsonString(msg));
        }
        return true;
    }

    public boolean sendTrade(String asset, String symbol, List<BbTradeBo> tradeList) {
        String topic = BbRocketMqUtil.buildBbAccountContractMqTopicName(bbmatchRocketMqSetting.getBbMatchTopicNamePattern(), asset, symbol);

        for (BbTradeBo trade : tradeList) {
            // TODO zw
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
                bbmatchProducer.send(message,
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