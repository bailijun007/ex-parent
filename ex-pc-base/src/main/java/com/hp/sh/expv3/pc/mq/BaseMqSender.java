package com.hp.sh.expv3.pc.mq;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.hp.sh.expv3.pc.constant.MqTopic;
import com.hp.sh.expv3.pc.mq.starter.OrderMessageQueueSelector;
import com.hp.sh.expv3.pc.msg.BaseSymbolMsg;
import com.hp.sh.rocketmq.codec.MsgCodec;

public class BaseMqSender {
	private static final Logger logger = LoggerFactory.getLogger(BaseMqSender.class);
	
	@Autowired
	protected MQProducer producer;
	
	@Autowired
	protected MsgCodec msgCodec;
    
	public BaseMqSender() {
	}
	
	protected void sendOrderMsg(BaseSymbolMsg msg, String tags, String keys){
		String topic = this.getTopic(msg);
		Message mqMsg = new Message(topic, tags, encodeMsg2(msg));
		mqMsg.setKeys(keys);
	    this.send(mqMsg);
	}
	
	private byte[] encodeMsg(Object msg){
		byte[] msgBuff = (byte[]) msgCodec.encode(msg);
		return msgBuff;
	}
	
	//去掉000
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	private byte[] encodeMsg2(Object msg){
		Map msgMap = BeanHelper.beanToMap(msg);
		Set<Map.Entry> entrySet = msgMap.entrySet();
		for (Map.Entry entry : entrySet){
			Object val = entry.getValue();
			if(val instanceof BigDecimal){
				BigDecimal bd = (BigDecimal)val;
				String strBd = bd.stripTrailingZeros().toPlainString();
				entry.setValue(strBd);
			}
		}
		
		byte[] msgBuff = (byte[]) msgCodec.encode(msgMap);
		
		return msgBuff;
	}

	protected void send(Message mqMsg) {
		int n = 0;
		while(true){
			try {
		        SendResult sendResult = producer.send(mqMsg, new OrderMessageQueueSelector(), 0);
		        logger.info("sendMsg:{}->{}->{}->{},{}", mqMsg.getTags(), mqMsg.getTopic(), mqMsg.getKeys(),(n++) ,sendResult.toString());
		        break;
			} catch (Exception e) {
				logger.error("发送失败:{}-{}-{}", mqMsg.getTopic(), mqMsg.getTags(), mqMsg.getKeys());
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	protected String getTopic(BaseSymbolMsg msg){
		String asset = msg.getAsset();
		String symbol = msg.getSymbol();
		return MqTopic.getOrderTopic(asset, symbol);
	}
    
}
