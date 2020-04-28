package com.hp.sh.expv3.bb.mq.send;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.cache.RedisPublisher;
import com.hp.sh.expv3.bb.constant.EventType;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.bb.mq.msg.out.TradeListMsg;
import com.hp.sh.expv3.bb.msg.EventMsg;


@Component
public class BBPublisher {
	private static final Logger logger = LoggerFactory.getLogger(BBPublisher.class);

	@Autowired
	private RedisPublisher publisher;
	
	public void sendEventMsg(EventMsg eventMsg) {
		String channel = this.getChannel(eventMsg);
		logger.info("publish:{},{}", channel, eventMsg);
		publisher.publish(channel, eventMsg);
	}
	
	public void send(List<BBMatchedTrade> list) {
		TradeListMsg listMsg = new TradeListMsg();
		BBMatchedTrade matchedTrade = list.get(list.size()-1);
		listMsg.setLastPrice(matchedTrade.getPrice());
		listMsg.setMatchTxId(matchedTrade.getMatchTxId());
		listMsg.setMessageId(""+matchedTrade.getId());
		listMsg.setTrades(list);
		
		this.publisher.publish(String.format("bb:trade:%s:%s", matchedTrade.getAsset(), matchedTrade.getSymbol()), listMsg);
	}

	public void send(BBMatchedTrade matchedTrade){
		TradeListMsg listMsg = new TradeListMsg();
		
		listMsg.setLastPrice(matchedTrade.getPrice());
		listMsg.setMatchTxId(matchedTrade.getMatchTxId());
		listMsg.setMessageId(""+matchedTrade.getId());
		listMsg.setTrades(Arrays.asList(matchedTrade));
		
		this.publisher.publish(String.format("bb:trade:%s:%s", matchedTrade.getAsset(), matchedTrade.getSymbol()), listMsg);
	}
	
	private String getChannel(EventMsg eventMsg){
		String channel = null;
		if(eventMsg.getType()==EventType.BB_ACCOUNT){
			channel = "bb:account:"+eventMsg.getAsset();
		}
		if(eventMsg.getType()==EventType.ORDER){
			channel = "bb:order:"+eventMsg.getAsset()+":"+eventMsg.getSymbol();
		}
		channel.toString();
		return channel;
	}
}
