/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.component.notify;

import com.hp.sh.expv3.match.bo.BbTradeBo;
import com.hp.sh.expv3.match.component.id.SnowflakeIdWorker;
import com.hp.sh.expv3.match.component.rocketmq.BbMatchProducer;
import com.hp.sh.expv3.match.config.setting.BbmatchRocketMqSetting;
import com.hp.sh.expv3.match.enums.RmqTagEnum;
import com.hp.sh.expv3.match.mqmsg.BbOrderCancelMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.BbOrderMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.BbOrderNotMatchedMqMsgDto;
import com.hp.sh.expv3.match.mqmsg.BbTradeMqMsgDto;
import com.hp.sh.expv3.match.util.BbRocketMqUtil;
import com.hp.sh.expv3.match.util.JsonUtil;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
                "" + RmqTagEnum.BB_MATCH_ORDER_CANCELLED.getConstant(),// tag
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

        if (null == tradeList || tradeList.isEmpty()) {
        } else {
            String topic = BbRocketMqUtil.buildBbAccountContractMqTopicName(bbmatchRocketMqSetting.getBbMatchTopicNamePattern(), asset, symbol);

            for (BbTradeBo bbTradeBo : tradeList) {

                BbTradeMqMsgDto msgDto = new BbTradeMqMsgDto();
                BeanUtils.copyProperties(bbTradeBo, msgDto);

                Message msg = buildMessage(
                        topic,// topic
                        "" + RmqTagEnum.BB_MATCH_ORDER_MATCHED.getConstant(),// tag
                        "" + msgDto.getTkOrderId(),
                        msgDto// body
                );
                safeSend2MatchTopic(msg, msgDto.getTkAccountId());

                if (logger.isDebugEnabled()) {
                    logger.debug("{} {} topic:{} tag:{},keys:{} {}", asset, symbol, msg.getTopic(), msg.getTags(), msg.getKeys(), JsonUtil.toJsonString(msg));
                }
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

    private boolean safeSend2MatchTopic(Message message, long accountId) {
        boolean first = true;
        while (true) {
            try {
                bbmatchProducer.send(message,
                        (mqs, msg1, arg) -> mqs.get(Math.abs(Long.valueOf(SnowflakeIdWorker.getTimeInMs(accountId)).intValue()) % mqs.size()),
                        accountId);
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