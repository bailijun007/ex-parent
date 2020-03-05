package com.hp.sh.expv3.component.executor;

import java.util.ArrayList;
import java.util.List;

public class OrderlyExecutors {
	
	private List<OrderlyExecutor> list = new ArrayList<OrderlyExecutor>();
	
	public OrderlyExecutors(int count, int queueSize) {
		for(int i=0;i<count; i++){
			list.add(new OrderlyExecutor(queueSize));
		}
	}

	public void submit(int key, Runnable task){
		int index = getIndex(key);
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

	private int getIndex(int key){
		int index = key % list.size();
		return index;
	}

}
