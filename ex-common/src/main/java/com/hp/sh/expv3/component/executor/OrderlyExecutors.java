package com.hp.sh.expv3.component.executor;

import java.util.ArrayList;
import java.util.List;

public class OrderlyExecutors {
	
	private List<OrderlyExecutor> list = new ArrayList<OrderlyExecutor>();
	
	public OrderlyExecutors(int count) {
		for(int i=0;i<count; i++){
			list.add(new OrderlyExecutor());
		}
	}

	public void submit(long code, Runnable task){
		int index = getIndex(code);
		OrderlyExecutor executor = list.get(index);
		executor.submit(task);
	}
	
	public int getQueueSize(long code){
		int index = this.getIndex(code);
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

	private int getIndex(long code){
		int index = (int) (code % list.size());
		return index;
	}

}
