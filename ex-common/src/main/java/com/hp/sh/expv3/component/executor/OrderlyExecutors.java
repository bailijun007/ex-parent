package com.hp.sh.expv3.component.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderlyExecutors {
	
	private final List<OrderlyExecutor> list = new ArrayList<OrderlyExecutor>();
	
	public OrderlyExecutors(int count, int queueSize) {
		for(int i=0; i<count; i++){
			list.add(new OrderlyExecutor(queueSize));
		}
	}

	public void submit(int key, Runnable task){
		int index = getIndex(key);
		OrderlyExecutor executor = list.get(index);
		executor.submit(task);
	}

	public void submit(GroupTask task){
		int index = getIndex(task.getKey());
		OrderlyExecutor executor = list.get(index);
		executor.submit(task);
	}
	
	public int getQueueSize(int key){
		int index = this.getIndex(key);
		OrderlyExecutor executor = list.get(index);
		return executor.getQueueSize();
	}
	
	public int getTaskTotal(){
		int total = 0;
		for(OrderlyExecutor executor : list){
			total += executor.getQueueSize();
		}
		return total;
	}

	public Map<Integer,Integer> getQueueSizeMap(){
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		for(int i=0;i<list.size();i++){
			OrderlyExecutor executor = list.get(i);
			map.put(i, executor.getQueueSize());
		}
		return map;
	}

	private int getIndex(int key){
		int index = key % list.size();
		return index;
	}

}
