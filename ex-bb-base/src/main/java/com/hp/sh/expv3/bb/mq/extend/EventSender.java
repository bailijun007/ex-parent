package com.hp.sh.expv3.bb.mq.extend;

import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.constant.MsgConstant;
import com.hp.sh.expv3.bb.mq.BaseMqSender;
import com.hp.sh.expv3.bb.msg.PcAccountLog;

/**
 * 发送事件消息
 * 
 * @author wangjg
 *
 */
@Component
public class EventSender extends BaseMqSender {

	public EventSender() {
	}

	public void sendEventMsg(PcAccountLog logMsg) {
		String topic = MsgConstant.EVENT_TOPIC;
		byte[] msgBuff = (byte[]) msgCodec.encode(logMsg);
		Message mqMsg = new Message(topic, "" + logMsg.getType(), msgBuff);
		mqMsg.setKeys(logMsg.getType() + "_" + logMsg.getRefId());
		this.send(mqMsg);
	}

}
