/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.util;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.bo.PcTradeBo;
import com.hp.sh.expv3.match.component.rocketmq.BasePcOrderProducer;
import com.hp.sh.expv3.match.config.setting.PcRocketMqSetting;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.enums.RmqTagEnum;
import com.hp.sh.expv3.match.mqmsg.*;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PcOrderMqNotify {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BasePcOrderProducer orderProducer;

    @Autowired
    private PcRocketMqSetting pcRocketMqSetting;

    public boolean sendOrderPendingNew(PcOrder4MatchBo pcOrderBo) {
        String topic = PcRocketMqUtil.buildPcOrderTopicName(pcRocketMqSetting.getPcOrderTopicNamePattern(), pcOrderBo.getAsset(), pcOrderBo.getSymbol());

        PcOrderMqMsgDto msg = new PcOrderMqMsgDto();
        msg.setAccountId(pcOrderBo.getAccountId());
        msg.setOrderId(pcOrderBo.getId());
        msg.setAsset(pcOrderBo.getAsset());
        msg.setSymbol(pcOrderBo.getSymbol());
        msg.setBidFlag(pcOrderBo.getBidFlag());
        msg.setCloseFlag(pcOrderBo.getCloseFlag());
        msg.setNumber(pcOrderBo.getNumber());
        msg.setPrice(pcOrderBo.getPrice());
        msg.setOrderType(pcOrderBo.getOrderType());
        msg.setOrderTime(pcOrderBo.getOrderTime()); //
        Message message = new Message(
                topic,// topic
                "" + RmqTagEnum.PC_ORDER_PENDING_NEW.getConstant(),// tag
                "" + pcOrderBo.getId(),
                JsonUtil.toJsonString(msg).getBytes()// body
        );
        safeSend2OrderTopic(message);
        return true;
    }

    public boolean sendOrderSnapshotTrigger(String asset, String symbol) {
        String topic = PcRocketMqUtil.buildPcOrderTopicName(pcRocketMqSetting.getPcOrderTopicNamePattern(), asset, symbol);

        PcMatchOrderSnapshotMqMsgDto msg = new PcMatchOrderSnapshotMqMsgDto();
        msg.setAsset(asset);
        msg.setSymbol(symbol);

        Message message = new Message(
                topic,// topic
                "" + RmqTagEnum.PC_MATCH_ORDER_SNAPSHOT_CREATE.getConstant(),// tag
                "" + RmqTagEnum.PC_MATCH_ORDER_SNAPSHOT_CREATE.getConstant(),// tag
                JsonUtil.toJsonString(msg).getBytes()// body
        );
        safeSend2OrderTopic(message);
        return true;
    }

    public boolean sendTrade(String asset, String symbol, List<PcTradeBo> tradeList) {
        String topic = PcRocketMqUtil.buildPcAccountContractMqTopicName(pcRocketMqSetting.getPcAccountContractTopicNamePattern(), asset, symbol);

        if (null != tradeList && !tradeList.isEmpty()) {

            Message message = new Message(
                    topic,// topic
                    "" + RmqTagEnum.PC_TRADE.getConstant(),// tag
                    "" + tradeList.get(0).getTkOrderId(),
                    JsonUtil.toJsonString(tradeList).getBytes()// body
            );
            safeSend2OrderTopic(message);
        }
        return true;
    }

    public boolean sendOrderPendingCancel(String asset, String symbol, long accountId, long orderId) {
        String topic = PcRocketMqUtil.buildPcOrderTopicName(pcRocketMqSetting.getPcOrderTopicNamePattern(), asset, symbol);

        PcOrderMqMsgDto msg = new PcOrderMqMsgDto();
        msg.setAccountId(accountId);
        msg.setOrderId(orderId);
        msg.setAsset(asset);
        msg.setSymbol(symbol);

        Message message = new Message(
                topic,// topic
                "" + RmqTagEnum.PC_ORDER_PENDING_CANCEL.getConstant(),// tag
                "" + orderId,
                JsonUtil.toJsonString(msg).getBytes()// body
        );
        safeSend2OrderTopic(message);
        return true;
    }


    public boolean sendBookReset(String asset, String symbol) {
        String topic = PcRocketMqUtil.buildPcOrderTopicName(pcRocketMqSetting.getPcOrderTopicNamePattern(), asset, symbol);

        BookResetMqMsgDto msg = new BookResetMqMsgDto(asset, symbol);

        Message message = new Message(
                topic,// topic
                "" + RmqTagEnum.PC_BOOK_RESET.getConstant(),// tag
                "" + RmqTagEnum.PC_BOOK_RESET.getConstant(),// tag
                JsonUtil.toJsonString(msg).getBytes()// body
        );
        safeSend2OrderTopic(message);
        return true;
    }

    public boolean sendPosLocked(PcPosLockedMqMsgDto msg) {
        String topic = PcRocketMqUtil.buildPcOrderTopicName(pcRocketMqSetting.getPcOrderTopicNamePattern(), msg.getAsset(), msg.getSymbol());

        Message message = new Message(
                topic,// topic
                "" + RmqTagEnum.PC_POS_LIQ_LOCKED.getConstant(),// tag
                "" + msg.getId(),
                JsonUtil.toJsonString(msg).getBytes()// body
        );
        safeSend2OrderTopic(message);
        return true;
    }

    private boolean safeSend2OrderTopic(Message message) {
        while (true) {
            try {
                SendResult sr = orderProducer.send(message,
                        (mqs, msg1, arg) -> mqs.get(0),
                        5000L);
//                if (sr.getSendStatus().equals(SendStatus.SEND_OK))
                break;
            } catch (Exception e) {
            }
        }
        return true;
    }


}