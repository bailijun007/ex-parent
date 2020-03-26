package com.hp.sh.expv3.pc.mq.starter;

import java.util.ArrayList;
import java.util.List;

import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.common.message.MessageQueue;

public class PcAllocateMessageQueueStrategy implements AllocateMessageQueueStrategy {

	@Override
	public List<MessageQueue> allocate(String consumerGroup, String currentCID, List<MessageQueue> mqAll,
			List<String> cidAll) {
		List<MessageQueue> list = new ArrayList<MessageQueue>();
		list.add(mqAll.get(0));
		list.add(mqAll.get(1));
		list.add(mqAll.get(2));
		list.add(mqAll.get(3));
		list.add(mqAll.get(4));
		return list;
	}

	@Override
	public String getName() {
		return "pc";
	}

}
